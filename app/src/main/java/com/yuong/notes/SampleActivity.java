package com.yuong.notes;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_sample_1).setOnClickListener(this);
        findViewById(R.id.tv_sample_2).setOnClickListener(this);
        findViewById(R.id.tv_sample_3).setOnClickListener(this);
        findViewById(R.id.tv_sample_4).setOnClickListener(this);
        findViewById(R.id.tv_sample_5).setOnClickListener(this);
        findViewById(R.id.tv_sample_6).setOnClickListener(this);
        findViewById(R.id.tv_sample_6).setOnClickListener(this);
        findViewById(R.id.tv_sample_7).setOnClickListener(this);
        findViewById(R.id.tv_sample_8).setOnClickListener(this);
        findViewById(R.id.tv_sample_8).setOnClickListener(this);
        findViewById(R.id.tv_sample_9).setOnClickListener(this);
        findViewById(R.id.tv_sample_10).setOnClickListener(this);
        findViewById(R.id.tv_sample_11).setOnClickListener(this);
        findViewById(R.id.tv_sample_12).setOnClickListener(this);
        findViewById(R.id.tv_sample_13).setOnClickListener(this);
        findViewById(R.id.tv_sample_14).setOnClickListener(this);
        findViewById(R.id.tv_sample_15).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sample_1://弹性布局
                startActivity(new Intent(this, FlexibleActivity.class));
                break;
            case R.id.tv_sample_2://下拉刷新上拉加载
                startActivity(new Intent(this, PullToRefreshActivity.class));
                break;
            case R.id.tv_sample_3://权限申请
                startActivity(new Intent(this, PermissionActivity.class));
                break;
            case R.id.tv_sample_4://自定义dialog
                startActivity(new Intent(this, DialogActivity.class));
                break;
            case R.id.tv_sample_5://RecyclerView多类型列表
                startActivity(new Intent(this, MultipleItemActivity.class));
                break;
            case R.id.tv_sample_6://轮播图
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case R.id.tv_sample_7://ScrollView实现滑动悬浮吸顶效果
                startActivity(new Intent(this, StickyHeaderActivity.class));
                break;
            case R.id.tv_sample_8://列表加载更多
                startActivity(new Intent(this, LoadMoreActivity.class));
                break;
            case R.id.tv_sample_9://PopWindow
                startActivity(new Intent(this, PopWindowActivity.class));
                break;
            case R.id.tv_sample_10://自定义进度条
                startActivity(new Intent(this, ProgressActivity.class));
                break;
            case R.id.tv_sample_11://自定义TabLayout Tab
                startActivity(new Intent(this, CustomTabActivity.class));
                break;
            case R.id.tv_sample_12://自定义日期加减View
                startActivity(new Intent(this, DatePickerActivity.class));
                break;
            case R.id.tv_sample_13://自定义长按进度View
                startActivity(new Intent(this, PressProgressActivity.class));
                break;
            case R.id.tv_sample_14://使用markwon加载富文本
                startActivity(new Intent(this, MarkwonActivity.class));
                break;
            case R.id.tv_sample_15://使用webview加载html标签
                startActivity(new Intent(this, WebViewActivity.class));
                break;
        }
    }
}
