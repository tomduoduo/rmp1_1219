package com.motionrivalry.rowmasterpro.training;

import android.location.Location;

/**
 * 训练会话数据
 * 存储单个训练会话的所有相关数据
 */
public class TrainingSessionData {
    
    // 会话基本信息
    public String sessionId;
    public String sessionName;
    public long startTime;
    public long endTime;
    public long totalDuration;      // 总持续时间（毫秒）
    public long totalPauseTime;     // 总暂停时间（毫秒）
    public long actualTrainingTime; // 实际训练时间（毫秒）
    
    // 位置信息
    public Location startLocation;
    public Location endLocation;
    public Location lastLocation;
    
    // 距离和速度
    public double totalDistance;    // 总距离（米）
    public double maxSpeed;         // 最大速度（km/h）
    public double averageSpeed;     // 平均速度（km/h）
    public double speedSum;         // 速度总和（用于计算平均值）
    public int speedCount;          // 速度采样次数
    public double lastSpeed;        // 最后记录的速度
    
    // 桨频统计
    public int strokeCount;         // 总划桨次数
    public double maxStrokeRate;    // 最大桨频（次/分钟）
    public double minStrokeRate;    // 最小桨频（次/分钟）
    public double averageStrokeRate; // 平均桨频（次/分钟）
    public double strokeRateSum;    // 桨频总和（用于计算平均值）
    public int strokeRateCount;     // 桨频采样次数
    
    // 心率统计
    public int maxHeartRate;        // 最大心率（bpm）
    public int minHeartRate;        // 最小心率（bpm）
    public int averageHeartRate;    // 平均心率（bpm）
    public int heartRateSum;        // 心率总和（用于计算平均值）
    public int heartRateCount;      // 心率采样次数
    
    // 状态信息
    public int pauseCount;          // 暂停次数
    public int resumeCount;         // 恢复次数
    public long lastPauseTime;      // 最后暂停时间
    public boolean isCompleted;     // 是否完成
    public boolean isCancelled;     // 是否取消
    public String completionReason; // 完成原因
    
    // 文件和数据
    public String dataFilePath;     // 数据文件路径
    public String screenshotPath;   // 截图文件路径
    public String notes;            // 训练笔记
    public int dataQuality;         // 数据质量（1-5，5为最好）
    
    // 性能指标
    public double efficiency;       // 效率指标（0-100）
    public double consistency;      // 一致性指标（0-100）
    public double performanceScore; // 综合表现评分（0-100）
    
    // 目标和计划
    public int targetDuration;      // 目标持续时间（分钟）
    public double targetDistance;   // 目标距离（米）
    public int targetStrokeRate;     // 目标桨频（次/分钟）
    public boolean targetReached;    // 是否达成目标
    
    /**
     * 默认构造函数
     */
    public TrainingSessionData() {
        reset();
    }
    
    /**
     * 重置所有数据
     */
    public void reset() {
        sessionId = null;
        sessionName = null;
        startTime = 0;
        endTime = 0;
        totalDuration = 0;
        totalPauseTime = 0;
        actualTrainingTime = 0;
        
        startLocation = null;
        endLocation = null;
        lastLocation = null;
        
        totalDistance = 0;
        maxSpeed = 0;
        averageSpeed = 0;
        speedSum = 0;
        speedCount = 0;
        lastSpeed = 0;
        
        strokeCount = 0;
        maxStrokeRate = 0;
        minStrokeRate = 0;
        averageStrokeRate = 0;
        strokeRateSum = 0;
        strokeRateCount = 0;
        
        maxHeartRate = 0;
        minHeartRate = 0;
        averageHeartRate = 0;
        heartRateSum = 0;
        heartRateCount = 0;
        
        pauseCount = 0;
        resumeCount = 0;
        lastPauseTime = 0;
        isCompleted = false;
        isCancelled = false;
        completionReason = null;
        
        dataFilePath = null;
        screenshotPath = null;
        notes = null;
        dataQuality = 0;
        
        efficiency = 0;
        consistency = 0;
        performanceScore = 0;
        
        targetDuration = 0;
        targetDistance = 0;
        targetStrokeRate = 0;
        targetReached = false;
    }
    
    /**
     * 获取会话持续时间（秒）
     * 
     * @return 持续时间（秒）
     */
    public long getDurationSeconds() {
        return totalDuration / 1000;
    }
    
    /**
     * 获取实际训练时间（秒）
     * 
     * @return 训练时间（秒）
     */
    public long getTrainingTimeSeconds() {
        return actualTrainingTime / 1000;
    }
    
    /**
     * 获取总距离（公里）
     * 
     * @return 距离（公里）
     */
    public double getDistanceKm() {
        return totalDistance / 1000.0;
    }
    
    /**
     * 获取平均速度（m/s）
     * 
     * @return 平均速度（m/s）
     */
    public double getAverageSpeedMs() {
        if (actualTrainingTime > 0) {
            return totalDistance / (actualTrainingTime / 1000.0);
        }
        return 0;
    }
    
