package com.motionrivalry.rowmasterpro.sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * 传感器数据缓存
 * 管理传感器数据的历史记录，支持环形缓冲区和数据窗口
 */
public class SensorDataCache {
    
    private static final String TAG = "SensorDataCache";
    
    // 数据缓存
    private List<SensorData> dataCache;
    private int maxSize;
    
    // 环形缓冲区索引
    private int writeIndex;
    private boolean isFull;
    
    // 统计信息
    private long totalDataCount;
    private long cacheHits;
    private long cacheMisses;
    
    /**
     * 构造函数
     * 
     * @param maxSize 最大缓存大小
     */
    public SensorDataCache(int maxSize) {
        this.maxSize = maxSize;
        this.dataCache = new ArrayList<>(maxSize);
        this.writeIndex = 0;
        this.isFull = false;
        
        this.totalDataCount = 0;
        this.cacheHits = 0;
        this.cacheMisses = 0;
        
        // 预分配空间
        for (int i = 0; i < maxSize; i++) {
            dataCache.add(new SensorData());
        }
    }
    
    /**
     * 添加数据到缓存
     * 
     * @param data 传感器数据
     */
    public void addData(SensorData data) {
        if (data == null) {
            return;
        }
        
        // 复制数据到缓存位置
        SensorData cacheData = dataCache.get(writeIndex);
        copyData(data, cacheData);
        
        // 更新写入索引
        writeIndex = (writeIndex + 1) % maxSize;
        
        if (writeIndex == 0) {
            isFull = true;
        }
        
        totalDataCount++;
    }
    
    /**
     * 获取最新数据
     * 
     * @return 最新数据，如果缓存为空则返回null
     */
    public SensorData getLatestData() {
        if (isEmpty()) {
            cacheMisses++;
            return null;
        }
        
        cacheHits++;
        
        int latestIndex = isFull ? (writeIndex - 1 + maxSize) % maxSize : 
                                  (writeIndex - 1 + maxSize) % maxSize;
        
        return dataCache.get(latestIndex);
    }
    
    /**
     * 获取指定索引的数据
     * 
     * @param index 索引（0表示最新，1表示前一个，以此类推）
     * @return 指定索引的数据
     */
    public SensorData getData(int index) {
        if (index < 0 || index >= getDataCount()) {
            cacheMisses++;
            return null;
        }
        
        cacheHits++;
        
        int actualIndex = isFull ? 
            (writeIndex - 1 - index + maxSize) % maxSize :
            (writeIndex - 1 - index + maxSize) % maxSize;
        
        return dataCache.get(actualIndex);
    }
    
    /**
     * 获取最近N个数据
     * 
     * @param count 数据数量
     * @return 数据列表
     */
    public List<SensorData> getRecentData(int count) {
        List<SensorData> result = new ArrayList<>();
        
        int dataCount = getDataCount();
        int actualCount = Math.min(count, dataCount);
        
        for (int i = 0; i < actualCount; i++) {
            SensorData data = getData(i);
            if (data != null) {
                result.add(data);
            }
        }
        
        return result;
    }
    
    /**
     * 获取数据窗口
     * 
     * @param windowSize 窗口大小
     * @return 数据窗口
     */
    public List<SensorData> getDataWindow(int windowSize) {
        return getRecentData(windowSize);
    }
    
    /**
     * 获取时间窗口内的数据
     * 
     * @param timeWindow 时间窗口（毫秒）
     * @return 时间窗口内的数据
     */
    public List<SensorData> getTimeWindowData(long timeWindow) {
        List<SensorData> result = new ArrayList<>();
        
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - timeWindow;
        
        for (int i = 0; i < getDataCount(); i++) {
            SensorData data = getData(i);
            if (data != null && data.timestamp >= startTime) {
                result.add(data);
            }
        }
        
        return result;
    }
    
