package com.motionrivalry.rowmasterpro.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * 训练数据模型类
 * 用于存储和传输训练相关数据
 */
public class TrainingData {

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("trainingDate")
    private String trainingDate;

    @SerializedName("duration")
    private int duration; // 训练时长（秒）

    @SerializedName("distance")
    private double distance; // 训练距离（米）

    @SerializedName("strokeRate")
    private double strokeRate; // 桨频（次/分钟）

    @SerializedName("averageSpeed")
    private double averageSpeed; // 平均速度（米/秒）

    @SerializedName("maxSpeed")
    private double maxSpeed; // 最大速度（米/秒）

    @SerializedName("calories")
    private int calories; // 消耗卡路里

    @SerializedName("strokeCount")
    private int strokeCount; // 划桨次数

    @SerializedName("heartRateData")
    private String heartRateData; // 心率数据（JSON格式）

    @SerializedName("gpsData")
    private String gpsData; // GPS数据（JSON格式）

    @SerializedName("accelerationData")
    private String accelerationData; // 加速度数据（JSON格式）

    @SerializedName("createdAt")
    private long createdAt; // 创建时间戳

    @SerializedName("updatedAt")
    private long updatedAt; // 更新时间戳

    @SerializedName("syncStatus")
    private int syncStatus; // 同步状态（0:未同步, 1:已同步, 2:同步失败）

    public TrainingData() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.syncStatus = 0; // 默认未同步
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getStrokeRate() {
        return strokeRate;
    }

    public void setStrokeRate(double strokeRate) {
        this.strokeRate = strokeRate;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(int strokeCount) {
        this.strokeCount = strokeCount;
    }

    public String getHeartRateData() {
        return heartRateData;
    }

    public void setHeartRateData(String heartRateData) {
        this.heartRateData = heartRateData;
    }

    public String getGpsData() {
        return gpsData;
    }

    public void setGpsData(String gpsData) {
        this.gpsData = gpsData;
    }

    public String getAccelerationData() {
        return accelerationData;
    }

    public void setAccelerationData(String accelerationData) {
        this.accelerationData = accelerationData;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    /**
     * 更新更新时间戳
     */
    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 标记为已同步
     */
    public void markAsSynced() {
        this.syncStatus = 1;
        updateTimestamp();
    }

    /**
     * 标记为同步失败
     */
    public void markAsSyncFailed() {
        this.syncStatus = 2;
        updateTimestamp();
    }

    /**
     * 检查是否已同步
     * 
     * @return true表示已同步，false表示未同步
     */
    public boolean isSynced() {
        return syncStatus == 1;
    }

    /**
     * 获取训练时长描述
     * 
     * @return 格式化的时长字符串
     */
    public String getDurationDescription() {
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    /**
     * 获取距离描述
     * 
     * @return 格式化的距离字符串
     */
    public String getDistanceDescription() {
        if (distance >= 1000) {
            return String.format("%.2f公里", distance / 1000);
        } else {
            return String.format("%.0f米", distance);
        }
    }

    /**
     * 获取速度描述
     * 
     * @return 格式化的速度字符串
     */
    public String getSpeedDescription() {
        double speedKmh = averageSpeed * 3.6; // 转换为公里/小时
        return String.format("%.1f公里/小时", speedKmh);
    }

    /**
     * 获取卡路里描述
     * 
     * @return 格式化的卡路里字符串
     */
    public String getCaloriesDescription() {
        return calories + "卡路里";
    }

    /**
     * 获取桨频描述
     * 
     * @return 格式化的桨频字符串
     */
    public String getStrokeRateDescription() {
        return String.format("%.1f次/分钟", strokeRate);
    }

    @Override
    public String toString() {
        return "TrainingData{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", trainingDate='" + trainingDate + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                ", strokeRate=" + strokeRate +
                ", averageSpeed=" + averageSpeed +
                ", maxSpeed=" + maxSpeed +
                ", calories=" + calories +
                ", strokeCount=" + strokeCount +
                ", syncStatus=" + syncStatus +
                '}';
    }
}