package com.app.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionUtil {

    private Activity mActivity;
    private List<String> mPermissions = new ArrayList<>();
    private boolean mConstant;

    private PermissionUtil(Activity activity) {
        mActivity = activity;
    }

    public static PermissionUtil with(Activity activity) {
        return new PermissionUtil(activity);
    }

    public PermissionUtil permission(String... permissions) {
        mPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * 被拒绝后继续申请，直到授权或者永久拒绝
     */
    public PermissionUtil constantRequest() {
        mConstant = true;
        return this;
    }

    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * 请求权限
     */
    public void request(OnPermissionCallback call) {

        ArrayList<String> DeniedPermissions = getDeniedPermissions(mActivity, mPermissions);

        if (DeniedPermissions == null || DeniedPermissions.size() == 0) {
            //证明权限已经全部授予过
            call.onPermissionGranted(mPermissions, true);
            call.onPermissionComplete();
        } else {
            //检测权限有没有在清单文件中注册
            checkPermissions(mActivity, mPermissions);
            //申请没有授予过的权限
            PermissionFragment.newInstant((new ArrayList<>(mPermissions)), mConstant).prepareRequest(mActivity, call);
        }
    }


    /**
     * 获取没有授予的权限
     *
     * @param context     上下文对象
     * @param permissions 需要请求的权限组
     */
    private static ArrayList<String> getDeniedPermissions(Context context, List<String> permissions) {

        //必须设置目标SDK为23及以上才能正常检测安装权限
        if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
            throw new RuntimeException("The targetSdkVersion SDK must be 23 or more");
        }

        //如果是安卓6.0以下版本就返回null
        if (!isOverMarshmallow()) {
            return null;
        }

        ArrayList<String> DeniedPermissions = null;

        for (String permission : permissions) {
            //把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                if (DeniedPermissions == null) {
                    DeniedPermissions = new ArrayList<>();
                }
                DeniedPermissions.add(permission);
            }
        }
        return DeniedPermissions;
    }


    /**
     * 检查某个权限是否被永久拒绝(即选择禁止并勾选：下次不在询问)
     * <p>
     * ( 注意：shouldShowRequestPermissionRationale
     * 1.第一次打开App时 ,返回false
     * 2.上次弹出权限点击了禁止（但没有勾选“下次不在询问”）,返回true
     * 3.上次选择禁止并勾选：下次不在询问 ,返回false
     * )
     *
     * @param activity    Activity对象
     * @param permissions 请求的权限
     */
    public static boolean checkPermanentDeniedPermissions(Activity activity, List<String> permissions) {

        for (String permission : permissions) {
            if (isOverMarshmallow()) {
                if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    if (!activity.shouldShowRequestPermissionRationale(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获得永久性没有授予的权限
     *
     * @param activity
     * @param permissions
     */
    public static List<String> getPermanentDeniedPermissions(Activity activity, List<String> permissions) {

        List<String> permanentDeniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (isOverMarshmallow()) {
                if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    if (!activity.shouldShowRequestPermissionRationale(permission)) {
                        permanentDeniedPermissions.add(permission);
                    }
                }
            }
        }
        return permanentDeniedPermissions;
    }

    /**
     * 获得暂时的没有授权权限
     * @param activity
     * @param permissions
     */
    public static List<String> getTempDeniedPermissions(Activity activity, List<String> permissions){
        List<String> tempDeniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (isOverMarshmallow()) {
                if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    if (activity.shouldShowRequestPermissionRationale(permission)) {
                        tempDeniedPermissions.add(permission);
                    }
                }
            }
        }
        return tempDeniedPermissions;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity           Activity对象
     * @param requestPermissions 请求的权限组
     */
    private static void checkPermissions(Activity activity, List<String> requestPermissions) {
        List<String> manifest = getManifestPermissions(activity);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new RuntimeException(permission + " : Permissions are not registered in the manifest file");
                }
            }
        } else {
            throw new RuntimeException("Permissions are not registered in the manifest file");
        }
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    private static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }
}