    /**
     * 计算数据窗口的平均值
     * 
     * @param windowSize 窗口大小
     * @param dataType 数据类型（"speed", "strokeRate", "heartRate"等）
     * @return 平均值
     */
    public double calculateWindowAverage(int windowSize, String dataType) {
        List<SensorData> windowData = getDataWindow(windowSize);
        
        if (windowData.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        int count = 0;
        
        for (SensorData data : windowData) {
            double value = getDataValue(data, dataType);
            if (!Double.isNaN(value)) {
                sum += value;
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0;
    }
    
    /**
     * 计算数据窗口的标准差
     * 
     * @param windowSize 窗口大小
     * @param dataType 数据类型
     * @return 标准差
     */
    public double calculateWindowStandardDeviation(int windowSize, String dataType) {
        List<SensorData> windowData = getDataWindow(windowSize);
        
        if (windowData.isEmpty()) {
            return 0;
        }
        
        double average = calculateWindowAverage(windowSize, dataType);
        double sumSquaredDiff = 0;
        int count = 0;
        
        for (SensorData data : windowData) {
            double value = getDataValue(data, dataType);
            if (!Double.isNaN(value)) {
                double diff = value - average;
                sumSquaredDiff += diff * diff;
                count++;
            }
        }
        
        return count > 0 ? Math.sqrt(sumSquaredDiff / count) : 0;
    }
    
    /**
     * 获取数据值
     * 
     * @param data 传感器数据
     * @param dataType 数据类型
     * @return 数据值
     */
    private double getDataValue(SensorData data, String dataType) {
        switch (dataType.toLowerCase()) {
            case "speed":
            case "boatspeed":
                return data.boatSpeed;
            case "strokerate":
                return data.strokeRate;
            case "heartrate":
                return data.heartRate;
            case "strokecount":
                return data.strokeCount;
            case "yaw":
            case "boatyaw":
                return data.boatYaw;
            case "roll":
            case "boatroll":
                return data.boatRoll;
            case "pitch":
            case "boatpitch":
                return data.boatPitch;
            default:
                return Double.NaN;
        }
    }
    
    /**
     * 复制数据
     * 
     * @param source 源数据
     * @param destination 目标数据
     */
    private void copyData(SensorData source, SensorData destination) {
        destination.timestamp = source.timestamp;
        destination.processingTime = source.processingTime;
        
        // 复制数组数据
        if (source.accelerometerRaw != null) {
            System.arraycopy(source.accelerometerRaw, 0, destination.accelerometerRaw, 0, source.accelerometerRaw.length);
        }
        if (source.accelerometerSmoothed != null) {
            System.arraycopy(source.accelerometerSmoothed, 0, destination.accelerometerSmoothed, 0, source.accelerometerSmoothed.length);
        }
        if (source.magnetometer != null) {
            System.arraycopy(source.magnetometer, 0, destination.magnetometer, 0, source.magnetometer.length);
        }
        if (source.linearAcceleration != null) {
            System.arraycopy(source.linearAcceleration, 0, destination.linearAcceleration, 0, source.linearAcceleration.length);
        }
        if (source.rotationVector != null) {
            System.arraycopy(source.rotationVector, 0, destination.rotationVector, 0, source.rotationVector.length);
        }
        
        // 复制时间戳
        destination.accelerometerTime = source.accelerometerTime;
        destination.magnetometerTime = source.magnetometerTime;
        destination.linearAccelerationTime = source.linearAccelerationTime;
        destination.rotationVectorTime = source.rotationVectorTime;
        destination.orientationTime = source.orientationTime;
        destination.gpsTime = source.gpsTime;
        destination.heartRateTime = source.heartRateTime;
        destination.lastStrokeTime = source.lastStrokeTime;
        destination.recordingStartTime = source.recordingStartTime;
        
        // 复制数值数据
        destination.boatYaw = source.boatYaw;
        destination.boatRoll = source.boatRoll;
        destination.boatPitch = source.boatPitch;
        destination.latitude = source.latitude;
        destination.longitude = source.longitude;
        destination.altitude = source.altitude;
        destination.gpsAccuracy = source.gpsAccuracy;
        destination.gpsSignalQuality = source.gpsSignalQuality;
        destination.boatSpeed = source.boatSpeed;
        destination.boatSpeedRaw = source.boatSpeedRaw;
        destination.boatSpeedMax = source.boatSpeedMax;
        destination.strokeRate = source.strokeRate;
        destination.strokeRateSmoothed = source.strokeRateSmoothed;
        destination.strokeCount = source.strokeCount;
        destination.heartRate = source.heartRate;
        destination.heartRateDeviceId = source.heartRateDeviceId;
        
        // 复制状态
        destination.rowingState = source.rowingState;
        destination.isRecording = source.isRecording;
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        writeIndex = 0;
        isFull = false;
        
        for (SensorData data : dataCache) {
            data.reset();
        }
        
        cacheHits = 0;
        cacheMisses = 0;
    }
    
    /**
     * 获取缓存中的数据数量
     * 
     * @return 数据数量
     */
    public int getDataCount() {
        return isFull ? maxSize : writeIndex;
    }
    
    /**
     * 检查缓存是否为空
     * 
     * @return true表示为空
     */
    public boolean isEmpty() {
        return getDataCount() == 0;
    }
    
    /**
     * 检查缓存是否已满
     * 
     * @return true表示已满
     */
    public boolean isFull() {
        return isFull;
    }
    
    /**
     * 获取最大容量
     * 
     * @return 最大容量
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * 获取缓存命中率
     * 
     * @return 命中率（0-1）
     */
    public double getHitRate() {
        long total = cacheHits + cacheMisses;
        return total > 0 ? (double) cacheHits / total : 0;
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        return String.format("数据缓存统计 - 容量: %d, 数量: %d, 命中率: %.1f%%, " +
                           "总数据: %d, 命中: %d, 未命中: %d",
                maxSize, getDataCount(), getHitRate() * 100,
                totalDataCount, cacheHits, cacheMisses);
    }
    
    @Override
    public String toString() {
        return String.format("SensorDataCache{size=%d/%d, hitRate=%.1f%%}",
                getDataCount(), maxSize, getHitRate() * 100);
    }
}