package com.motionrivalry.rowmasterpro.network.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.motionrivalry.rowmasterpro.network.model.TrainingData;
import com.motionrivalry.rowmasterpro.network.model.UserData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 本地数据缓存服务类
 * 提供用户数据和训练数据的本地缓存管理
 * 使用SharedPreferences和Gson进行数据持久化
 */
public class LocalDataService {

    private static final String TAG = "LocalDataService";
    private static final String PREF_NAME = "rowmasterpro_data";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_TRAINING_DATA = "training_data";
    private static final String KEY_SYNC_TIMESTAMP = "sync_timestamp";
    private static final String KEY_DEVICE_SETTINGS = "device_settings";

    private static LocalDataService instance;
    private final SharedPreferences preferences;
    private final Gson gson;

    // 内存缓存
    private UserData cachedUserData;
    private final List<TrainingData> cachedTrainingData;
    private long lastSyncTimestamp;

    /**
     * 私有构造函数，初始化SharedPreferences和Gson
     */
    private LocalDataService(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.cachedTrainingData = new CopyOnWriteArrayList<>();
        loadCachedData();
    }

    /**
     * 初始化本地数据服务
     * 必须在Application中调用一次
     * 
     * @param context 应用上下文
     */
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new LocalDataService(context);
        }
    }

    /**
     * 获取本地数据服务单例实例
     * 
     * @return LocalDataService实例
     * @throws IllegalStateException 如果未初始化
     */
    public static LocalDataService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LocalDataService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * 保存用户数据
     * 
     * @param userData 用户数据
     */
    public void saveUserData(UserData userData) {
        if (userData == null) {
            return;
        }

        try {
            String userDataJson = gson.toJson(userData);
            preferences.edit()
                    .putString(KEY_USER_DATA, userDataJson)
                    .apply();

            // 更新内存缓存
            cachedUserData = userData;
            Log.i(TAG, "用户数据已保存");
        } catch (Exception e) {
            Log.e(TAG, "保存用户数据失败", e);
        }
    }

    /**
     * 获取用户数据
     * 
     * @return 用户数据，不存在时返回null
     */
    public UserData getUserData() {
        if (cachedUserData != null) {
            return cachedUserData;
        }

        try {
            String userDataJson = preferences.getString(KEY_USER_DATA, null);
            if (!TextUtils.isEmpty(userDataJson)) {
                cachedUserData = gson.fromJson(userDataJson, UserData.class);
                return cachedUserData;
            }
        } catch (Exception e) {
            Log.e(TAG, "获取用户数据失败", e);
        }

        return null;
    }

    /**
     * 清除用户数据
     */
    public void clearUserData() {
        try {
            preferences.edit()
                    .remove(KEY_USER_DATA)
                    .apply();

            cachedUserData = null;
            Log.i(TAG, "用户数据已清除");
        } catch (Exception e) {
            Log.e(TAG, "清除用户数据失败", e);
        }
    }

    /**
     * 保存训练数据
     * 
     * @param trainingData 训练数据
     */
    public void saveTrainingData(TrainingData trainingData) {
        if (trainingData == null) {
            return;
        }

        try {
            // 添加到内存缓存
            cachedTrainingData.add(trainingData);

            // 保存到SharedPreferences
            saveTrainingDataList();
            Log.i(TAG, "训练数据已保存: " + trainingData.getId());
        } catch (Exception e) {
            Log.e(TAG, "保存训练数据失败", e);
        }
    }

    /**
     * 批量保存训练数据
     * 
     * @param trainingDataList 训练数据列表
     */
    public void saveTrainingDataBatch(List<TrainingData> trainingDataList) {
        if (trainingDataList == null || trainingDataList.isEmpty()) {
            return;
        }

        try {
            // 添加到内存缓存
            cachedTrainingData.addAll(trainingDataList);

            // 保存到SharedPreferences
            saveTrainingDataList();
            Log.i(TAG, "批量训练数据已保存: " + trainingDataList.size() + "条");
        } catch (Exception e) {
            Log.e(TAG, "批量保存训练数据失败", e);
        }
    }

    /**
     * 更新训练数据
     * 
     * @param trainingData 训练数据
     */
    public void updateTrainingData(TrainingData trainingData) {
        if (trainingData == null || TextUtils.isEmpty(trainingData.getId())) {
            return;
        }

        try {
            // 在内存缓存中查找并更新
            boolean found = false;
            for (int i = 0; i < cachedTrainingData.size(); i++) {
                TrainingData data = cachedTrainingData.get(i);
                if (trainingData.getId().equals(data.getId())) {
                    cachedTrainingData.set(i, trainingData);
                    found = true;
                    break;
                }
            }

            if (found) {
                // 保存到SharedPreferences
                saveTrainingDataList();
                Log.i(TAG, "训练数据已更新: " + trainingData.getId());
            } else {
                Log.w(TAG, "未找到要更新的训练数据: " + trainingData.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "更新训练数据失败", e);
        }
    }

    /**
     * 删除训练数据
     * 
     * @param dataId 数据ID
     */
    public void deleteTrainingData(String dataId) {
        if (TextUtils.isEmpty(dataId)) {
            return;
        }

        try {
            // 从内存缓存中删除
            boolean removed = cachedTrainingData.removeIf(data -> dataId.equals(data.getId()));

            if (removed) {
                // 保存到SharedPreferences
                saveTrainingDataList();
                Log.i(TAG, "训练数据已删除: " + dataId);
            } else {
                Log.w(TAG, "未找到要删除的训练数据: " + dataId);
            }
        } catch (Exception e) {
            Log.e(TAG, "删除训练数据失败", e);
        }
    }

    /**
     * 获取所有训练数据
     * 
     * @return 训练数据列表
     */
    public List<TrainingData> getAllTrainingData() {
        return new ArrayList<>(cachedTrainingData);
    }

    /**
     * 获取未同步的训练数据
     * 
     * @return 未同步的训练数据列表
     */
    public List<TrainingData> getUnsyncedTrainingData() {
        List<TrainingData> unsyncedData = new ArrayList<>();
        for (TrainingData data : cachedTrainingData) {
            if (!data.isSynced()) {
                unsyncedData.add(data);
            }
        }
        return unsyncedData;
    }

    /**
     * 获取已同步的训练数据
     * 
     * @return 已同步的训练数据列表
     */
    public List<TrainingData> getSyncedTrainingData() {
        List<TrainingData> syncedData = new ArrayList<>();
        for (TrainingData data : cachedTrainingData) {
            if (data.isSynced()) {
                syncedData.add(data);
            }
        }
        return syncedData;
    }

    /**
     * 获取最近的训练数据
     * 
     * @param count 获取数量
     * @return 最近的训练数据列表
     */
    public List<TrainingData> getRecentTrainingData(int count) {
        if (cachedTrainingData.isEmpty()) {
            return new ArrayList<>();
        }

        int size = cachedTrainingData.size();
        int startIndex = Math.max(0, size - count);
        return new ArrayList<>(cachedTrainingData.subList(startIndex, size));
    }

    /**
     * 清空所有训练数据
     */
    public void clearAllTrainingData() {
        try {
            cachedTrainingData.clear();
            preferences.edit()
                    .remove(KEY_TRAINING_DATA)
                    .apply();
            Log.i(TAG, "所有训练数据已清空");
        } catch (Exception e) {
            Log.e(TAG, "清空训练数据失败", e);
        }
    }

    /**
     * 保存同步时间戳
     * 
     * @param timestamp 时间戳
     */
    public void saveSyncTimestamp(long timestamp) {
        try {
            preferences.edit()
                    .putLong(KEY_SYNC_TIMESTAMP, timestamp)
                    .apply();

            lastSyncTimestamp = timestamp;
            Log.i(TAG, "同步时间戳已保存: " + timestamp);
        } catch (Exception e) {
            Log.e(TAG, "保存同步时间戳失败", e);
        }
    }

    /**
     * 获取同步时间戳
     * 
     * @return 同步时间戳
     */
    public long getSyncTimestamp() {
        if (lastSyncTimestamp == 0) {
            lastSyncTimestamp = preferences.getLong(KEY_SYNC_TIMESTAMP, 0);
        }
        return lastSyncTimestamp;
    }

    /**
     * 获取训练数据统计
     * 
     * @return 统计信息数组 [总条数, 已同步条数, 未同步条数]
     */
    public int[] getTrainingDataStatistics() {
        int total = cachedTrainingData.size();
        int synced = 0;
        int unsynced = 0;

        for (TrainingData data : cachedTrainingData) {
            if (data.isSynced()) {
                synced++;
            } else {
                unsynced++;
            }
        }

        return new int[] { total, synced, unsynced };
    }

    /**
     * 保存设备设置
     * 
     * @param settingsJson 设备设置JSON字符串
     */
    public void saveDeviceSettings(String settingsJson) {
        if (TextUtils.isEmpty(settingsJson)) {
            return;
        }

        try {
            preferences.edit()
                    .putString(KEY_DEVICE_SETTINGS, settingsJson)
                    .apply();
            Log.i(TAG, "设备设置已保存");
        } catch (Exception e) {
            Log.e(TAG, "保存设备设置失败", e);
        }
    }

    /**
     * 获取设备设置
     * 
     * @return 设备设置JSON字符串，不存在时返回null
     */
    public String getDeviceSettings() {
        return preferences.getString(KEY_DEVICE_SETTINGS, null);
    }

    /**
     * 加载缓存数据
     */
    private void loadCachedData() {
        try {
            // 加载用户数据
            String userDataJson = preferences.getString(KEY_USER_DATA, null);
            if (!TextUtils.isEmpty(userDataJson)) {
                cachedUserData = gson.fromJson(userDataJson, UserData.class);
            }

            // 加载训练数据
            String trainingDataJson = preferences.getString(KEY_TRAINING_DATA, null);
            if (!TextUtils.isEmpty(trainingDataJson)) {
                Type listType = new TypeToken<List<TrainingData>>() {
                }.getType();
                List<TrainingData> dataList = gson.fromJson(trainingDataJson, listType);
                if (dataList != null) {
                    cachedTrainingData.addAll(dataList);
                }
            }

            // 加载同步时间戳
            lastSyncTimestamp = preferences.getLong(KEY_SYNC_TIMESTAMP, 0);

            Log.i(TAG, "缓存数据加载完成 - 用户数据: " + (cachedUserData != null ? "有" : "无") +
                    ", 训练数据: " + cachedTrainingData.size() + "条");
        } catch (Exception e) {
            Log.e(TAG, "加载缓存数据失败", e);
        }
    }

    /**
     * 保存训练数据列表到SharedPreferences
     */
    private void saveTrainingDataList() {
        try {
            String trainingDataJson = gson.toJson(cachedTrainingData);
            preferences.edit()
                    .putString(KEY_TRAINING_DATA, trainingDataJson)
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "保存训练数据列表失败", e);
        }
    }

    /**
     * 获取缓存大小信息
     * 
     * @return 缓存信息字符串
     */
    public String getCacheInfo() {
        int[] stats = getTrainingDataStatistics();
        return String.format("本地缓存 - 用户数据: %s, 训练数据: %d条(已同步: %d, 未同步: %d), 上次同步: %s",
                cachedUserData != null ? "有" : "无",
                stats[0], stats[1], stats[2],
                lastSyncTimestamp > 0 ? new java.util.Date(lastSyncTimestamp).toString() : "从未");
    }

    /**
     * 清理所有本地数据
     */
    public void clearAllData() {
        try {
            cachedUserData = null;
            cachedTrainingData.clear();
            lastSyncTimestamp = 0;

            preferences.edit()
                    .clear()
                    .apply();

            Log.i(TAG, "所有本地数据已清理");
        } catch (Exception e) {
            Log.e(TAG, "清理本地数据失败", e);
        }
    }
}