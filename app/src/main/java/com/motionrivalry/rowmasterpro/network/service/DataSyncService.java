package com.motionrivalry.rowmasterpro.network.service;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.motionrivalry.rowmasterpro.network.NetworkManager;
import com.motionrivalry.rowmasterpro.network.callback.NetworkCallback;
import com.motionrivalry.rowmasterpro.network.model.TrainingData;
import com.motionrivalry.rowmasterpro.network.model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据同步服务类
 * 负责本地数据与服务器数据的同步管理
 * 提供自动同步、手动同步、冲突解决等功能
 */
public class DataSyncService {

    private static final String TAG = "DataSyncService";
    private static DataSyncService instance;

    private final Context applicationContext;
    private final UserService userService;
    private final TrainingDataService trainingDataService;
    private final LocalDataService localDataService;
    private final NetworkStateService networkStateService;

    // 同步状态
    private boolean isSyncing = false;
    private long lastSyncTime = 0;
    private static final long SYNC_INTERVAL = 5 * 60 * 1000; // 5分钟同步间隔

    /**
     * 私有构造函数，初始化服务和依赖
     */
    private DataSyncService(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.userService = UserService.getInstance();
        this.trainingDataService = TrainingDataService.getInstance();
        this.localDataService = LocalDataService.getInstance();
        this.networkStateService = NetworkStateService.getInstance();
    }

