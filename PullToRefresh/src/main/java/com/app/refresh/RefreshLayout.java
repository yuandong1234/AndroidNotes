package com.app.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.app.refresh.header.DefaultLoadingHeader;
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
    private float MOVE_FACTOR = 0.25f;
    private Scroller scroller;
    private int touchSlop;//最小view滚动距离

    private boolean loadOnce;
    private boolean enableRefresh = true;
    private boolean enableLoadMore = true;
    private boolean canScroll;
    private float mLastY;


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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "view onMeasure ...");
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "view onLayout ...");
        int height = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == header) {
                headerHeight = child.getMeasuredHeight();
                Log.e(TAG, "headerHeight : " + headerHeight);
                child.layout(0, -headerHeight, child.getMeasuredWidth(), 0);
            } else {
                contentView = child;
                child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
                height += child.getMeasuredHeight();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("dispatchTouchEvent ...");
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
                if (distance > 0) {
                    intercept = canPullDown();
                } else if (distance < 0) {
                    intercept = canPullUp();
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("onInterceptTouchEvent : up");
                intercept = false;
                break;
        }
        mLastY = currentY;
        Log.e(TAG, "intercept " + intercept);
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
                scrollBy(0, -offset);
                break;
            case MotionEvent.ACTION_UP:
                canScroll = true;
                int scrollY = getScrollY();
                System.out.println("scrollY :" + scrollY);
                scroller.startScroll(0, scrollY, 0, -scrollY, 500);
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

    public LoadingLayout getHeader() {
        if (header == null) {
            header = new DefaultLoadingHeader(getContext());
        }
        return header;
    }

    public void setHeader(LoadingLayout header) {
        this.header = header;
    }

    private boolean canPullDown() {
        boolean enable = false;
        if (contentView instanceof ScrollView) {
            if (contentView.getScrollY() <= 0) {
                enable = true;
            }
        }
        return enable;
    }

    private boolean canPullUp() {
        boolean enable = false;
        if (contentView instanceof ScrollView) {
            if (contentView.getScrollY() >= (((ScrollView) contentView).getChildAt(0).getHeight() - contentView.getHeight())) {
                enable = true;
            }
        }
        return enable;
    }
}
