package com.yuong.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BannerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MZBannerView mz_viewPager;


    private int datas[] = { R.drawable.img_02, R.drawable.img_03, R.drawable.img_04};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        //viewPager.setPageTransformer(true, new DepthPageTransformer());
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //viewPager.setPageTransformer(true, new ScaleYTransformer());
        //viewPager.setPageTransformer(true, new CoverModeTransformer(viewPager));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new CustomViewPagerAdapter(this, datas));


        List<Integer> list = new ArrayList<>();
        for(int i=0;i<datas.length;i++){
            list.add(datas[i]);
        }

        mz_viewPager = findViewById(R.id.mz_viewPager);
        mz_viewPager.setIndicatorVisible(true);
        // 代码中更改indicator 的位置
        //mMZBanner.setIndicatorAlign(MZBannerView.IndicatorAlign.LEFT);
        //mMZBanner.setIndicatorPadding(10,0,0,150);
        mz_viewPager.setPages(list, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
    }

    /**
     * PagerAdapter、FragmentPagerAdapter、FragmentStatePagerAdapter
     */

    private class CustomViewPagerAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater inflater;
        private int[] data;

        public CustomViewPagerAdapter(Context context, int[] data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data != null ? data.length : 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View contentView = inflater.inflate(R.layout.item_layout_viewpager, null);
            ImageView img = contentView.findViewById(R.id.img);
            img.setImageResource(data[position]);
            container.addView(contentView);
            return contentView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    public static class BannerViewHolder implements MZViewHolder<Integer> {
        private ImageView mImageView;
        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout_viewpager,null);
            mImageView = (ImageView) view.findViewById(R.id.img);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            mImageView.setImageResource(data);
        }
    }
}
