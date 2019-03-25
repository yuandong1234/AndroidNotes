package com.yuong.notes;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.app.refresh.RefreshLayout;
import com.app.refresh.listener.RefreshListener;

/**
 * 下拉刷新上拉加载测试用例
 */
public class PullToRefreshActivity extends AppCompatActivity implements RefreshListener {
    private RefreshLayout refreshLayout;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setListener(this);
//        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.e("PullToRefreshActivity", "onTouch......");
//                return false;
//            }
//        });

    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.onRefreshComplete();
            }
        },3000);
    }

    @Override
    public void onLoadMore() {

    }
}
