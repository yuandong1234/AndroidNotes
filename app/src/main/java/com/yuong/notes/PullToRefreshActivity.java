package com.yuong.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 下拉刷新上拉加载测试用例
 */
public class PullToRefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);
        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("PullToRefreshActivity","onTouch......");
                return false;
            }
        });

    }


}
