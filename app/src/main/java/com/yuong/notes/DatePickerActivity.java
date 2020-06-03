package com.yuong.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yuong.notes.widget.DatePickerView;

public class DatePickerActivity extends AppCompatActivity {

    private DatePickerView datePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        initView();
    }

    private void initView() {
        datePickerView = findViewById(R.id.datePickerView);
        datePickerView.setCallback(new DatePickerView.OnDateChangeCallback() {
            @Override
            public void onChange(String date) {
                Log.e("DatePickerActivity", "date : " + date);
            }
        });
    }
}
