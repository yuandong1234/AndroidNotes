package com.app.permission;

import java.util.List;

public interface OnPermissionCallback {
    /**
     * 有权限被授予时回调
     *
     * @param granted 请求成功的权限组
     * @param isAllGranted   是否全部授予了
     */
    void onPermissionGranted(List<String> granted, boolean isAllGranted);


    /**
     * 有权限被拒绝授予时回调
     * @param denied  未授权的权限
     * @param permanentDenied  永久性未授权的权限，需要用户手动去设置中心设置
     */
    void onPermissionDenied(List<String> denied, List<String> permanentDenied);

    /**
     * 权限申请完成
     */
    void onPermissionComplete();
}
