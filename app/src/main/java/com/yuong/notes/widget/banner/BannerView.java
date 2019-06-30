package com.yuong.notes.widget.banner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yuong.notes.R;
import com.yuong.notes.widget.banner.holder.BannerHolderCreator;
import com.yuong.notes.widget.banner.holder.BannerViewHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerView<T> extends RelativeLayout {

    private static final String TAG = "BannerView";
    private BannerViewPager mViewPager;
    private BannerPagerAdapter mAdapter;
    private List<T> mDatas;
    private boolean mIsAutoPlay = true;// 是否自动播放
    private int mCurrentItem = 0;//当前位置
    private Handler mHandler = new Handler();
    private int mDelayedTime = 3000;// Banner 切换时间间隔
    private ViewPagerScroller mViewPagerScroller;//控制ViewPager滑动速度的Scroller
    private boolean mIsCanLoop = true;// 是否轮播图片
    private LinearLayout mIndicatorContainer;//indicator容器
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    //mIndicatorRes[0] 为未选中，mIndicatorRes[1]为选中
    private int[] mIndicatorRes = new int[]{R.drawable.indicator_normal, R.drawable.indicator_selected};
    private int mIndicatorMarginLeft = 0;// indicator 距离左边的距离
    private int mIndicatorMarginRight = 0;//indicator 距离右边的距离
    private int mIndicatorMarginTop = 0;//indicator 距离上边的距离
    private int mIndicatorMarginBottom = 0;//indicator 距离下边的距离
    private int mBannerPaddingLeft = 0;//BannerViewPager左右内边距（由于前后显示了上下一个页面的部分）
    private int mBannerPaddingRight = 0;
    private int mIndicatorAlign = 1;
    private OnPageChangeListener mOnPageChangeListener;
    private BannerPageClickListener mBannerPageClickListener;

    public enum IndicatorAlign {
        LEFT,//左对齐
        CENTER,//居中对齐
        RIGHT //右对齐
    }

    public BannerView(@NonNull Context context) {
        super(context);
        init();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);
        init();
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mIsCanLoop = typedArray.getBoolean(R.styleable.BannerView_canLoop, false);
        mBannerPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.BannerView_PaddingLeft, 0);
        mBannerPaddingRight = typedArray.getDimensionPixelSize(R.styleable.BannerView_PaddingRight, 0);
        mIndicatorAlign = typedArray.getInt(R.styleable.BannerView_indicatorAlign, IndicatorAlign.CENTER.ordinal());
        mIndicatorMarginLeft = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorMarginLeft, dpToPx(15));
        mIndicatorMarginRight = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorMarginRight,  dpToPx(15));
        mIndicatorMarginTop = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorMarginTop, 0);
        mIndicatorMarginBottom = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorMarginBottom, dpToPx(25));
        typedArray.recycle();
    }


    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.banner_layout, this, true);
        mIndicatorContainer = view.findViewById(R.id.banner_indicator_container);
        mViewPager = view.findViewById(R.id.banner_vp);
        mViewPager.setPadding(mBannerPaddingLeft, 0, mBannerPaddingRight, 0);
        mViewPager.setOffscreenPageLimit(3);
        // 初始化Scroller
        initViewPagerScroll();
        //确定指示器位置
        sureIndicatorPosition();
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mViewPagerScroller = new ViewPagerScroller(mViewPager.getContext());
            mScroller.set(mViewPager, mViewPagerScroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * make sure the indicator
     */
    private void sureIndicatorPosition() {
        if (mIndicatorAlign == IndicatorAlign.LEFT.ordinal()) {
            setIndicatorAlign(IndicatorAlign.LEFT);
        } else if (mIndicatorAlign == IndicatorAlign.CENTER.ordinal()) {
            setIndicatorAlign(IndicatorAlign.CENTER);
        } else {
            setIndicatorAlign(IndicatorAlign.RIGHT);
        }
    }

    /**
     * 设置Indicator 的对齐方式
     *
     * @param indicatorAlign {@link IndicatorAlign#CENTER }{@link IndicatorAlign#LEFT }{@link IndicatorAlign#RIGHT }
     */
    public void setIndicatorAlign(IndicatorAlign indicatorAlign) {
        mIndicatorAlign = indicatorAlign.ordinal();
        LayoutParams layoutParams = (LayoutParams) mIndicatorContainer.getLayoutParams();
        if (indicatorAlign == IndicatorAlign.LEFT) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (indicatorAlign == IndicatorAlign.RIGHT) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        layoutParams.setMargins(0, mIndicatorMarginTop, 0, mIndicatorMarginBottom);
        mIndicatorContainer.setLayoutParams(layoutParams);

    }

    /**
     * 初始化指示器Indicator
     */
    private void initIndicator() {
        mIndicatorContainer.removeAllViews();
        mIndicators.clear();
        for (int i = 0; i < mDatas.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            if (mIndicatorAlign == IndicatorAlign.LEFT.ordinal()) {
                if (i == 0) {
                    int paddingLeft = mIndicatorMarginLeft + mBannerPaddingLeft;
                    imageView.setPadding(paddingLeft + 6, 0, 6, 0);
                } else {
                    imageView.setPadding(6, 0, 6, 0);
                }

            } else if (mIndicatorAlign == IndicatorAlign.RIGHT.ordinal()) {
                if (i == mDatas.size() - 1) {
                    int paddingRight = mIndicatorMarginRight + mBannerPaddingRight;
                    imageView.setPadding(6, 0, 6 + paddingRight, 0);
                } else {
                    imageView.setPadding(6, 0, 6, 0);
                }
            } else {
                imageView.setPadding(6, 0, 6, 0);
            }

            if (i == (mCurrentItem % mDatas.size())) {
                imageView.setImageResource(mIndicatorRes[1]);
            } else {
                imageView.setImageResource(mIndicatorRes[0]);
            }

            mIndicators.add(imageView);
            mIndicatorContainer.addView(imageView);
        }
    }

    //TODO 待处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsCanLoop) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            // 按住Banner的时候，停止自动轮播
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_DOWN:
                int paddingLeft = mViewPager.getLeft();
                float touchX = ev.getRawX();
                // 如果是魅族模式，去除两边的区域
                if (touchX >= paddingLeft && touchX < getScreenWidth(getContext()) - paddingLeft) {
                    pause();
                }
                break;
            case MotionEvent.ACTION_UP:
                start();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }

    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                mCurrentItem = mViewPager.getCurrentItem();
                mCurrentItem++;
                if (mCurrentItem == mAdapter.getCount() - 1) {
                    mCurrentItem = 0;
                    mViewPager.setCurrentItem(mCurrentItem, false);
                    mHandler.postDelayed(this, mDelayedTime);
                } else {
                    mViewPager.setCurrentItem(mCurrentItem);
                    mHandler.postDelayed(this, mDelayedTime);
                }
            } else {
                mHandler.postDelayed(this, mDelayedTime);
            }
        }
    };


    public void start() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (mAdapter == null) {
            return;
        }
        if (mIsCanLoop) {
            pause();
            mIsAutoPlay = true;
            mHandler.postDelayed(mLoopRunnable, mDelayedTime);
        }
    }

    /**
     * 停止轮播
     */
    public void pause() {
        mIsAutoPlay = false;
        mHandler.removeCallbacks(mLoopRunnable);
    }

    /**
     * 设置是否可以轮播
     *
     * @param canLoop
     */
    public void setCanLoop(boolean canLoop) {
        mIsCanLoop = canLoop;
        if (!canLoop) {
            pause();
        }
    }

    /**
     * 设置BannerView 的切换时间间隔
     *
     * @param delayedTime
     */
    public void setDelayedTime(int delayedTime) {
        mDelayedTime = delayedTime;
    }

    public void addPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 添加Page点击事件
     *
     * @param bannerPageClickListener {@link BannerPageClickListener}
     */
    public void setBannerPageClickListener(BannerPageClickListener bannerPageClickListener) {
        mBannerPageClickListener = bannerPageClickListener;
    }

    /**
     * 是否显示Indicator
     *
     * @param visible true 显示Indicator，否则不显示
     */
    public void setIndicatorVisible(boolean visible) {
        if (visible) {
            mIndicatorContainer.setVisibility(VISIBLE);
        } else {
            mIndicatorContainer.setVisibility(GONE);
        }
    }

    /**
     * set indicator margin
     *
     * @param marginLeft
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     */
    public void setIndicatorPadding(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        mIndicatorMarginLeft = marginLeft;
        mIndicatorMarginRight = marginTop;
        mIndicatorMarginTop = marginRight;
        mIndicatorMarginBottom = marginBottom;
        sureIndicatorPosition();
    }

    /**
     * 返回ViewPager
     *
     * @return {@link ViewPager}
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 设置indicator 图片资源
     *
     * @param unSelectRes 未选中状态资源图片
     * @param selectRes   选中状态资源图片
     */
    public void setIndicatorRes(@DrawableRes int unSelectRes, @DrawableRes int selectRes) {
        mIndicatorRes[0] = unSelectRes;
        mIndicatorRes[1] = selectRes;
    }

    /**
     * 设置数据，这是最重要的一个方法。
     * <p>其他的配置应该在这个方法之前调用</p>
     *
     * @param datas         Banner 展示的数据集合
     * @param holderCreator ViewHolder生成器 {@link BannerHolderCreator} And {@link BannerViewHolder}
     */
    public void setPages(List<T> datas, BannerHolderCreator holderCreator) {
        if (datas == null || holderCreator == null) {
            return;
        }
        mDatas = datas;
        //如果在播放，就先让播放停止
        pause();


        //初始化Indicator
        initIndicator();

        mAdapter = new BannerPagerAdapter(datas, holderCreator, mIsCanLoop);
        mAdapter.setUpViewViewPager(mViewPager);
        mAdapter.setPageClickListener(mBannerPageClickListener);


        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int realPosition = position % mIndicators.size();
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;


                // 切换indicator
                int realSelectPosition = mCurrentItem % mIndicators.size();
                for (int i = 0; i < mDatas.size(); i++) {
                    if (i == realSelectPosition) {
                        mIndicators.get(i).setImageResource(mIndicatorRes[1]);
                    } else {
                        mIndicators.get(i).setImageResource(mIndicatorRes[0]);
                    }
                }
                // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的position
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(realSelectPosition);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mIsAutoPlay = false;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        mIsAutoPlay = true;
                        break;

                }
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });


    }


    public LinearLayout getIndicatorContainer() {
        return mIndicatorContainer;
    }

    /**
     * 设置ViewPager切换的速度
     *
     * @param duration 切换动画时间
     */
    public void setDuration(int duration) {
        mViewPagerScroller.setDuration(duration);
    }

    /**
     * 设置是否使用ViewPager默认是的切换速度
     *
     * @param useDefaultDuration 切换动画时间
     */
    public void setUseDefaultDuration(boolean useDefaultDuration) {
        mViewPagerScroller.setUseDefaultDuration(useDefaultDuration);
    }

    /**
     * 获取Banner页面切换动画时间
     *
     * @return
     */
    public int getDuration() {
        return mViewPagerScroller.getScrollDuration();
    }


    /**
     * Banner page 点击回调
     */
    public interface BannerPageClickListener {
        void onPageClick(View view, int position);
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
