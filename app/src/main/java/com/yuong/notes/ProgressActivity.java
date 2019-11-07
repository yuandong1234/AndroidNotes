package com.yuong.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yuong.notes.widget.progressbar.ProgressView;

public class ProgressActivity extends AppCompatActivity {

    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        initView();
    }

    private void initView() {
        progressView = findViewById(R.id.progressView);
        progressView.setValue(50f);
    }
}
