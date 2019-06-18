package com.yuong.notes.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 解决SwipeRefreshLayout中嵌套ViewPager滑动冲突
 */
public class ViewPagerSwipeRefreshLayout extends SwipeRefreshLayout {
    private String TAG = ViewPagerSwipeRefreshLayout.class.getSimpleName();
    private final int mTouchSlop;
    private float mStartX;
    private float mStartY;
    private boolean isVpDrag;

    public ViewPagerSwipeRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public ViewPagerSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获得滑动距离范围
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                isVpDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isVpDrag) {
                    return false;
                }
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                Log.e(TAG, "mTouchSlop : " + mTouchSlop + " distanceX : " + distanceX + "  distanceY : " + distanceY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isVpDrag = true;
                    return false;
                }
                mStartX = endX;
                mStartY = endY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isVpDrag = false;
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