    /**
     * 获取平均划桨距离（米/次）
     * 
     * @return 平均划桨距离（米/次）
     */
    public double getAverageStrokeDistance() {
        if (strokeCount > 0) {
            return totalDistance / strokeCount;
        }
        return 0;
    }
    
    /**
     * 获取效率指标
     * 
     * @return 效率指标
     */
    public double getEfficiency() {
        if (strokeCount > 0 && totalDistance > 0) {
            // 基于距离和划桨次数的效率计算
            return (totalDistance / strokeCount) * (averageSpeed / maxSpeed);
        }
        return 0;
    }
    
    /**
     * 获取一致性指标
     * 
     * @return 一致性指标
     */
    public double getConsistency() {
        if (strokeRateCount > 0 && maxStrokeRate > minStrokeRate) {
            // 基于桨频变化的一致性计算
            double variation = (maxStrokeRate - minStrokeRate) / averageStrokeRate;
            return Math.max(0, 100 - (variation * 100));
        }
        return 0;
    }
    
    /**
     * 获取综合表现评分
     * 
     * @return 表现评分（0-100）
     */
    public double getPerformanceScore() {
        // 基于多个因素的综合评分
        double score = 0;
        
        // 距离因素（30%）
        if (targetDistance > 0) {
            double distanceRatio = Math.min(1.0, totalDistance / targetDistance);
            score += distanceRatio * 30;
        } else {
            score += 15; // 默认分数
        }
        
        // 时间因素（20%）
        if (targetDuration > 0) {
            double durationMinutes = getDurationSeconds() / 60.0;
            double durationRatio = Math.min(1.0, durationMinutes / targetDuration);
            score += durationRatio * 20;
        } else {
            score += 10; // 默认分数
        }
        
        // 桨频因素（20%）
        if (targetStrokeRate > 0) {
            double strokeRateRatio = 1.0 - Math.abs(averageStrokeRate - targetStrokeRate) / targetStrokeRate;
            score += Math.max(0, strokeRateRatio) * 20;
        } else {
            score += 10; // 默认分数
        }
        
        // 效率因素（20%）
        score += (efficiency / 100.0) * 20;
        
        // 一致性因素（10%）
        score += (consistency / 100.0) * 10;
        
        return Math.min(100, Math.max(0, score));
    }
    
    /**
     * 检查是否达成目标
     * 
     * @return true表示达成目标
     */
    public boolean isTargetReached() {
        boolean distanceReached = targetDistance <= 0 || totalDistance >= targetDistance;
        boolean durationReached = targetDuration <= 0 || (getDurationSeconds() / 60.0) >= targetDuration;
        boolean strokeRateReached = targetStrokeRate <= 0 || averageStrokeRate >= targetStrokeRate;
        
        return distanceReached && durationReached && strokeRateReached;
    }
    
    /**
     * 获取会话状态描述
     * 
     * @return 状态描述
     */
    public String getStatusDescription() {
        if (isCancelled) {
            return "已取消";
        } else if (isCompleted) {
            if (targetReached) {
                return "已完成（达成目标）";
            } else {
                return "已完成";
            }
        } else if (startTime > 0) {
            return "进行中";
        } else {
            return "未开始";
        }
    }
    
    /**
     * 获取简短摘要
     * 
     * @return 摘要字符串
     */
    public String getSummary() {
        return String.format("%s - %.1fkm in %d分钟 - %d次划桨 - 评分:%.0f",
                sessionName != null ? sessionName : "训练",
                getDistanceKm(),
                getDurationSeconds() / 60,
                strokeCount,
                performanceScore);
    }
    
    /**
     * 获取详细统计
     * 
     * @return 详细统计字符串
     */
    public String getDetailedStatistics() {
        return String.format("训练统计:\n" +
                           "持续时间: %d分钟\n" +
                           "总距离: %.2f公里\n" +
                           "平均速度: %.1f km/h\n" +
                           "最大速度: %.1f km/h\n" +
                           "划桨次数: %d次\n" +
                           "平均桨频: %.1f次/分钟\n" +
                           "平均心率: %d bpm\n" +
                           "暂停次数: %d次\n" +
                           "效率: %.1f%%\n" +
                           "一致性: %.1f%%\n" +
                           "综合评分: %.0f/100",
                getDurationSeconds() / 60,
                getDistanceKm(),
                averageSpeed,
                maxSpeed,
                strokeCount,
                averageStrokeRate,
                averageHeartRate,
                pauseCount,
                efficiency,
                consistency,
                performanceScore);
    }
    
    @Override
    public String toString() {
        return String.format("TrainingSessionData{id=%s, name=%s, duration=%d分钟, distance=%.1fkm, strokes=%d}",
                sessionId, sessionName, getDurationSeconds() / 60, getDistanceKm(), strokeCount);
    }
}