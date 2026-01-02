package com.motionrivalry.rowmasterpro.sensor;

import java.util.HashMap;
import java.util.Map;

/**
 * Polar心率数据处理
 * 处理Polar心率带设备的数据接收和管理
 */
public class PolarDataProcessor {
    
    private static final String TAG = "PolarDataProcessor";
    
    // 心率设备管理
    private Map<String, HeartRateDevice> connectedDevices;
    private String activeDeviceId;
    
    // 心率数据处理
    private int currentHeartRate;
    private int averageHeartRate;
    private int maxHeartRate;
    private int minHeartRate;
    private long heartRateStartTime;
    
    // 统计信息
    private int heartRateSampleCount;
    private long totalHeartRate;
    private int heartRateZone; // 1-5区
    
    // 心率区间定义（基于最大心率的百分比）
    private static final int[] HEART_RATE_ZONES = {
        50,  // 区间1：50-60%
        60,  // 区间2：60-70%
        70,  // 区间3：70-80%
        80,  // 区间4：80-90%
        90   // 区间5：90-100%
    };
    
    /**
     * 心率设备信息
     */
    public static class HeartRateDevice {
        public String deviceId;
        public String deviceName;
        public boolean isConnected;
        public int batteryLevel;
        public long lastUpdateTime;
        public int currentHeartRate;
        
