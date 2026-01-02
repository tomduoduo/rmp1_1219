package com.motionrivalry.rowmasterpro.network.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 网络状态服务类
 * 提供网络状态检查、用户反馈等通用网络服务
 * 使用单例模式确保全局一致性
 */
public class NetworkStateService {

    private static final String TAG = "NetworkStateService";
    private static NetworkStateService instance;
    private final Context applicationContext;
    private final Handler mainHandler;
    private final Handler debounceHandler;
    private Runnable lastToastRunnable;

    /**
     * 私有构造函数，初始化服务和处理器
     */
    private NetworkStateService(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.debounceHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 初始化网络状态服务
     * 必须在Application中调用一次
     * 
     * @param context 应用上下文
     */
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new NetworkStateService(context);
        }
    }

    /**
     * 获取网络状态服务单例实例
     * 
     * @return NetworkStateService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static NetworkStateService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NetworkStateService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * 检查网络是否可用
     * 
     * @return true表示网络可用，false表示网络不可用
     */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            // 在某些设备上可能需要权限，静默处理异常
        }
        return false;
    }

    /**
     * 检查WiFi是否连接
     * 
     * @return true表示WiFi已连接，false表示WiFi未连接
     */
    public boolean isWifiConnected() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiInfo != null && wifiInfo.isConnected();
            }
        } catch (Exception e) {
            // 静默处理异常
        }
        return false;
    }

    /**
     * 检查移动网络是否连接
     * 
     * @return true表示移动网络已连接，false表示移动网络未连接
     */
    public boolean isMobileConnected() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return mobileInfo != null && mobileInfo.isConnected();
            }
        } catch (Exception e) {
            // 静默处理异常
        }
        return false;
    }

    /**
     * 显示网络错误提示（防抖动）
     * 在短时间内多次调用只会显示一次提示
     * 
     * @param message 错误消息
     */
    public void showNetworkError(final String message) {
        // 取消之前的提示
        if (lastToastRunnable != null) {
            debounceHandler.removeCallbacks(lastToastRunnable);
        }

        // 创建新的提示任务
        lastToastRunnable = new Runnable() {
            @Override
            public void run() {
                showToast(message);
            }
        };

        // 延迟执行，防止短时间内重复提示
        debounceHandler.postDelayed(lastToastRunnable, 1000);
    }

    /**
     * 显示网络不可用提示
     * 使用默认的错误消息
     */
    public void showNetworkUnavailableError() {
        showNetworkError("网络连接不可用，请检查网络设置");
    }

    /**
     * 显示服务器错误提示
     * 使用默认的错误消息
     */
    public void showServerError() {
        showNetworkError("服务器连接失败，请稍后重试");
    }

    /**
     * 显示超时错误提示
     * 使用默认的错误消息
     */
    public void showTimeoutError() {
        showNetworkError("网络连接超时，请检查网络后重试");
    }

    /**
     * 显示数据加载错误提示
     * 使用默认的错误消息
     */
    public void showDataLoadError() {
        showNetworkError("数据加载失败，请稍后重试");
    }

    /**
     * 显示操作失败提示
     * 使用默认的错误消息
     */
    public void showOperationFailedError() {
        showNetworkError("操作失败，请稍后重试");
    }

    /**
     * 显示成功提示
     * 
     * @param message 成功消息
     */
    public void showSuccess(String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示Toast提示
     * 在主线程中执行
     * 
     * @param message 提示消息
     */
    private void showToast(final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取网络类型描述
     * 
     * @return 网络类型描述字符串
     */
    public String getNetworkTypeDescription() {
        if (isWifiConnected()) {
            return "WiFi网络";
        } else if (isMobileConnected()) {
            return "移动网络";
        } else if (isNetworkAvailable()) {
            return "其他网络";
        } else {
            return "无网络连接";
        }
    }

    /**
     * 执行网络操作前的检查
     * 如果网络不可用，会显示错误提示并返回false
     * 
     * @return true表示网络可用，可以继续操作；false表示网络不可用
     */
    public boolean checkNetworkBeforeOperation() {
        if (!isNetworkAvailable()) {
            showNetworkUnavailableError();
            return false;
        }
        return true;
    }

    /**
     * 执行网络操作前的检查
     * 如果网络不可用，会显示自定义错误消息并返回false
     * 
     * @param customErrorMessage 自定义错误消息
     * @return true表示网络可用，可以继续操作；false表示网络不可用
     */
    public boolean checkNetworkBeforeOperation(String customErrorMessage) {
        if (!isNetworkAvailable()) {
            showNetworkError(customErrorMessage);
            return false;
        }
        return true;
    }

    /**
     * 网络状态监听器接口
     */
    public interface NetworkStateListener {
        /**
         * 网络状态变化回调
         * 
         * @param isAvailable 网络是否可用
         */
        void onNetworkStateChanged(boolean isAvailable);
    }

    /**
     * 清理资源
     * 在应用退出时调用
     */
    public void cleanup() {
        if (debounceHandler != null && lastToastRunnable != null) {
            debounceHandler.removeCallbacks(lastToastRunnable);
        }
    }
}