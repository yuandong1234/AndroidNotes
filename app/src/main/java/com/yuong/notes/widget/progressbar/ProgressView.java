package com.yuong.notes.widget.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.yuong.notes.R;

public class ProgressView extends View {

    private Paint mCirclePaint;
    private Paint mArcPaint;
    private Paint mTextPaint;
    private RectF mOval = new RectF();
    private Rect mBounds = new Rect();

    private float mCenterX, mCenterY;
    private int mProgressBackground;
    private int mProgressColor;
    private float mProgressRadius;
    private float mProgressStrokeWidth;
    private int mProgressTitleColor;
    private int mProgressValueColor;
    private float mProgressTitleTextSize;
    private float mProgressValueTextSize;
    private float mProgressPadding;
    private String mProgressTitle;
    private int mWidth, mHeight;
    private float mMaxAngle, mAngle, mValue;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mProgressBackground = typedArray.getColor(R.styleable.ProgressView_progress_background, Color.parseColor("#EAEAEA"));
        mProgressColor = typedArray.getColor(R.styleable.ProgressView_progress_color, Color.parseColor("#FF9900"));
        mProgressRadius = typedArray.getDimensionPixelSize(R.styleable.ProgressView_progress_radius, 0);
        mProgressStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressView_progress_stroke_width, 0);
        mProgressTitleColor = typedArray.getColor(R.styleable.ProgressView_progress_title_color, Color.parseColor("#999999"));
        mProgressValueColor = typedArray.getColor(R.styleable.ProgressView_progress_value_color, Color.parseColor("#3f3f3f"));
        mProgressTitleTextSize = typedArray.getDimension(R.styleable.ProgressView_progress_title_text_size, sp2px(14));
        mProgressValueTextSize = typedArray.getDimension(R.styleable.ProgressView_progress_value_text_size, sp2px(14));
        mProgressPadding = typedArray.getDimension(R.styleable.ProgressView_progress_padding, dp2px(5));
        mProgressTitle = typedArray.getString(R.styleable.ProgressView_progress_title);
        typedArray.recycle();
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mProgressBackground);
        mCirclePaint.setStrokeWidth(mProgressStrokeWidth);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setDither(true);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mProgressColor);
        mArcPaint.setStrokeWidth(mProgressStrokeWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setDither(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setDither(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mCenterX = mWidth / 2;
        mCenterY = mProgressRadius + mProgressStrokeWidth / 2 + mProgressPadding;
        mHeight = (int) (mProgressPadding * 2 + 2 * mProgressRadius + mProgressStrokeWidth);

        mOval.left = mCenterX - mProgressRadius;
        mOval.top = mCenterY - mProgressRadius;
        mOval.right = mCenterX + mProgressRadius;
        mOval.bottom = mCenterY + mProgressRadius;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawTxt(canvas);
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(mCenterX, mCenterY, mProgressRadius, mCirclePaint);
        canvas.rotate(-270, mCenterX, mCenterY);
        canvas.drawArc(mOval, 0, mAngle, false, mArcPaint);//画弧
        canvas.restore();
    }

    private void drawTxt(Canvas canvas) {
        mTextPaint.setTextSize(mProgressValueTextSize);
        mTextPaint.setFakeBoldText(true);
        String text = String.valueOf(getCurrentValue());
        mTextPaint.getTextBounds(text, 0, text.length(), mBounds);
        mTextPaint.setColor(mProgressValueColor);
        canvas.drawText(text, mCenterX, mCenterY, mTextPaint);

        mTextPaint.setTextSize(mProgressTitleTextSize);
        mTextPaint.setFakeBoldText(false);
        String text2 = TextUtils.isEmpty(mProgressTitle) ? "--" : mProgressTitle;
        mTextPaint.getTextBounds(text2, 0, text2.length(), mBounds);
        mTextPaint.setColor(mProgressTitleColor);
        canvas.drawText(text2, mCenterX, mCenterY + mBounds.height() + 20, mTextPaint);
    }

    private void startAnimation() {
        ValueAnimator anim = ValueAnimator.ofFloat(mAngle, mMaxAngle);
        anim.setDuration(800);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        anim.start();
    }

    //设置数据
    public void setValue(float value) {
        this.mValue = value;
        this.mMaxAngle = value * 360 / 100;
        startAnimation();
    }

    private String getCurrentValue() {
        if (mAngle == mMaxAngle) {
            return mValue + "";
        }
        float result = mAngle * mValue / mMaxAngle;
        return Math.round(result * 10) / 10.0 + "";
    }

    /**
     * dp转化为px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * sp转化为px
     *
     * @param sp
     * @return
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
