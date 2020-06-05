package com.yuong.notes.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.yuong.notes.R;

/**
 * 长按进度控件
 */
public class PressProgressView extends View {
    private String TAG = PressProgressView.class.getSimpleName();
    private Paint mPaint;   // 绘制画笔
    private RectF mRectF;       // 绘制区域
    private float mProgress;      // 圆环进度(0-100)
    private long mDuration = 3 * 1000;
    private boolean isPressing = false;
    private long mPressDuration;
    private OnPressClickListener onPressClickListener;
    private PressCountDownTimer mTimer = new PressCountDownTimer(mDuration, mDuration / 360);
    private boolean isShowProgress = true;

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
    }

    public PressProgressView(Context context) {
        this(context, null);
    }

    public PressProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PressProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PressProgressView);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mPaint.setAntiAlias(true);              // 设置抗锯齿
        mPaint.setDither(true);                 // 设置抖动
        mPaint.setStrokeWidth(typedArray.getDimension(R.styleable.PressProgressView_PressProgress_stroke_width, 10));
        mPaint.setColor(typedArray.getColor(R.styleable.PressProgressView_PressProgress_color, Color.BLUE));
        mProgress = typedArray.getInteger(R.styleable.PressProgressView_PressProgress_progress, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((viewWide > viewHigh ? viewHigh : viewWide) - mPaint.getStrokeWidth());
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        mRectF = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, -90 + mProgress, 360 - mProgress, false, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.setPressed(true);
                if (isShowProgress) {
                    startPress();
                }
                break;
            case MotionEvent.ACTION_UP:
                this.setPressed(false);
                if (!isShowProgress) {
                    if (onPressClickListener != null) {
                        onPressClickListener.onClick();
                    }
                } else {
                    stopPress();
                }
                break;
        }
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 获取当前进度
     *
     * @return 当前进度（0-100）
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * 设置当前进度
     *
     * @param progress 当前进度（0-100）
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    public void setProgress(int progress, long animTime) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    /**
     * 设置进度圆环宽度
     *
     * @param width 进度圆环宽度
     */
    public void setProgressWidth(int width) {
        mPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置进度圆环颜色
     *
     * @param color 景圆环颜色
     */
    public void setProgressColor(@ColorRes int color) {
        mPaint.setColor(ContextCompat.getColor(getContext(), color));
        mPaint.setShader(null);
        invalidate();
    }


    private class PressCountDownTimer extends CountDownTimer {
        PressCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateProgress(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            isPressing = false;
            updateProgress(0);
            stopPress();
        }
    }

    private void updateProgress(long millisUntilFinished) {
        mPressDuration = (int) (mDuration - millisUntilFinished);
        Log.e(TAG, "mPressDuration  :" + mPressDuration);
        mProgress = 360f - millisUntilFinished / (float) mDuration * 360f;
        invalidate();
    }

    private void startPress() {
        mTimer.start();
        isPressing = true;
    }

    private synchronized void stopPress() {

        if (isPressing) {
            mTimer.cancel();
            onPressClickListener.onShortClick();
        }

        if (mPressDuration == mDuration && onPressClickListener != null) {
            onPressClickListener.onPressClick();
        }

        isPressing = false;
        mPressDuration = 0;
        mProgress = 360f;
        invalidate();
    }

    public void setOnPressClickListener(OnPressClickListener onPressClickListener) {
        this.onPressClickListener = onPressClickListener;
    }

    public interface OnPressClickListener {
        void onPressClick();

        void onShortClick();

        void onClick();
    }
}
