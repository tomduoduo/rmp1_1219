package com.motionrivalry.rowmasterpro.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * 设备信息模型类
 * 用于存储和传输设备相关信息
 */
public class DeviceInfo {

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("deviceName")
    private String deviceName;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("deviceModel")
    private String deviceModel;

    @SerializedName("firmwareVersion")
    private String firmwareVersion;

    @SerializedName("hardwareVersion")
    private String hardwareVersion;

    @SerializedName("macAddress")
    private String macAddress;

    @SerializedName("serialNumber")
    private String serialNumber;

    @SerializedName("manufacturer")
    private String manufacturer;

    @SerializedName("status")
    private String status; // online/offline/maintenance

    @SerializedName("batteryLevel")
    private int batteryLevel; // 电池电量（0-100）

    @SerializedName("lastConnectionTime")
    private long lastConnectionTime;

    @SerializedName("bindTime")
    private long bindTime;

    @SerializedName("createdAt")
    private long createdAt;

    @SerializedName("updatedAt")
    private long updatedAt;

    @SerializedName("settings")
    private String settings; // 设备设置（JSON格式）

    public DeviceInfo() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.status = "offline";
        this.batteryLevel = 0;
    }

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
    }

    public long getLastConnectionTime() {
        return lastConnectionTime;
    }

    public void setLastConnectionTime(long lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
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

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    /**
     * 更新更新时间戳
     */
    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 检查设备是否在线
     * 
     * @return true表示在线，false表示离线
     */
    public boolean isOnline() {
        return "online".equals(status);
    }

    /**
     * 检查设备是否离线
     * 
     * @return true表示离线，false表示在线
     */
    public boolean isOffline() {
        return "offline".equals(status);
    }

    /**
     * 检查设备是否维护中
     * 
     * @return true表示维护中，false表示正常
     */
    public boolean isMaintenance() {
        return "maintenance".equals(status);
    }

    /**
     * 获取电池电量描述
     * 
     * @return 电池电量描述字符串
     */
    public String getBatteryLevelDescription() {
        if (batteryLevel == 100) {
            return "电量充足";
        } else if (batteryLevel >= 80) {
            return "电量良好";
        } else if (batteryLevel >= 50) {
            return "电量中等";
        } else if (batteryLevel >= 20) {
            return "电量较低";
        } else if (batteryLevel > 0) {
            return "电量不足";
        } else {
            return "电量未知";
        }
    }

    /**
     * 获取状态描述
     * 
     * @return 状态描述字符串
     */
    public String getStatusDescription() {
        switch (status) {
            case "online":
                return "在线";
            case "offline":
                return "离线";
            case "maintenance":
                return "维护中";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取设备类型描述
     * 
     * @return 设备类型描述字符串
     */
    public String getDeviceTypeDescription() {
        if (deviceType == null) {
            return "未知设备";
        }

        switch (deviceType) {
            case "speedcoach":
                return "SpeedCoach GPS";
            case "rowing_machine":
                return "划船机";
            case "kayak_sensor":
                return "皮划艇传感器";
            case "heart_rate_monitor":
                return "心率监测器";
            default:
                return deviceType;
        }
    }

    /**
     * 检查是否需要固件更新
     * 
     * @param targetVersion 目标版本
     * @return true表示需要更新，false表示不需要更新
     */
    public boolean needsFirmwareUpdate(String targetVersion) {
        if (firmwareVersion == null || targetVersion == null) {
            return false;
        }

        // 简单的版本比较，实际应用中可能需要更复杂的版本号比较逻辑
        return !firmwareVersion.equals(targetVersion);
    }

    /**
     * 获取最后连接时间的描述
     * 
     * @return 最后连接时间描述字符串
     */
    public String getLastConnectionTimeDescription() {
        if (lastConnectionTime <= 0) {
            return "从未连接";
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastConnectionTime;

        if (timeDiff < 60000) { // 1分钟内
            return "刚刚连接";
        } else if (timeDiff < 3600000) { // 1小时内
            int minutes = (int) (timeDiff / 60000);
            return minutes + "分钟前连接";
        } else if (timeDiff < 86400000) { // 1天内
            int hours = (int) (timeDiff / 3600000);
            return hours + "小时前连接";
        } else { // 超过1天
            int days = (int) (timeDiff / 86400000);
            return days + "天前连接";
        }
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", status='" + status + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", lastConnectionTime=" + lastConnectionTime +
                '}';
    }
}