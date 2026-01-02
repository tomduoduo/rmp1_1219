package com.motionrivalry.rowmasterpro.network.service;

import android.app.Application;
import android.util.Log;

import com.motionrivalry.rowmasterpro.network.callback.NetworkCallback;
import com.motionrivalry.rowmasterpro.network.model.DeviceInfo;
import com.motionrivalry.rowmasterpro.network.model.TrainingData;
import com.motionrivalry.rowmasterpro.network.model.UserData;

import java.util.List;

/**
 * 网络层使用示例
 * 展示如何在Activity中使用网络服务
 */
public class NetworkUsageExample {

    private static final String TAG = "NetworkUsageExample";

    /**
     * 在Application中初始化网络服务
     */
    public static void initializeInApplication(Application application) {
        try {
            // 初始化网络服务管理器
            NetworkServiceManager.initialize(application);
            Log.i(TAG, "网络服务初始化完成");

            // 可以在这里进行网络配置
            // NetworkManager.getInstance().setEnvironment("production");

        } catch (Exception e) {
            Log.e(TAG, "网络服务初始化失败", e);
        }
    }

    /**
     * 用户登录示例
     */
    public static void loginExample() {
        // 获取用户服务
        UserService userService = NetworkServiceManager.getUserService();

        userService.login("username", "password", new NetworkCallback<UserData>() {
            @Override
            public void onStart() {
                // 显示加载对话框
                Log.i(TAG, "开始登录...");
            }

            @Override
            public void onSuccess(UserData userData) {
                // 登录成功，保存用户信息
                Log.i(TAG, "登录成功: " + userData.getUsername());

                // 可以在这里跳转到主界面
                // startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                // 显示错误提示
                Log.e(TAG, "登录失败: " + errorMessage);

                // 显示Toast提示用户
                // Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                // 隐藏加载对话框
                Log.i(TAG, "登录操作完成");
            }
        });
    }

    /**
     * 上传训练数据示例
     */
    public static void uploadTrainingDataExample() {
        // 获取训练数据服务
        TrainingDataService trainingDataService = NetworkServiceManager.getTrainingDataService();

        // 创建训练数据
        TrainingData trainingData = new TrainingData();
        trainingData.setTrainingDate("2024-01-15");
        trainingData.setDuration(3600); // 1小时
        trainingData.setDistance(5000.0); // 5公里
        trainingData.setStrokeRate(25.5); // 桨频
        trainingData.setCalories(350);

        // 上传数据
        trainingDataService.uploadTrainingData(trainingData, new NetworkCallback<String>() {
            @Override
            public void onStart() {
                Log.i(TAG, "开始上传训练数据...");
            }

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "训练数据上传成功: " + result);

                // 可以显示成功提示
                // Toast.makeText(context, "数据上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "训练数据上传失败: " + errorMessage);

                // 保存到本地，稍后同步
                LocalDataService.getInstance().saveTrainingData(trainingData);

                // 显示错误提示
                // Toast.makeText(context, "数据保存到本地，将在网络可用时上传", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "训练数据上传操作完成");
            }
        });
    }

    /**
     * 数据同步示例
     */
    public static void syncDataExample() {
        // 获取数据同步服务
        DataSyncService syncService = NetworkServiceManager.getDataSyncService();

        // 执行完整同步
        syncService.performFullSync(new NetworkCallback<Void>() {
            @Override
            public void onStart() {
                Log.i(TAG, "开始数据同步...");
            }

            @Override
            public void onSuccess(Void result) {
                Log.i(TAG, "数据同步成功");

                // 显示成功提示
                // Toast.makeText(context, "数据同步完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "数据同步失败: " + errorMessage);

                // 显示错误提示
                // Toast.makeText(context, "数据同步失败: " + errorMessage,
                // Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "数据同步操作完成");
            }
        });
    }

    /**
     * 网络状态检查示例
     */
    public static void checkNetworkStatusExample() {
        // 获取网络状态服务
        NetworkStateService stateService = NetworkServiceManager.getNetworkStateService();

        // 检查网络状态
        if (stateService.isNetworkAvailable()) {
            Log.i(TAG, "网络可用: " + stateService.getNetworkTypeDescription());

            // 执行需要网络的操作
            // performNetworkOperation();
        } else {
            Log.w(TAG, "网络不可用");

            // 显示网络不可用提示
            stateService.showNetworkUnavailableError();
        }
    }

    /**
     * 获取服务状态示例
     */
    public static void getServiceStatusExample() {
        // 获取所有服务状态
        String status = NetworkServiceManager.getServiceStatus();
        Log.i(TAG, "服务状态:\n" + status);

        // 可以显示在界面上
        // statusTextView.setText(status);
    }

    /**
     * 错误处理示例
     */
    public static void handleNetworkErrorExample() {
        // 获取网络状态服务
        NetworkStateService stateService = NetworkServiceManager.getNetworkStateService();

        // 根据不同的错误类型显示不同的提示
        // 网络错误
        stateService.showNetworkError("网络连接失败，请检查网络设置");

        // 或者使用预设的错误提示
        stateService.showServerError(); // 服务器错误
        stateService.showTimeoutError(); // 超时错误
        stateService.showDataLoadError(); // 数据加载错误
        stateService.showOperationFailedError(); // 操作失败
    }

    /**
     * 本地数据操作示例
     */
    public static void localDataOperationExample() {
        // 获取本地数据服务
        LocalDataService localService = NetworkServiceManager.getLocalDataService();

        // 获取训练数据统计
        int[] stats = localService.getTrainingDataStatistics();
        Log.i(TAG, String.format("训练数据统计 - 总数: %d, 已同步: %d, 未同步: %d",
                stats[0], stats[1], stats[2]));

        // 获取最近的训练数据
        List<TrainingData> recentData = localService.getRecentTrainingData(10);
        Log.i(TAG, "最近的训练数据: " + recentData.size() + "条");

        // 获取未同步的数据
        List<TrainingData> unsyncedData = localService.getUnsyncedTrainingData();
        Log.i(TAG, "未同步的训练数据: " + unsyncedData.size() + "条");
    }

    /**
     * 设备绑定示例
     */
    public static void bindDeviceExample() {
        // 获取设备服务
        DeviceService deviceService = NetworkServiceManager.getDeviceService();

        // 绑定设备
        deviceService.bindDevice("device123", "我的划船机", "rowing_machine", new NetworkCallback<DeviceInfo>() {
            @Override
            public void onStart() {
                Log.i(TAG, "开始绑定设备...");
            }

            @Override
            public void onSuccess(DeviceInfo deviceInfo) {
                Log.i(TAG, "设备绑定成功: " + deviceInfo.getDeviceName());

                // 显示成功提示
                NetworkStateService.getInstance().showSuccess("设备绑定成功");
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "设备绑定失败: " + errorMessage);

                // 显示错误提示
                NetworkStateService.getInstance().showNetworkError("设备绑定失败: " + errorMessage);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "设备绑定操作完成");
            }
        });
    }

    /**
     * 自动同步示例
     */
    public static void autoSyncExample() {
        // 获取数据同步服务
        DataSyncService syncService = NetworkServiceManager.getDataSyncService();

        // 检查是否应该自动同步
        syncService.checkAutoSync(new NetworkCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.i(TAG, "自动同步成功");
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.w(TAG, "自动同步失败: " + errorMessage);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "自动同步检查完成");
            }
        });
    }
}