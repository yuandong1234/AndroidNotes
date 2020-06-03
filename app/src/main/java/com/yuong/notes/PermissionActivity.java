package com.yuong.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.permission.OnPermissionCallback;
import com.app.permission.Permission;
import com.app.permission.PermissionUtil;

import java.util.List;


public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        applyPermissions();
    }


    private void applyPermissions() {
        PermissionUtil.with(this)
                //.constantRequest()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_PHONE_STATE, Permission.ACCESS_FINE_LOCATION,
                        Permission.ACCESS_COARSE_LOCATION, Permission.CAMERA)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onPermissionGranted(List<String> granted, boolean isAllGranted) {
                        Log.e("FlexibleActivity", "onPermissionGranted granted : " + granted.toString() + "\n" + " isAllGranted : " + isAllGranted);
                    }

                    @Override
                    public void onPermissionDenied(List<String> denied, List<String> permanentDenied) {
                        Log.e("FlexibleActivity", "onPermissionDenied denied : " + denied.toString() + "\n" + " permanentDenied : " + permanentDenied.toString());
                    }

                    @Override
                    public void onPermissionComplete() {
                        Log.e("FlexibleActivity", "onPermissionComplete ... ");
                    }
                });
    }
}