        public HeartRateDevice(String deviceId, String deviceName) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.isConnected = false;
            this.batteryLevel = -1; // -1表示未知
            this.lastUpdateTime = 0;
            this.currentHeartRate = 0;
        }
    }
    
    /**
     * 构造函数
     */
    public PolarDataProcessor() {
        this.connectedDevices = new HashMap<>();
        this.activeDeviceId = null;
        
        this.currentHeartRate = 0;
        this.averageHeartRate = 0;
        this.maxHeartRate = 0;
        this.minHeartRate = Integer.MAX_VALUE;
        this.heartRateStartTime = System.currentTimeMillis();
        
        this.heartRateSampleCount = 0;
        this.totalHeartRate = 0;
        this.heartRateZone = 1;
    }
    
    /**
     * 更新心率数据
     * 
     * @param deviceId 设备ID
     * @param heartRate 心率值
     */
    public void updateHeartRate(String deviceId, int heartRate) {
        if (deviceId == null || heartRate <= 0) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // 更新设备信息
        HeartRateDevice device = connectedDevices.get(deviceId);
        if (device == null) {
            device = new HeartRateDevice(deviceId, "Polar设备");
            connectedDevices.put(deviceId, device);
        }
        
        device.isConnected = true;
        device.currentHeartRate = heartRate;
        device.lastUpdateTime = currentTime;
        
        // 更新当前心率
        this.currentHeartRate = heartRate;
        
        // 更新统计信息
        updateHeartRateStatistics(heartRate);
        
        // 更新心率区间
        updateHeartRateZone(heartRate);
        
        // 如果这是第一个设备或活跃设备，设置为活跃设备
        if (activeDeviceId == null || activeDeviceId.equals(deviceId)) {
            activeDeviceId = deviceId;
        }
    }
    
    /**
     * 添加心率设备
     * 
     * @param deviceId 设备ID
     * @param deviceName 设备名称
     */
    public void addDevice(String deviceId, String deviceName) {
        if (deviceId == null || deviceName == null) {
            return;
        }
        
        HeartRateDevice device = new HeartRateDevice(deviceId, deviceName);
        connectedDevices.put(deviceId, device);
        
        // 如果没有活跃设备，设置为活跃设备
        if (activeDeviceId == null) {
            activeDeviceId = deviceId;
        }
    }
    
    /**
     * 移除心率设备
     * 
     * @param deviceId 设备ID
     */
    public void removeDevice(String deviceId) {
        connectedDevices.remove(deviceId);
        
        // 如果移除的是活跃设备，选择另一个设备
        if (deviceId.equals(activeDeviceId)) {
            if (!connectedDevices.isEmpty()) {
                activeDeviceId = connectedDevices.keySet().iterator().next();
            } else {
                activeDeviceId = null;
            }
        }
    }
    
    /**
     * 设置设备连接状态
     * 
     * @param deviceId 设备ID
     * @param isConnected 是否连接
     */
    public void setDeviceConnectionState(String deviceId, boolean isConnected) {
        HeartRateDevice device = connectedDevices.get(deviceId);
        if (device != null) {
            device.isConnected = isConnected;
            if (!isConnected) {
                device.currentHeartRate = 0;
            }
        }
    }
    
    /**
     * 更新设备电池电量
     * 
     * @param deviceId 设备ID
     * @param batteryLevel 电池电量（0-100）
     */
    public void updateDeviceBattery(String deviceId, int batteryLevel) {
        HeartRateDevice device = connectedDevices.get(deviceId);
        if (device != null) {
            device.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
        }
    }
    
    /**
     * 更新心率统计信息
     * 
     * @param heartRate 心率值
     */
    private void updateHeartRateStatistics(int heartRate) {
        heartRateSampleCount++;
        totalHeartRate += heartRate;
        
        // 更新最大值和最小值
        if (heartRate > maxHeartRate) {
            maxHeartRate = heartRate;
        }
        
        if (heartRate < minHeartRate) {
            minHeartRate = heartRate;
        }
        
        // 计算平均心率
        if (heartRateSampleCount > 0) {
            averageHeartRate = (int) (totalHeartRate / heartRateSampleCount);
        }
    }
    
    /**
     * 更新心率区间
     * 
     * @param heartRate 心率值
     */
    private void updateHeartRateZone(int heartRate) {
        // 基于最大心率的百分比计算区间
        // 这里使用估算的最大心率 = 220 - 年龄（假设年龄为30）
        int maxHR = 190; // 220 - 30
        int percentage = (heartRate * 100) / maxHR;
        
        // 确定区间
        if (percentage < HEART_RATE_ZONES[0]) {
            heartRateZone = 1;
        } else if (percentage < HEART_RATE_ZONES[1]) {
            heartRateZone = 2;
        } else if (percentage < HEART_RATE_ZONES[2]) {
            heartRateZone = 3;
        } else if (percentage < HEART_RATE_ZONES[3]) {
            heartRateZone = 4;
        } else if (percentage < HEART_RATE_ZONES[4]) {
            heartRateZone = 5;
        } else {
            heartRateZone = 5; // 超过90%都算第5区间
        }
    }
    
    /**
     * 获取当前心率
     * 
     * @return 当前心率
     */
    public int getCurrentHeartRate() {
        return currentHeartRate;
    }
    
    /**
     * 获取平均心率
     * 
     * @return 平均心率
     */
    public int getAverageHeartRate() {
        return averageHeartRate;
    }
    
    /**
     * 获取最大心率
     * 
     * @return 最大心率
     */
    public int getMaxHeartRate() {
        return maxHeartRate;
    }
    
    /**
     * 获取最小心率
     * 
     * @return 最小心率
     */
    public int getMinHeartRate() {
        return minHeartRate == Integer.MAX_VALUE ? 0 : minHeartRate;
    }
    
    /**
     * 获取心率区间
     * 
     * @return 心率区间（1-5）
     */
    public int getHeartRateZone() {
        return heartRateZone;
    }
    
    /**
     * 获取活跃设备ID
     * 
     * @return 活跃设备ID
     */
    public String getActiveDeviceId() {
        return activeDeviceId;
    }
    
    /**
     * 设置活跃设备
     * 
     * @param deviceId 设备ID
     */
    public void setActiveDevice(String deviceId) {
        if (connectedDevices.containsKey(deviceId)) {
            this.activeDeviceId = deviceId;
        }
    }
    
    /**
     * 获取已连接设备数量
     * 
     * @return 已连接设备数量
     */
    public int getConnectedDeviceCount() {
        int count = 0;
        for (HeartRateDevice device : connectedDevices.values()) {
            if (device.isConnected) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 获取设备列表
     * 
     * @return 设备列表
     */
    public Map<String, HeartRateDevice> getConnectedDevices() {
        return new HashMap<>(connectedDevices);
    }
    
    /**
     * 获取指定设备信息
     * 
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public HeartRateDevice getDevice(String deviceId) {
        return connectedDevices.get(deviceId);
    }
    
    /**
     * 检查是否有设备连接
     * 
     * @return true表示有设备连接
     */
    public boolean hasConnectedDevice() {
        for (HeartRateDevice device : connectedDevices.values()) {
            if (device.isConnected) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取心率区间描述
     * 
     * @param zone 区间
     * @return 区间描述
     */
    public static String getHeartRateZoneDescription(int zone) {
        switch (zone) {
            case 1:
                return "恢复区间";
            case 2:
                return "有氧基础";
            case 3:
                return "有氧强化";
            case 4:
                return "乳酸阈值";
            case 5:
                return "无氧爆发";
            default:
                return "未知区间";
        }
    }
    
    /**
     * 获取心率统计信息
     * 
     * @return 统计信息字符串
     */
    public String getHeartRateStatistics() {
        return String.format("心率统计 - 当前: %d bpm, 平均: %d bpm, " +
                           "最大: %d bpm, 最小: %d bpm, 区间: %d区 (%s)",
                currentHeartRate, averageHeartRate, maxHeartRate, getMinHeartRate(),
                heartRateZone, getHeartRateZoneDescription(heartRateZone));
    }
    
    /**
     * 重置处理器
     */
    public void reset() {
        currentHeartRate = 0;
        averageHeartRate = 0;
        maxHeartRate = 0;
        minHeartRate = Integer.MAX_VALUE;
        heartRateStartTime = System.currentTimeMillis();
        
        heartRateSampleCount = 0;
        totalHeartRate = 0;
        heartRateZone = 1;
        
        // 重置设备连接状态
        for (HeartRateDevice device : connectedDevices.values()) {
            device.isConnected = false;
            device.currentHeartRate = 0;
        }
    }
    
    @Override
    public String toString() {
        return String.format("PolarDataProcessor{current=%d bpm, avg=%d bpm, devices=%d}",
                currentHeartRate, averageHeartRate, getConnectedDeviceCount());
    }
}