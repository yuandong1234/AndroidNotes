package com.yuong.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yuong.notes.utils.ScreenUtil;
import com.yuong.notes.widget.popwindow.CommonPopupWindow;

public class PopWindowActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private Button btn2;
    private CommonPopupWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_window);
        initView();
    }

    private void initView() {
        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                popDown();
                break;
            case R.id.btn2:
                popDown();
                break;

        }
    }

    private void popDown() {
        int width = ScreenUtil.getScreenWidth(this);
        int screenHeight = ScreenUtil.getScreenHeight(this);
        int height = screenHeight - ScreenUtil.getViewLocationOnScreen(btn)[1] - btn.getHeight();

        if (popWindow != null && popWindow.isShowing()) {
            return;
        }

        popWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.layout_pop_window_view)
                .setWidth(width)
                //.setHeight(height)
                .setAnimationStyle(R.style.AnimDown)
                .setOutsideTouchable(true)
                .setFocusable(true)
                //.setBackgroundLevel(0.8f)
                .build();

        popWindow.showAsDropDown(btn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popWindow.isShowing()) {
            popWindow.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
