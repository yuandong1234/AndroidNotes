package com.yuong.notes.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class FlexibleLinearLayout extends LinearLayout {

    private float MOVE_FACTOR = 0.25f;
    private Scroller mScroller;
    private Context mContext;
    private boolean canScroll;
    private float lastY;

    public FlexibleLinearLayout(Context context) {
        this(context, null);
    }

    public FlexibleLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        mScroller = new Scroller(mContext);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("执行onTouchEvent...");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("down.........");
                canScroll = true;
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("move...........");
                canScroll = true;
                float nowY = event.getY();
                float deltaY = nowY - lastY;
                int offset = (int) (deltaY * MOVE_FACTOR);
                System.out.println("deltaY :" + deltaY + " offset :" + offset);
                //滑动
                scrollBy(0, -offset);
                lastY = nowY;
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("up.......");
                canScroll = false;
                int scrollY = getScrollY();
                System.out.println("scrollY :" + scrollY);
                mScroller.startScroll(0, scrollY, 0, -scrollY, 500);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller == null) {
            return;
        }
        if (mScroller.computeScrollOffset() && !canScroll) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
