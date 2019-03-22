package com.yuong.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.app.permission.OnPermissionCallback;
import com.app.permission.Permission;
import com.app.permission.PermissionUtil;
import com.yuong.notes.widget.dialog.CommonDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
