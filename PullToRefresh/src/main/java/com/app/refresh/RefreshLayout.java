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

    private static final int SCROLL_SPEED = 300;
    private float MOVE_FACTOR = 0.3f;
    private Scroller scroller;
    private int touchSlop;//最小view滚动距离

    private boolean enableRefresh = true;
    private boolean enableLoadMore = true;
    private boolean canScroll;
    private float mLastY;
    private float mInterceptLastY;
    //private float mInterceptLastX;

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
        Log.e(TAG, "touchSlop : " + touchSlop);
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
        int height = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == header) {
                headerHeight = child.getMeasuredHeight();
                Log.i(TAG, "headerHeight : " + headerHeight);
                if (enableRefresh) {
                    child.layout(0, -headerHeight, child.getMeasuredWidth(), 0);
                } else {
                    child.layout(0, 0, 0, 0);
                }
            } else if (child == footer) {
                footerHeight = child.getMeasuredHeight();
                Log.i(TAG, "footerHeight : " + footerHeight);
                if (enableLoadMore) {
                    child.layout(0, height, child.getMeasuredWidth(), height + footerHeight);
                } else {
                    child.layout(0, 0, 0, 0);
                }
            } else {
                contentView = child;
                child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
                height += child.getMeasuredHeight();
            }
        }
    }

    private float dispatchLastY;
    private boolean isMoving;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Log.e(TAG, "dispatchTouchEvent ...");
//        if (currentState == State.STATUS_REFRESHING || currentState == State.STATUS_LOADING) {
//            return false;
//        }
        float currentY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMoving = false;
                Log.e(TAG, "dispatchTouchEvent : down");
                dispatchLastY = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "dispatchTouchEvent : move");
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "dispatchTouchEvent : up");
                //mLastY = 0;
                isMoving = false;
                break;
        }

        dispatchLastY = currentY;
        return super.dispatchTouchEvent(ev);

        //return onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e(TAG, "onInterceptTouchEvent");
        float currentY = ev.getY();
        boolean intercept = false;
        Log.e(TAG, " currentY : " + currentY);
        Log.e(TAG, " onInterceptTouchEvent action  : " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onInterceptTouchEvent : down");
                canScroll = false;//当按下的时候，要停止当前的滑动
                if (scroller != null && !scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                //mLastY = currentY;
                mInterceptLastY = currentY;
                //mInterceptLastX = currentX;
                Log.e(TAG, "mInterceptLastY : " + mInterceptLastY);
                intercept = false;
                isMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent : move");
                Log.e(TAG, " currentY : " + currentY + "  mInterceptLastY : " + mInterceptLastY);
                float distance = currentY - mInterceptLastY;
                Log.e(TAG, "onInterceptTouchEvent : move ： " + distance);
                int scrollY = getScrollY();
                Log.e(TAG, "ScrollY : " + scrollY);

                //TODO 有问题
                if (distance > 0) {//下拉
                    Log.e(TAG, "--------------> pull down <-------------- ");
                    intercept = ViewInterceptManager.canPullDown(contentView, touchSlop, distance);
                } else if (distance < 0) {//上拉
                    Log.e(TAG, "--------------> pull up <-------------- ");
                    intercept = ViewInterceptManager.canPullUp(contentView, touchSlop, distance, getMeasuredHeight());
                } else {
                    intercept = false;
                }

                //TODO 当viewGroup 有竖直方向滑动时，禁止contentView滑动
                if (scrollY != 0) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onInterceptTouchEvent : up");
                intercept = false;
                canScroll = true;
                isMoving = false;
                //防止 下拉或上拉滑动一段距离，突然松手，然后再次迅速触摸屏幕且迅速松开，导致卡住的问题
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                postInvalidate();
                break;
        }
        mInterceptLastY = currentY;
        mLastY = currentY;
        Log.i(TAG, "intercept " + intercept);
        Log.i(TAG, "-------------------------------------------- ");
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "currentState : " + currentState);
//        if (currentState == State.STATUS_REFRESHING || currentState == State.STATUS_LOADING) {//正在刷新或加载的时候拦截
//            return true;
//        }
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouchEvent : down");
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onTouchEvent : move");
                float nowY = event.getY();
                float deltaY = nowY - mLastY;
                int offset = (int) (deltaY * MOVE_FACTOR);
                Log.e(TAG, "deltaY :" + deltaY + " offset :" + offset);
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

                //TODO 下拉过程中，先拉下头部，手不离开屏幕再上拉，尾部会显示出来
                boolean isCanScroll = true;
                if (deltaY > 0) {//下拉
                    isCanScroll = ViewInterceptManager.canPullDown(contentView, touchSlop, offset);
                    if (getScrollY() > 0) {
                        isCanScroll = true;
                    }
                } else if (deltaY < 0) {//上拉
                    isCanScroll = ViewInterceptManager.canPullUp(contentView, touchSlop, offset, getMeasuredHeight());
                    if (getScrollY() < 0) {//如果刷新头部显示，允许上拉
                        isCanScroll = true;
                    }
                }
                Log.e(TAG, "isCanScroll :" + isCanScroll);
                if (isCanScroll) {
                    isMoving = false;
                    scrollBy(0, -offset);
                } else {
                    isMoving = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent : up");
                canScroll = true;
                int scrollY = getScrollY();
                if (scrollY < -headerHeight && enableRefresh) {
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
                } else if (scrollY > footerHeight && enableLoadMore) {
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
                postInvalidate();
                break;
        }
        mLastY = y;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
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
        Log.e(TAG, "onRefreshComplete ...... REFRESH_COUNT : " + REFRESH_COUNT + "  isRefreshing : " + isRefreshing + "  canScroll  :" + canScroll);
        REFRESH_COUNT--;
        if (REFRESH_COUNT > 0) return;
        if (!isRefreshing) return;
        //刷新完毕
        Log.i(TAG, "The view refresh complete");
        isRefreshing = false;
        header.setState(State.STATUS_REFRESH_FINISHED);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                postInvalidate();
                currentState = State.STATUS_REFRESH_FINISHED;
            }
        }, 300);
    }

    public void onLoadMoreComplete() {
        Log.e(TAG, "onLoadMoreComplete ...... LOADING_COUNT : " + LOADING_COUNT + " isLoading : " + isLoading);
        LOADING_COUNT--;
        if (LOADING_COUNT > 0) return;
        if (!isLoading) return;
        //加载完毕
        Log.i(TAG, "The view load more  complete");
        isLoading = false;
        footer.setState(State.STATUS_REFRESH_FINISHED);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                postInvalidate();
                currentState = State.STATUS_REFRESH_FINISHED;
            }
        }, 300);
    }

    public RefreshListener getListener() {
        return listener;
    }

    public void setListener(RefreshListener listener) {
        this.listener = listener;
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
        requestLayout();
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
        requestLayout();
    }
}
