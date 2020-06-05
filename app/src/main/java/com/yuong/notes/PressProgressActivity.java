package com.yuong.notes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.yuong.notes.widget.PressProgressView;

public class PressProgressActivity extends AppCompatActivity {
    private PressProgressView pressProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press_progress);
        pressProgressView = findViewById(R.id.pressProgressView);
        pressProgressView.setOnPressClickListener(new PressProgressView.OnPressClickListener() {
            @Override
            public void onPressClick() {

            }

            @Override
            public void onShortClick() {

            }

            @Override
            public void onClick() {

            }
        });
    }
}
