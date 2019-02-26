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

        applyPermissions();
        findViewById(R.id.tv_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }


    private void showDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_common, null);
        CommonDialog dialog = builder.view(view)
                .height(200)
                .style(R.style.theme_common_dialog)
                .cancelTouchOut(true)
                .build();
        dialog.show();
    }

    public void applyPermissions() {
        PermissionUtil.with(this)
                //.constantRequest()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_PHONE_STATE, Permission.ACCESS_FINE_LOCATION,
                        Permission.ACCESS_COARSE_LOCATION, Permission.CAMERA)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onPermissionGranted(List<String> granted, boolean isAllGranted) {
                        Log.e("MainActivity", "onPermissionGranted granted : " + granted.toString() + "\n" + " isAllGranted : " + isAllGranted);
                    }

                    @Override
                    public void onPermissionDenied(List<String> denied, List<String> permanentDenied) {
                        Log.e("MainActivity", "onPermissionDenied denied : " + denied.toString() + "\n" + " permanentDenied : " + permanentDenied.toString());
                    }

                    @Override
                    public void onPermissionComplete() {
                        Log.e("MainActivity", "onPermissionComplete ... ");

                    }
                });
    }
}
