package com.app.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.app.refresh.footer.DefaultLoadingFooter;
import com.app.refresh.header.DefaultLoadingHeader;
import com.app.refresh.intercept.ViewInterceptManager;
import com.app.refresh.internal.LoadingLayout;
import com.app.refresh.listener.RefreshListener;

/**
 * Created by yuandong
 * on 2019/3/21 20:44.
 */
public class RefreshLayout extends ViewGroup {
    private static String TAG = RefreshLayout.class.getSimpleName();

    private LoadingLayout header;
    private LoadingLayout footer;
    private View contentView;

    private int headerHeight;
    private int footerHeight;

    private State currentState;
    private RefreshListener listener;

    private static final int SCROLL_SPEED = 500;
    private float MOVE_FACTOR = 0.3f;
    private Scroller scroller;
    private int touchSlop;//最小view滚动距离

    private boolean loadOnce;
    private boolean enableRefresh = true;
    private boolean enableLoadMore = true;
    private boolean canScroll;
    private float mLastY;

    private boolean isRefreshing, isLoading;
    private int REFRESH_COUNT;
    private int LOADING_COUNT;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "view init finished ...");
        if (getChildCount() > 1) throw new RuntimeException("Only one child view can be included!");
        addHeader();
        addFooter();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "view onMeasure ...");
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "view onLayout ...");
        if (!loadOnce) {
            int height = 0;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child == header) {
                    headerHeight = child.getMeasuredHeight();
                    Log.i(TAG, "headerHeight : " + headerHeight);
                    child.layout(0, -headerHeight, child.getMeasuredWidth(), 0);
                } else if (child == footer) {
                    footerHeight = child.getMeasuredHeight();
                    child.layout(0, height, child.getMeasuredWidth(), height + footerHeight);
                    Log.i(TAG, "footerHeight : " + footerHeight);
                } else {
                    contentView = child;
                    child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
                    height += child.getMeasuredHeight();
                }
            }
            loadOnce = true;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("dispatchTouchEvent ...");
//        if (currentState == State.STATUS_REFRESHING || currentState == State.STATUS_LOADING) {
//            return false;
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float currentY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("onInterceptTouchEvent : down");
                mLastY = currentY;
                intercept = false;
                canScroll = false;//当按下的时候，要停止当前的滑动
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("onInterceptTouchEvent : move");
                float distance = currentY - mLastY;
                if (distance > 0) {//pull down
                    intercept = ViewInterceptManager.canPullDown(contentView, touchSlop, distance);
                } else if (distance < 0) {//pull up
                    intercept = ViewInterceptManager.canPullUp(contentView, touchSlop, distance, getMeasuredHeight());
                } else {
                    intercept = false;
                }
                if (currentState == State.STATUS_REFRESHING || currentState == State.STATUS_LOADING) {//正在刷新或加载的时候拦截
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("onInterceptTouchEvent : up");
                intercept = false;
                break;
        }
        mLastY = currentY;
        Log.i(TAG, "intercept " + intercept);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float nowY = event.getY();
                float deltaY = nowY - mLastY;
                int offset = (int) (deltaY * MOVE_FACTOR);
                System.out.println("deltaY :" + deltaY + " offset :" + offset);

                if (getScrollY() < 0) {//下拉
                    if (getScrollY() <= -headerHeight) {
                        //释放立即刷新
                        currentState = State.STATUS_RELEASE_TO_REFRESH;
                        header.setState(State.STATUS_RELEASE_TO_REFRESH);
                    } else {
                        //下拉可以刷新
                        currentState = State.STATUS_PULL_TO_REFRESH;
                        header.setState(State.STATUS_PULL_TO_REFRESH);
                        isRefreshing = false;
                    }
                }

                if (getScrollY() > 0) {//上拉
                    if (getScrollY() >= footerHeight) {
                        //释放立即加载
                        currentState = State.STATUS_RELEASE_TO_LOADING;
                        footer.setState(State.STATUS_RELEASE_TO_LOADING);
                    } else {
                        //上拉刷新
                        currentState = State.STATUS_PULL_TO_LOADING;
                        footer.setState(State.STATUS_PULL_TO_LOADING);
                        isLoading = false;
                    }
                }
                scrollBy(0, -offset);
                break;
            case MotionEvent.ACTION_UP:
                canScroll = true;
                int scrollY = getScrollY();
                if (scrollY <= -headerHeight) {
                    //正在刷新
                    currentState = State.STATUS_REFRESHING;
                    header.setState(State.STATUS_REFRESHING);
                    scroller.startScroll(0, getScrollY(), 0, -(scrollY + headerHeight), SCROLL_SPEED);
                    if (!isRefreshing) {
                        isRefreshing = true;
                        REFRESH_COUNT++;
                        Log.i(TAG, " REFRESH_COUNT : " + REFRESH_COUNT);
                        if (listener != null) {
                            listener.onRefresh();
                        }
                    }
                } else if (scrollY >= footerHeight) {
                    //正在加载
                    currentState = State.STATUS_LOADING;
                    footer.setState(State.STATUS_LOADING);
                    scroller.startScroll(0, getScrollY(), 0, -(scrollY - footerHeight), SCROLL_SPEED);
                    if (!isLoading) {
                        isLoading = true;
                        LOADING_COUNT++;
                        Log.i(TAG, " LOADING_COUNT : " + LOADING_COUNT);
                        if (listener != null) {
                            listener.onLoadMore();
                        }
                    }
                } else {
                    scroller.startScroll(0, scrollY, 0, -scrollY, SCROLL_SPEED);
                }
                invalidate();
                break;
        }
        mLastY = y;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller == null) {
            return;
        }
        if (scroller.computeScrollOffset() && canScroll) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private void addHeader() {
        addView(getHeader());
    }

    private void addFooter() {
        addView(getFooter());
    }

    public LoadingLayout getHeader() {
        if (header == null) {
            header = new DefaultLoadingHeader(getContext());
        }
        return header;
    }

    public void setHeader(LoadingLayout header) {
        this.header = header;
    }

    public LoadingLayout getFooter() {
        if (footer == null) {
            footer = new DefaultLoadingFooter(getContext());
        }
        return footer;
    }

    public void setFooter(LoadingLayout footer) {
        this.footer = footer;
    }

    public void onRefreshComplete() {
        Log.i(TAG, "onRefreshComplete ......");
        REFRESH_COUNT--;
        if (REFRESH_COUNT > 0) return;
        //刷新完毕
        Log.i(TAG, "The view refresh complete");
        currentState = State.STATUS_REFRESH_FINISHED;
        header.setState(State.STATUS_REFRESH_FINISHED);
        isRefreshing = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                invalidate();
            }
        }, 300);
    }

    public void onLoadMoreComplete() {
        Log.i(TAG, "onLoadMoreComplete ......");
        LOADING_COUNT--;
        if (LOADING_COUNT > 0) return;
        //加载完毕
        Log.i(TAG, "The view load more  complete");
        currentState = State.STATUS_REFRESH_FINISHED;
        footer.setState(State.STATUS_REFRESH_FINISHED);
        isLoading = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                invalidate();
            }
        }, 300);
    }

    public RefreshListener getListener() {
        return listener;
    }

    public void setListener(RefreshListener listener) {
        this.listener = listener;
    }
}
