package com.yuong.notes.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 滑动冲突目前只适配ScrollView
 */
public class FlexibleLayout extends ViewGroup {

    private static String TAG = FlexibleLayout.class.getSimpleName();

    private float MOVE_FACTOR = 0.25f;
    private View mContentView;
    private Scroller mScroller;
    private Context mContext;
    private float mLastY;
    private boolean canScroll;

    public FlexibleLayout(Context context) {
        this(context, null);
    }

    public FlexibleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    private void init() {
        mScroller = new Scroller(mContext);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) throw new RuntimeException("Only one child view can be included!");

        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = 0;
        int count = getChildCount();
        Log.i(TAG, "ChildCount : " + count);
//        for (int i = 0; i < count; i++) {
//            child = getChildAt(i);
//            child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
//            height += child.getMeasuredHeight();
//        }
        View child = getChildAt(0);
        child.layout(0, height, child.getMeasuredWidth(), height + child.getMeasuredHeight());
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
                scrollBy(0, -offset);
                break;
            case MotionEvent.ACTION_UP:
                canScroll = true;
                int scrollY = getScrollY();
                System.out.println("scrollY :" + scrollY);
                mScroller.startScroll(0, scrollY, 0, -scrollY, 500);
                invalidate();
                break;
        }
        mLastY = y;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller == null) {
            return;
        }
        if (mScroller.computeScrollOffset() && canScroll) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private boolean canPullDown() {
        boolean enable = false;
        if (mContentView instanceof ScrollView) {
            if (mContentView.getScrollY() <= 0) {
                enable = true;
            }
        }
        return enable;
    }

    private boolean canPullUp() {
        boolean enable = false;
        if (mContentView instanceof ScrollView) {
            if (mContentView.getScrollY() >= (((ScrollView) mContentView).getChildAt(0).getHeight() - mContentView.getHeight())) {
                enable = true;
            }
        }
        return enable;
    }
}
