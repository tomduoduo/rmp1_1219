package com.motionrivalry.rowmasterpro.network.service;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 网络服务管理器
 * 统一管理所有网络服务的初始化
 * 必须在Application中初始化
 */
public class NetworkServiceManager {

    private static final String TAG = "NetworkServiceManager";
    private static boolean isInitialized = false;

    /**
     * 初始化所有网络服务
     * 必须在Application的onCreate()中调用
     * 
     * @param application 应用实例
     */
    public static void initialize(Application application) {
        if (isInitialized) {
            Log.w(TAG, "网络服务已经初始化，重复调用被忽略");
            return;
        }

        try {
            Log.i(TAG, "开始初始化网络服务...");

            // 1. 初始化网络状态服务
            NetworkStateService.initialize(application);
            Log.i(TAG, "网络状态服务初始化完成");

            // 2. 初始化本地数据服务
            LocalDataService.initialize(application);
            Log.i(TAG, "本地数据服务初始化完成");

            // 3. 初始化数据同步服务
            DataSyncService.initialize(application);
            Log.i(TAG, "数据同步服务初始化完成");

            isInitialized = true;
            Log.i(TAG, "所有网络服务初始化完成");

        } catch (Exception e) {
            Log.e(TAG, "网络服务初始化失败", e);
            throw new RuntimeException("网络服务初始化失败", e);
        }
    }

    /**
     * 检查是否已初始化
     * 
     * @return true表示已初始化，false表示未初始化
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * 获取网络状态服务
     * 
     * @return NetworkStateService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static NetworkStateService getNetworkStateService() {
        checkInitialized();
        return NetworkStateService.getInstance();
    }

    /**
     * 获取本地数据服务
     * 
     * @return LocalDataService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static LocalDataService getLocalDataService() {
        checkInitialized();
        return LocalDataService.getInstance();
    }

    /**
     * 获取数据同步服务
     * 
     * @return DataSyncService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static DataSyncService getDataSyncService() {
        checkInitialized();
        return DataSyncService.getInstance();
    }

    /**
     * 获取用户服务
     * 
     * @return UserService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static UserService getUserService() {
        checkInitialized();
        return UserService.getInstance();
    }

    /**
     * 获取训练数据服务
     * 
     * @return TrainingDataService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static TrainingDataService getTrainingDataService() {
        checkInitialized();
        return TrainingDataService.getInstance();
    }

    /**
     * 获取设备服务
     * 
     * @return DeviceService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static DeviceService getDeviceService() {
        checkInitialized();
        return DeviceService.getInstance();
    }

    /**
     * 检查初始化状态
     * 
     * @throws IllegalStateException 如果未初始化
     */
    private static void checkInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("NetworkServiceManager not initialized. Call initialize() first.");
        }
    }

    /**
     * 获取所有服务的状态信息
     * 
     * @return 服务状态信息字符串
     */
    public static String getServiceStatus() {
        if (!isInitialized) {
            return "网络服务管理器未初始化";
        }

        try {
            StringBuilder status = new StringBuilder();
            status.append("网络服务状态:\n");

            // 网络状态服务
            try {
                NetworkStateService stateService = NetworkStateService.getInstance();
                status.append("网络状态: ").append(stateService.isNetworkAvailable() ? "可用" : "不可用");
                status.append(" (").append(stateService.getNetworkTypeDescription()).append(")\n");
            } catch (Exception e) {
                status.append("网络状态: 获取失败\n");
            }

            // 本地数据服务
            try {
                LocalDataService localService = LocalDataService.getInstance();
                status.append(localService.getCacheInfo()).append("\n");
            } catch (Exception e) {
                status.append("本地数据: 获取失败\n");
            }

            // 数据同步服务
            try {
                DataSyncService syncService = DataSyncService.getInstance();
                status.append(syncService.getSyncStatistics()).append("\n");
            } catch (Exception e) {
                status.append("数据同步: 获取失败\n");
            }

            // 用户服务
            try {
                UserService userService = UserService.getInstance();
                status.append("用户状态: ").append(userService.isLoggedIn() ? "已登录" : "未登录");
                if (userService.isLoggedIn() && userService.getCurrentUser() != null) {
                    status.append(" (").append(userService.getCurrentUser().getUsername()).append(")");
                }
                status.append("\n");
            } catch (Exception e) {
                status.append("用户状态: 获取失败\n");
            }

            // 设备服务
            try {
                DeviceService deviceService = DeviceService.getInstance();
                status.append("设备状态: ").append(deviceService.isDeviceBound() ? "已绑定" : "未绑定");
                if (deviceService.isDeviceBound() && deviceService.getCurrentDevice() != null) {
                    status.append(" (").append(deviceService.getCurrentDevice().getDeviceName()).append(")");
                }
                status.append("\n");
            } catch (Exception e) {
                status.append("设备状态: 获取失败\n");
            }

            return status.toString();

        } catch (Exception e) {
            return "获取服务状态失败: " + e.getMessage();
        }
    }

    /**
     * 清理所有服务资源
     * 在应用退出时调用
     */
    public static void cleanup() {
        if (!isInitialized) {
            return;
        }

        try {
            Log.i(TAG, "开始清理网络服务资源...");

            // 清理网络状态服务
            try {
                NetworkStateService.getInstance().cleanup();
            } catch (Exception e) {
                Log.e(TAG, "清理网络状态服务失败", e);
            }

            // 清理本地数据服务
            try {
                // LocalDataService没有cleanup方法，数据会保留
            } catch (Exception e) {
                Log.e(TAG, "清理本地数据服务失败", e);
            }

            // 清理数据同步服务
            try {
                // DataSyncService没有cleanup方法
            } catch (Exception e) {
                Log.e(TAG, "清理数据同步服务失败", e);
            }

            Log.i(TAG, "网络服务资源清理完成");

        } catch (Exception e) {
            Log.e(TAG, "清理网络服务资源失败", e);
        }
    }
}