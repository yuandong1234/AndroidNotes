package com.yuong.notes.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ElasticScrollView extends ScrollView {
    private static String TAG = ElasticScrollView.class.getName();

    private static final float MOVE_FACTOR = 0.25f;
    private Context mContext;
    private Scroller mScroller;
    private View mContentView;
    private Rect mOriginalRect = new Rect();
    private float startY;
    private float lastY;
    private boolean canPullDown;
    private boolean canPullUp;
    private boolean isMoved;

    public ElasticScrollView(Context context) {
        this(context, null);
    }

    public ElasticScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    //initialize
    private void init() {
        mScroller = new Scroller(mContext);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mContentView == null) {
            return;
        }
        mOriginalRect.set(mContentView.getLeft(),
                mContentView.getTop(),
                mContentView.getRight(),
                mContentView.getBottom());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mContentView == null) {
            return super.dispatchTouchEvent(ev);
        }
        float y = ev.getY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = startY = y;
                Log.i(TAG, "startY : " + startY);
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canPullDown && !canPullUp) {
                    //ScrollView 自身滑动
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();
                    break;
                }

                Log.i(TAG, "y : " + y);
                int deltaY = (int) (y - startY);
                Log.i(TAG, "deltaY : " + deltaY);

                //是否应该移动布局
                boolean shouldMove = (canPullDown && deltaY > 0)    //可以下拉， 并且手指向下移动
                        || (canPullUp && deltaY < 0)    //可以上拉， 并且手指向上移动
                        || (canPullUp && canPullDown); //既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）
                if (shouldMove) {
                    //计算偏移量
                    final int offset = (int) (deltaY * MOVE_FACTOR);
                    Log.i(TAG, "offset : " + offset);
                    //随着手指的移动而移动布局
                    // ((View) getParent()).scrollBy(0, -offset);
                    //mContentView.scrollBy(0, -offset);
                    //scrollBy(0, -offset);
                    mContentView.layout(mOriginalRect.left, mOriginalRect.top + offset,
                            mOriginalRect.right, mOriginalRect.bottom + offset);

                    isMoved = true;  //记录移动了布局
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMoved) break;
                Log.i(TAG, "****************** ");
                canPullDown = false;
                canPullUp = false;
                isMoved = false;
//                //TODO
//                mScroller.startScroll(0, (int) y, 0, -(int) (y - lastY), 1000);
//                invalidate();
//                break;
        }
       // startY = y;
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }

    /**
     * 判断是否滚动到顶部
     */

    private boolean isCanPullDown() {
        return getScrollY() == 0 ||
                mContentView.getHeight() < getHeight() + getScrollY();
    }

    /**
     * 判断是否滚动到底部
     */

    private boolean isCanPullUp() {
        return mContentView.getHeight() <= getHeight() + getScrollY();

    }
}
