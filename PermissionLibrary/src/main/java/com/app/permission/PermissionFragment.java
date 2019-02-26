package com.app.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PermissionFragment extends Fragment {
    private static final String TAG = PermissionFragment.class.getSimpleName();

    private static final String PERMISSION_GROUP = "permission_group";//请求的权限
    private static final String REQUEST_CODE = "request_code";
    private static final String REQUEST_CONSTANT = "request_constant";

    private final static SparseArray<OnPermissionCallback> mContainer = new SparseArray<>();

    public static PermissionFragment newInstant(ArrayList<String> permissions, boolean constant) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();

        int requestCode;
        //请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            //requestCode = new Random().nextInt(65535);//Studio编译的APK请求码必须小于65536
            requestCode = new Random().nextInt(255);//Eclipse编译的APK请求码必须小于256
        } while (mContainer.get(requestCode) != null);

        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, permissions);
        bundle.putBoolean(REQUEST_CONSTANT, constant);
        fragment.setArguments(bundle);
        return fragment;
    }


    public void prepareRequest(Activity activity, OnPermissionCallback call) {
        //将当前的请求码和对象添加到集合中
        mContainer.put(getArguments().getInt(REQUEST_CODE), call);

        activity.getFragmentManager()
                .beginTransaction()
                .add(this, activity.getClass().getName())
                .commit();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestPermission();
    }

    private void requestPermission() {
        if (PermissionUtil.isOverMarshmallow()) {
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
            if (permissions != null) {
                requestPermissions(permissions.toArray(new String[0]), getArguments().getInt(REQUEST_CODE));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        OnPermissionCallback call = mContainer.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (call == null) return;

        //获取授予权限
        List<String> grantedPermissions = getGrantedPermissions(permissions, grantResults);
        //如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size() == permissions.length) {
            //代表申请的所有的权限都授予了
            call.onPermissionGranted(grantedPermissions, true);
        } else {
            //获取拒绝权限
            List<String> deniedPermissions = getDeniedPermissions(permissions, grantResults);
            //获取永久性拒绝权限
            List<String> permanentDeniedPermissions = PermissionUtil.getPermanentDeniedPermissions(getActivity(), deniedPermissions);
            //获得暂时性拒绝权限
            List<String> tempDeniedPermissions = PermissionUtil.getTempDeniedPermissions(getActivity(), deniedPermissions);

            //只要用户选择禁止，但未勾选“下次不在询问”，就一直请求权限
            if (getArguments().getBoolean(REQUEST_CONSTANT) && tempDeniedPermissions != null && tempDeniedPermissions.size() != 0) {
                //继续请求权限直到用户授权或者永久拒绝
                requestPermission();
                return;
            }

            //代表申请的权限中有不同意授予的，如果拒绝的时间过快证明是系统自动拒绝
            call.onPermissionDenied(deniedPermissions, permanentDeniedPermissions);

            //证明还有一部分权限被成功授予，回调成功接口
            if (!grantedPermissions.isEmpty()) {
                call.onPermissionGranted(grantedPermissions, false);
            }
        }
        //权限申请结束
        call.onPermissionComplete();
        //权限回调结束后要删除集合中的对象，避免重复请求
        mContainer.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    private  List<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    private  List<String> getGrantedPermissions(String[] permissions, int[] grantResults) {

        List<String> grantedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i]);
            }
        }
        return grantedPermissions;
    }
}
