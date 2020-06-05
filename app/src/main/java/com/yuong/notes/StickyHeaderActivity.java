package com.yuong.notes;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.yuong.notes.widget.stickyview.MyScrollView;

public class StickyHeaderActivity extends AppCompatActivity implements MyScrollView.OnScrollListener {

    private SwipeRefreshLayout parentLayout;
    private TextView topStickyHeaderView;
    private TextView stickyHeaderView;
    private MyScrollView myScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_header);
        initView();
    }

    private void initView() {
        parentLayout = findViewById(R.id.parent_layout);
        topStickyHeaderView = findViewById(R.id.top_sticky_layout);
        stickyHeaderView = findViewById(R.id.sticky_layout);
        myScrollView = findViewById(R.id.scrollView);
        myScrollView.setOnScrollListener(this);
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("StickyHeaderActivity","ScrollY : "+myScrollView.getScrollY());
                onScroll(myScrollView.getScrollY());
            }

        });
    }

    @Override
    public void onScroll(int scrollY) {
        Log.e("StickyHeaderActivity","ScrollY2 : "+myScrollView.getScrollY());
        int mBuyLayout2ParentTop = Math.max(scrollY, stickyHeaderView.getTop());
        topStickyHeaderView.layout(0,
                mBuyLayout2ParentTop,
                topStickyHeaderView.getWidth(),
                mBuyLayout2ParentTop + topStickyHeaderView.getHeight());
    }
}
