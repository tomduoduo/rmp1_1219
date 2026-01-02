package com.motionrivalry.rowmasterpro.sensor;

/**
 * 处理后的数据模型
 * 存储经过算法处理后的传感器数据
 */
public class ProcessedData {
    
    // 时间戳
    public long processingTime;
    
    // 处理后的传感器数据
    public double processedBoatSpeed;      // 处理后的船速（km/h）
    public double processedStrokeRate;     // 处理后的桨频（次/分钟）
    public double processedBoatYaw;         // 处理后的偏航角（度）
    
    // 统计数据
    public double averageSpeed;            // 平均速度（km/h）
    public double maxSpeed;               // 最大速度（km/h）
    public double averageStrokeRate;      // 平均桨频（次/分钟）
    public double maxStrokeRate;          // 最大桨频（次/分钟）
    
    // 距离和时间
    public double totalDistance;          // 总距离（米）
    public long totalTime;               // 总时间（毫秒）
    public int totalStrokeCount;          // 总划桨次数
    
    // 性能指标
    public double efficiency;             // 效率指标（0-100）
    public double consistency;           // 一致性指标（0-100）
    
    // 状态指标
    public boolean isDataValid;           // 数据是否有效
    public String dataQuality;            // 数据质量（优/良/中/差）
    public String lastError;             // 最后错误信息
    
    /**
     * 默认构造函数
     */
    public ProcessedData() {
        this.processingTime = System.currentTimeMillis();
        this.isDataValid = true;
        this.dataQuality = "未知";
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 要复制的数据
     */
    public ProcessedData(ProcessedData other) {
        this.processingTime = other.processingTime;
        this.processedBoatSpeed = other.processedBoatSpeed;
        this.processedStrokeRate = other.processedStrokeRate;
        this.processedBoatYaw = other.processedBoatYaw;
        this.averageSpeed = other.averageSpeed;
        this.maxSpeed = other.maxSpeed;
        this.averageStrokeRate = other.averageStrokeRate;
        this.maxStrokeRate = other.maxStrokeRate;
        this.totalDistance = other.totalDistance;
        this.totalTime = other.totalTime;
        this.totalStrokeCount = other.totalStrokeCount;
        this.efficiency = other.efficiency;
        this.consistency = other.consistency;
        this.isDataValid = other.isDataValid;
        this.dataQuality = other.dataQuality;
        this.lastError = other.lastError;
    }
    
    /**
     * 从SensorData复制数据
     * 
     * @param sensorData 传感器数据
     */
    public void copyFrom(SensorData sensorData) {
        this.processingTime = sensorData.processingTime;
        this.processedBoatSpeed = sensorData.boatSpeed;
        this.processedStrokeRate = sensorData.strokeRateSmoothed;
        this.processedBoatYaw = sensorData.boatYaw;
        
        // 更新统计信息
        updateStatistics();
        
        // 评估数据质量
        evaluateDataQuality();
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        // 这里可以实现更复杂的统计逻辑
        // 例如：移动平均、峰值检测等
        
        if (processedBoatSpeed > maxSpeed) {
            maxSpeed = processedBoatSpeed;
        }
        
        if (processedStrokeRate > maxStrokeRate) {
            maxStrokeRate = processedStrokeRate;
        }
    }
    
    /**
     * 评估数据质量
     */
    private void evaluateDataQuality() {
        // 基于多个因素评估数据质量
        int qualityScore = 0;
        
        // 速度合理性检查
        if (processedBoatSpeed >= 0 && processedBoatSpeed <= 30) { // 0-30 km/h
            qualityScore += 25;
        }
        
        // 桨频合理性检查
        if (processedStrokeRate >= 10 && processedStrokeRate <= 60) { // 10-60 spm
            qualityScore += 25;
        }
        
        // 数据一致性检查
        if (Math.abs(processedBoatSpeed - averageSpeed) < 5) { // 与平均值差异不大
            qualityScore += 25;
        }
        
        // 传感器状态检查
        if (isDataValid) {
            qualityScore += 25;
        }
        
        // 设置质量等级
        if (qualityScore >= 90) {
            dataQuality = "优";
        } else if (qualityScore >= 70) {
            dataQuality = "良";
        } else if (qualityScore >= 50) {
            dataQuality = "中";
        } else {
            dataQuality = "差";
        }
    }
    
    /**
     * 重置所有数据
     */
    public void reset() {
        this.processingTime = System.currentTimeMillis();
        this.processedBoatSpeed = 0;
        this.processedStrokeRate = 0;
        this.processedBoatYaw = 0;
        this.averageSpeed = 0;
        this.maxSpeed = 0;
        this.averageStrokeRate = 0;
        this.maxStrokeRate = 0;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.totalStrokeCount = 0;
        this.efficiency = 0;
        this.consistency = 0;
        this.isDataValid = true;
        this.dataQuality = "未知";
        this.lastError = null;
    }
    
    /**
     * 设置错误信息
     * 
     * @param error 错误信息
     */
    public void setError(String error) {
        this.lastError = error;
        this.isDataValid = false;
        this.dataQuality = "差";
    }
    
    /**
     * 获取速度描述
     * 
     * @return 速度描述字符串
     */
    public String getSpeedDescription() {
        if (processedBoatSpeed < 0) {
            return "无效";
        } else if (processedBoatSpeed < 5) {
            return "慢速";
        } else if (processedBoatSpeed < 10) {
            return "中速";
        } else if (processedBoatSpeed < 15) {
            return "快速";
        } else {
            return "高速";
        }
    }
    
    /**
     * 获取桨频描述
     * 
     * @return 桨频描述字符串
     */
    public String getStrokeRateDescription() {
        if (processedStrokeRate < 10) {
            return "过低";
        } else if (processedStrokeRate < 20) {
            return "休闲";
        } else if (processedStrokeRate < 30) {
            return "适中";
        } else if (processedStrokeRate < 40) {
            return "积极";
        } else if (processedStrokeRate < 50) {
            return "激烈";
        } else {
            return "极高";
        }
    }
    
    /**
     * 获取效率描述
     * 
     * @return 效率描述字符串
     */
    public String getEfficiencyDescription() {
        if (efficiency < 20) {
            return "很低";
        } else if (efficiency < 40) {
            return "低";
        } else if (efficiency < 60) {
            return "中等";
        } else if (efficiency < 80) {
            return "高";
        } else {
            return "极高";
        }
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        return String.format("速度: %.1f km/h (最大: %.1f), 桨频: %.1f spm (最大: %.1f), " +
                           "总距离: %.0f m, 总时间: %d s, 总划桨: %d次, 效率: %.0f%%",
                processedBoatSpeed, maxSpeed, processedStrokeRate, maxStrokeRate,
                totalDistance, totalTime / 1000, totalStrokeCount, efficiency);
    }
    
    @Override
    public String toString() {
        return String.format("ProcessedData{speed=%.1f km/h, strokeRate=%.1f spm, " +
                           "quality=%s, valid=%s}",
                processedBoatSpeed, processedStrokeRate, dataQuality, isDataValid);
    }
}