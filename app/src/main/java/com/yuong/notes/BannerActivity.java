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

public class BannerActivity extends AppCompatActivity {

    private ViewPager viewPager;

    private int datas[] = {R.drawable.img_01, R.drawable.img_02, R.drawable.img_03, R.drawable.img_04};

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
        viewPager.setAdapter(new CustomViewPager(this, datas));
    }

    /**
     * PagerAdapter、FragmentPagerAdapter、FragmentStatePagerAdapter
     */

    private class CustomViewPager extends PagerAdapter {
        private Context context;
        private LayoutInflater inflater;
        private int[] data;

        public CustomViewPager(Context context, int[] data) {
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
}
