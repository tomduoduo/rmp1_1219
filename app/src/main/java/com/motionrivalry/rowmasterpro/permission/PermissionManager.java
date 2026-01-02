package com.motionrivalry.rowmasterpro.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理器 - 统一管理应用权限请求和检查
 * 
 * 功能：
 * 1. 统一权限检查和请求
 * 2. 权限状态缓存
 * 3. 权限请求回调管理
 * 4. 特殊权限处理（如Android 11+的文件访问权限）
 * 
 * 使用示例：
 * <pre>
 * PermissionManager.getInstance().checkPermissions(activity, 
 *     new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
 *     new PermissionCallback() {
 *         @Override
 *         public void onAllPermissionsGranted() {
 *             // 所有权限已授予
 *         }
 *         
 *         @Override
 *         public void onPermissionsDenied(List<String> deniedPermissions) {
 *             // 处理被拒绝的权限
 *         }
 *     });
 * </pre>
 */
public class PermissionManager {
    
    private static final String TAG = "PermissionManager";
    private static PermissionManager instance;
    
    // 权限状态缓存
    private Map<String, Boolean> permissionCache = new HashMap<>();
    
    // 当前权限请求回调
    private PermissionCallback currentCallback;
    
    // 默认权限列表
    public static final String[] DEFAULT_PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.INTERNET,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE
    };
    
    /**
     * 权限请求回调接口
     */
    public interface PermissionCallback {
        /**
         * 所有权限都已授予
         */
        void onAllPermissionsGranted();
        
        /**
         * 部分权限被拒绝
         * @param deniedPermissions 被拒绝的权限列表
         */
        void onPermissionsDenied(List<String> deniedPermissions);
        
        /**
         * 权限请求被取消
         */
        default void onPermissionRequestCancelled() {
            // 默认实现
        }
    }
    
    /**
     * 获取PermissionManager单例实例
     * @return PermissionManager实例
     */
    public static synchronized PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }
    
    /**
     * 私有构造函数，防止外部实例化
     */
    private PermissionManager() {
        // 私有构造函数
    }
    
    /**
     * 检查单个权限是否已授予
     * @param context 上下文
     * @param permission 权限名称
     * @return true如果权限已授予，否则false
     */
    public boolean isPermissionGranted(Context context, String permission) {
        // 检查缓存
        if (permissionCache.containsKey(permission)) {
            return permissionCache.get(permission);
        }
        
        // 检查权限状态
        boolean granted = ContextCompat.checkSelfPermission(context, permission) 
                == PackageManager.PERMISSION_GRANTED;
        
        // 缓存结果
        permissionCache.put(permission, granted);
        
        return granted;
    }
    
    /**
     * 检查多个权限是否都已授予
     * @param context 上下文
     * @param permissions 权限数组
     * @return true如果所有权限都已授予，否则false
     */
    public boolean areAllPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取未授予的权限列表
     * @param context 上下文
     * @param permissions 权限数组
     * @return 未授予的权限列表
     */
    public List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }
    
    /**
     * 请求权限（简化版）
     * @param activity 当前Activity
     * @param permissions 要请求的权限数组
     * @param callback 权限请求回调
     */
    public void checkPermissions(Activity activity, String[] permissions, PermissionCallback callback) {
        this.currentCallback = callback;
        
        // 检查是否所有权限都已授予
        if (areAllPermissionsGranted(activity, permissions)) {
            if (callback != null) {
                callback.onAllPermissionsGranted();
            }
            return;
        }
        
        // 获取未授予的权限
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        
        // 请求未授予的权限
        String[] permissionsToRequest = deniedPermissions.toArray(new String[0]);
        ActivityCompat.requestPermissions(activity, permissionsToRequest, 1);
    }
    
    /**
     * 请求默认权限列表
     * @param activity 当前Activity
     * @param callback 权限请求回调
     */
    public void checkDefaultPermissions(Activity activity, PermissionCallback callback) {
        checkPermissions(activity, DEFAULT_PERMISSIONS, callback);
    }
    
    /**
     * 处理权限请求结果（需要在Activity的onRequestPermissionsResult中调用）
     * @param requestCode 请求代码
     * @param permissions 权限数组
     * @param grantResults 授权结果数组
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        if (requestCode != 1) {
            return;
        }
        
        if (currentCallback == null) {
            return;
        }
        
        // 更新权限缓存
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            permissionCache.put(permissions[i], granted);
            
            if (!granted) {
                deniedPermissions.add(permissions[i]);
            }
        }
        
        // 调用回调
        if (deniedPermissions.isEmpty()) {
            currentCallback.onAllPermissionsGranted();
        } else {
            currentCallback.onPermissionsDenied(deniedPermissions);
        }
        
        // 清除当前回调
        currentCallback = null;
    }
    
    /**
     * 检查是否需要显示权限说明
     * @param activity 当前Activity
     * @param permission 权限名称
     * @return true如果需要显示说明，否则false
     */
    public boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
    
    /**
     * 获取被拒绝且选择了"不再询问"的权限列表
     * @param activity 当前Activity
     * @param permissions 权限数组
     * @return 被拒绝且不再询问的权限列表
     */
    public List<String> getNeverAskAgainPermissions(Activity activity, String[] permissions) {
        List<String> neverAskAgainPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(activity, permission) && 
                !shouldShowRequestPermissionRationale(activity, permission)) {
                neverAskAgainPermissions.add(permission);
            }
        }
        return neverAskAgainPermissions;
    }
    
    /**
     * 清除权限缓存
     */
    public void clearPermissionCache() {
        permissionCache.clear();
    }
    
    /**
     * 检查Android 11+的特殊文件访问权限
     * @param context 上下文
     * @return true如果有文件访问权限，否则false
     */
    public boolean hasExternalStorageManagerPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return android.os.Environment.isExternalStorageManager();
        }
        return true; // Android 10及以下不需要特殊处理
    }
    
    /**
     * 请求Android 11+的特殊文件访问权限
     * @param activity 当前Activity
     */
    public void requestExternalStorageManagerPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && 
            !android.os.Environment.isExternalStorageManager()) {
            android.content.Intent intent = new android.content.Intent(
                android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            android.net.Uri uri = android.net.Uri.parse("package:" + activity.getPackageName());
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }
}