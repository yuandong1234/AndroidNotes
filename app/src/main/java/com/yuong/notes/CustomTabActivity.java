package com.yuong.notes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomTabActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tab_layout;
    private CustomTabFragment fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_tab);

        initView();
    }

    private void initView() {
        findViewById(R.id.btn).setOnClickListener(this);
        initTab();
    }

    private void initTab() {
        tab_layout = findViewById(R.id.tab_layout);
        ViewPager viewpager = findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(0);

        //设置标题
        List<String> titleList = new ArrayList<>();
        titleList.add("标题1");
        titleList.add("标题2");

        List<Fragment> fragmentList = new ArrayList<>();
        fragment1 = CustomTabFragment.newInstance();
        CustomTabFragment fragment2 = CustomTabFragment.newInstance();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        MyFragmentPagerAdapter fragmentAdapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList, titleList);
        viewpager.setAdapter(fragmentAdapter);
        //把TabLayout和ViewPager关联起来
        tab_layout.setupWithViewPager(viewpager);

        //自定义view
        for (int i = 0; i < tab_layout.getTabCount(); i++) {
            TabLayout.Tab tab = tab_layout.getTabAt(i);
            if (tab != null) {
                View view = LayoutInflater.from(this).inflate(R.layout.layout_custom_tab, null);

                TextView tv_tab = view.findViewById(R.id.title);
                tv_tab.setGravity(Gravity.CENTER);
                tv_tab.setText(titleList.get(i));
                tab.setCustomView(view);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                TabLayout.Tab tab = tab_layout.getTabAt(0);
                TextView textView = tab.getCustomView().findViewById(R.id.title);
                textView.setText("haha");
                break;
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> mList;
        List<String> titleList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> mList, List<String> titleList) {
            super(fm);
            this.mList = mList;
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

    }
}