    /**
     * 初始化数据同步服务
     * 必须在Application中调用一次
     * 
     * @param context 应用上下文
     */
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new DataSyncService(context);
        }
    }

    /**
     * 获取数据同步服务单例实例
     * 
     * @return DataSyncService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static DataSyncService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DataSyncService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * 执行完整的数据同步
     * 包括用户数据同步和训练数据同步
     * 
     * @param callback 同步结果回调
     */
    public void performFullSync(NetworkCallback<Void> callback) {
        if (isSyncing) {
            handleError(callback, -1, "同步正在进行中");
            return;
        }

        if (!userService.isLoggedIn()) {
            handleError(callback, 401, "用户未登录");
            return;
        }

        if (!networkStateService.checkNetworkBeforeOperation("同步需要网络连接")) {
            handleError(callback, -1, "网络不可用");
            return;
        }

        isSyncing = true;
        Log.i(TAG, "开始完整数据同步");

        if (callback != null) {
            callback.onStart();
        }

        // 1. 同步用户数据
        syncUserData(new NetworkCallback<UserData>() {
            @Override
            public void onSuccess(UserData userData) {
                // 用户数据同步成功，继续同步训练数据
                syncTrainingData(new NetworkCallback<List<TrainingData>>() {
                    @Override
                    public void onSuccess(List<TrainingData> trainingDataList) {
                        // 训练数据同步成功，更新同步时间
                        updateSyncTimestamp();
                        isSyncing = false;

                        handleSuccess(callback, null, "数据同步成功");
                        Log.i(TAG, "完整数据同步完成");
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        isSyncing = false;
                        handleError(callback, errorCode, "训练数据同步失败: " + errorMessage);
                    }

                    @Override
                    public void onComplete() {
                        // 由外部回调处理
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                isSyncing = false;
                handleError(callback, errorCode, "用户数据同步失败: " + errorMessage);
            }

            @Override
            public void onComplete() {
                // 由外部回调处理
            }
        });
    }

    /**
     * 同步用户数据
     * 从服务器获取最新的用户信息并更新本地缓存
     * 
     * @param callback 同步结果回调
     */
    private void syncUserData(NetworkCallback<UserData> callback) {
        Log.i(TAG, "开始同步用户数据");

        userService.getUserInfo(new NetworkCallback<UserData>() {
            @Override
            public void onSuccess(UserData userData) {
                // 更新本地缓存
                localDataService.saveUserData(userData);
                userService.updateLocalUser(userData);

                Log.i(TAG, "用户数据同步成功");
                if (callback != null) {
                    callback.onSuccess(userData);
                    callback.onComplete();
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "用户数据同步失败: " + errorMessage);
                if (callback != null) {
                    callback.onError(errorCode, errorMessage);
                    callback.onComplete();
                }
            }

            @Override
            public void onStart() {
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            public void onComplete() {
                // 由内部处理
            }
        });
    }

    /**
     * 同步训练数据
     * 上传本地未同步的数据，下载服务器的新数据
     * 
     * @param callback 同步结果回调
     */
    private void syncTrainingData(NetworkCallback<List<TrainingData>> callback) {
        Log.i(TAG, "开始同步训练数据");

        // 1. 获取本地未同步的数据
        List<TrainingData> unsyncedData = localDataService.getUnsyncedTrainingData();

        if (unsyncedData.isEmpty()) {
            Log.i(TAG, "没有需要上传的训练数据");
            // 直接下载新数据
            downloadNewTrainingData(callback);
        } else {
            // 2. 上传未同步的数据
            uploadUnsyncedData(unsyncedData, callback);
        }
    }

    /**
     * 上传未同步的训练数据
     * 
     * @param unsyncedData 未同步数据列表
     * @param callback     上传结果回调
     */
    private void uploadUnsyncedData(List<TrainingData> unsyncedData, NetworkCallback<List<TrainingData>> callback) {
        Log.i(TAG, "开始上传未同步的训练数据: " + unsyncedData.size() + "条");

        trainingDataService.uploadTrainingDataBatch(unsyncedData, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "训练数据上传成功: " + result);

                // 标记本地数据为已同步
                for (TrainingData data : unsyncedData) {
                    data.markAsSynced();
                    localDataService.updateTrainingData(data);
                }

                // 继续下载新数据
                downloadNewTrainingData(callback);
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "训练数据上传失败: " + errorMessage);

                // 标记为同步失败
                for (TrainingData data : unsyncedData) {
                    data.markAsSyncFailed();
                    localDataService.updateTrainingData(data);
                }

                // 即使上传失败，也尝试下载新数据
                downloadNewTrainingData(callback);
            }

            @Override
            public void onStart() {
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            public void onComplete() {
                // 由后续操作处理
            }
        });
    }

    /**
     * 下载新的训练数据
     * 
     * @param callback 下载结果回调
     */
    private void downloadNewTrainingData(NetworkCallback<List<TrainingData>> callback) {
        Log.i(TAG, "开始下载新的训练数据");

        long lastSyncTime = localDataService.getSyncTimestamp();

        // 创建同步请求数据
        Map<String, Object> syncRequest = new HashMap<>();
        syncRequest.put("lastSyncTime", lastSyncTime);
        syncRequest.put("userId", userService.getCurrentUser() != null ? userService.getCurrentUser().getUserId() : "");

        trainingDataService.syncTrainingData(syncRequest, new NetworkCallback<List<TrainingData>>() {
            @Override
            public void onSuccess(List<TrainingData> newData) {
                Log.i(TAG, "新训练数据下载成功: " + newData.size() + "条");

                // 保存新数据到本地
                if (!newData.isEmpty()) {
                    localDataService.saveTrainingDataBatch(newData);
                }

                if (callback != null) {
                    callback.onSuccess(newData);
                    callback.onComplete();
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "新训练数据下载失败: " + errorMessage);

                if (callback != null) {
                    callback.onError(errorCode, errorMessage);
                    callback.onComplete();
                }
            }

            @Override
            public void onStart() {
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            public void onComplete() {
                // 由内部处理
            }
        });
    }

    /**
     * 自动同步检查
     * 如果满足同步条件（用户已登录、网络可用、同步间隔到达），则执行同步
     * 
     * @param callback 同步结果回调（可选）
     */
    public void checkAutoSync(NetworkCallback<Void> callback) {
        if (!shouldAutoSync()) {
            return;
        }

        Log.i(TAG, "满足自动同步条件，开始同步");
        performFullSync(callback != null ? callback : new NetworkCallback<Void>() {
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
                // 静默处理
            }
        });
    }

    /**
     * 检查是否应该自动同步
     * 
     * @return true表示应该同步，false表示不应该同步
     */
    private boolean shouldAutoSync() {
        // 检查是否正在同步
        if (isSyncing) {
            return false;
        }

        // 检查用户登录状态
        if (!userService.isLoggedIn()) {
            return false;
        }

        // 检查网络状态
        if (!networkStateService.isNetworkAvailable()) {
            return false;
        }

        // 检查同步间隔
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSyncTime < SYNC_INTERVAL) {
            return false;
        }

        // 检查是否有未同步的数据
        List<TrainingData> unsyncedData = localDataService.getUnsyncedTrainingData();
        if (!unsyncedData.isEmpty()) {
            return true;
        }

        // 检查是否到达同步时间
        return true;
    }

    /**
     * 更新同步时间戳
     */
    private void updateSyncTimestamp() {
        lastSyncTime = System.currentTimeMillis();
        localDataService.saveSyncTimestamp(lastSyncTime);
        Log.i(TAG, "同步时间戳已更新: " + lastSyncTime);
    }

    /**
     * 获取同步状态
     * 
     * @return true表示正在同步，false表示未在同步
     */
    public boolean isSyncing() {
        return isSyncing;
    }

    /**
     * 获取上次同步时间
     * 
     * @return 上次同步时间戳
     */
    public long getLastSyncTime() {
        return lastSyncTime;
    }

    /**
     * 获取同步统计信息
     * 
     * @return 同步统计信息字符串
     */
    public String getSyncStatistics() {
        int[] stats = localDataService.getTrainingDataStatistics();
        return String.format("同步统计 - 上次同步: %s, 训练数据: %d条(已同步: %d, 未同步: %d), 当前状态: %s",
                lastSyncTime > 0 ? new java.util.Date(lastSyncTime).toString() : "从未",
                stats[0], stats[1], stats[2],
                isSyncing ? "同步中" : "未同步");
    }

    /**
     * 处理成功响应
     */
    private <T> void handleSuccess(NetworkCallback<T> callback, T data, String message) {
        Log.i(TAG, message);

        if (callback != null) {
            mainHandler.post(() -> {
                callback.onSuccess(data);
                callback.onComplete();
            });
        }
    }

    /**
     * 处理错误响应
     */
    private <T> void handleError(NetworkCallback<T> callback, int errorCode, String errorMessage) {
        Log.e(TAG, "Error " + errorCode + ": " + errorMessage);

        if (callback != null) {
            mainHandler.post(() -> {
                callback.onError(errorCode, errorMessage);
                callback.onComplete();
            });
        }
    }

    // 主线程处理器
    private final Handler mainHandler = new Handler(android.os.Looper.getMainLooper());
}