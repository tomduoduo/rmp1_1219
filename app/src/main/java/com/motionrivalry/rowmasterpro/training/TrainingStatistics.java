package com.motionrivalry.rowmasterpro.training;

/**
 * 训练统计信息
 * 存储训练会话的统计计算结果
 */
public class TrainingStatistics {
    
    // 时间统计
    public long totalDuration;          // 总持续时间（毫秒）
    public long actualTrainingTime;     // 实际训练时间（毫秒）
    public long pauseTime;             // 暂停时间（毫秒）
    public int pauseCount;             // 暂停次数
    
    // 距离统计
    public double totalDistance;        // 总距离（米）
    public double maxSpeed;            // 最大速度（km/h）
    public double minSpeed;            // 最小速度（km/h）
    public double averageSpeed;         // 平均速度（km/h）
    
    // 桨频统计
    public int totalStrokes;            // 总划桨次数
    public double maxStrokeRate;        // 最大桨频（次/分钟）
    public double minStrokeRate;        // 最小桨频（次/分钟）
    public double averageStrokeRate;    // 平均桨频（次/分钟）
    public double averageStrokeDistance; // 平均划桨距离（米/次）
    
    // 心率统计
    public int maxHeartRate;            // 最大心率（bpm）
    public int minHeartRate;            // 最小心率（bpm）
    public int averageHeartRate;        // 平均心率（bpm）
    public int heartRateZone1Time;     // 心率区间1时间（秒）
    public int heartRateZone2Time;     // 心率区间2时间（秒）
    public int heartRateZone3Time;     // 心率区间3时间（秒）
    public int heartRateZone4Time;     // 心率区间4时间（秒）
    public int heartRateZone5Time;     // 心率区间5时间（秒）
    
    // 效率指标
    public double efficiency;           // 效率指标（0-100）
    public double consistency;          // 一致性指标（0-100）
    public double performanceScore;   // 综合表现评分（0-100）
    
    // 训练强度
    public double trainingLoad;         // 训练负荷
    public double caloriesBurned;       // 消耗卡路里
    public double averagePower;         // 平均功率（瓦特）
    
    // 数据质量
    public int dataQuality;             // 数据质量（1-5，5为最好）
    public int gpsSignalQuality;        // GPS信号质量（0-100）
    public int sensorDataQuality;       // 传感器数据质量（0-100）
    
    /**
     * 默认构造函数
     */
    public TrainingStatistics() {
        reset();
    }
    
    /**
     * 重置所有统计数据
     */
    public void reset() {
        totalDuration = 0;
        actualTrainingTime = 0;
        pauseTime = 0;
        pauseCount = 0;
        
        totalDistance = 0;
        maxSpeed = 0;
        minSpeed = 0;
        averageSpeed = 0;
        
        totalStrokes = 0;
        maxStrokeRate = 0;
        minStrokeRate = 0;
        averageStrokeRate = 0;
        averageStrokeDistance = 0;
        
        maxHeartRate = 0;
        minHeartRate = 0;
        averageHeartRate = 0;
        heartRateZone1Time = 0;
        heartRateZone2Time = 0;
        heartRateZone3Time = 0;
        heartRateZone4Time = 0;
        heartRateZone5Time = 0;
        
        efficiency = 0;
        consistency = 0;
        performanceScore = 0;
        
        trainingLoad = 0;
        caloriesBurned = 0;
        averagePower = 0;
        
        dataQuality = 0;
        gpsSignalQuality = 0;
        sensorDataQuality = 0;
    }
    
    /**
     * 计算平均划桨距离
     * 
     * @return 平均划桨距离（米/次）
     */
    public double calculateAverageStrokeDistance() {
        if (totalStrokes > 0) {
            return totalDistance / totalStrokes;
        }
        return 0;
    }
    
    /**
     * 计算训练负荷（基于心率和时间）
     * 
     * @param averageHeartRate 平均心率
     * @param duration 持续时间（分钟）
     * @param maxHeartRate 最大心率
     * @return 训练负荷
     */
    public double calculateTrainingLoad(int averageHeartRate, long duration, int maxHeartRate) {
        if (maxHeartRate <= 0) {
            return 0;
        }
        
        double heartRateRatio = (double) averageHeartRate / maxHeartRate;
        double durationHours = duration / 60.0;
        
        // 简化的训练负荷计算
        return heartRateRatio * heartRateRatio * durationHours * 100;
    }
    
    /**
     * 计算消耗卡路里（基于心率、时间和体重）
     * 
     * @param averageHeartRate 平均心率
     * @param duration 持续时间（分钟）
     * @param weight 体重（kg）
     * @param age 年龄
     * @param isMale 是否为男性
     * @return 消耗卡路里
     */
    public double calculateCaloriesBurned(int averageHeartRate, long duration, 
                                        double weight, int age, boolean isMale) {
        if (weight <= 0 || age <= 0) {
            return 0;
        }
        
        // 使用心率计算卡路里消耗的公式
        double timeHours = duration / 60.0;
        
        if (isMale) {
            return ((-55.0969 + (0.6309 * averageHeartRate) + (0.1988 * weight) + (0.2017 * age)) / 4.184) * timeHours * 60;
        } else {
            return ((-20.4022 + (0.4472 * averageHeartRate) - (0.1263 * weight) + (0.074 * age)) / 4.184) * timeHours * 60;
        }
    }
    
    /**
     * 计算平均功率（基于速度、桨频和体重）
     * 
     * @param averageSpeed 平均速度（km/h）
     * @param averageStrokeRate 平均桨频（次/分钟）
     * @param weight 体重（kg）
     * @return 平均功率（瓦特）
     */
    public double calculateAveragePower(double averageSpeed, double averageStrokeRate, double weight) {
        if (averageSpeed <= 0 || weight <= 0) {
            return 0;
        }
        
        // 简化的功率计算（基于速度和体重）
        double speedMs = averageSpeed / 3.6; // 转换为m/s
        
        // 考虑水的阻力和划船效率
        double dragCoefficient = 0.5; // 简化的阻力系数
        double efficiency = 0.25;     // 划船效率
        
        double power = 0.5 * dragCoefficient * speedMs * speedMs * speedMs * weight / efficiency;
        
        return Math.max(0, power);
    }
    
    /**
     * 计算综合表现评分
     * 
     * @return 表现评分（0-100）
     */
    public double calculatePerformanceScore() {
        double score = 0;
        
        // 距离因素（25%）
        if (totalDistance > 0) {
            double distanceScore = Math.min(100, totalDistance / 1000.0 * 10); // 每公里10分，最多100分
            score += distanceScore * 0.25;
        }
        
        // 时间因素（25%）
        if (actualTrainingTime > 0) {
            double timeHours = actualTrainingTime / (60.0 * 60 * 1000);
            double timeScore = Math.min(100, timeHours * 20); // 每小时20分，最多100分
            score += timeScore * 0.25;
        }
        
        // 强度因素（20%）
        if (averageHeartRate > 0) {
            // 假设最大心率为190，基于平均心率计算强度分数
            double intensityScore = (averageHeartRate / 190.0) * 100;
            score += intensityScore * 0.20;
        }
        
        // 技术因素（15%）
        if (averageStrokeRate > 0 && averageStrokeDistance > 0) {
            // 基于桨频和划桨距离的技术评分
            double techniqueScore = (averageStrokeRate / 30.0) * (averageStrokeDistance / 10.0) * 50;
            techniqueScore = Math.min(100, techniqueScore);
            score += techniqueScore * 0.15;
        }
        
        // 一致性因素（15%）
        if (consistency > 0) {
            score += consistency * 0.15;
        }
        
        return Math.min(100, Math.max(0, score));
    }
    
    /**
     * 获取训练强度描述
     * 
     * @return 强度描述
     */
    public String getIntensityDescription() {
        if (averageHeartRate <= 0) {
            return "未知";
        }
        
        if (averageHeartRate < 100) {
            return "恢复训练";
        } else if (averageHeartRate < 120) {
            return "基础有氧";
        } else if (averageHeartRate < 140) {
            return "有氧强化";
        } else if (averageHeartRate < 160) {
            return "乳酸阈值";
        } else {
            return "无氧爆发";
        }
    }
    
    /**
     * 获取数据质量描述
     * 
     * @return 质量描述
     */
    public String getDataQualityDescription() {
        if (dataQuality >= 5) {
            return "优秀";
        } else if (dataQuality >= 4) {
            return "良好";
        } else if (dataQuality >= 3) {
            return "一般";
        } else if (dataQuality >= 2) {
            return "较差";
        } else {
            return "很差";
        }
    }
    
    /**
     * 获取速度变化趋势
     * 
     * @return 趋势描述
     */
    public String getSpeedTrend() {
        if (maxSpeed <= 0 || minSpeed <= 0) {
            return "未知";
        }
        
        double speedVariation = (maxSpeed - minSpeed) / averageSpeed;
        
        if (speedVariation < 0.1) {
            return "非常稳定";
        } else if (speedVariation < 0.2) {
            return "稳定";
        } else if (speedVariation < 0.4) {
            return "一般";
        } else if (speedVariation < 0.6) {
            return "变化较大";
        } else {
            return "变化很大";
        }
    }
    
    /**
     * 获取桨频稳定性
     * 
     * @return 稳定性描述
     */
    public String getStrokeRateStability() {
        if (maxStrokeRate <= 0 || minStrokeRate <= 0) {
            return "未知";
        }
        
        double rateVariation = (maxStrokeRate - minStrokeRate) / averageStrokeRate;
        
        if (rateVariation < 0.1) {
            return "非常稳定";
        } else if (rateVariation < 0.2) {
            return "稳定";
        } else if (rateVariation < 0.4) {
            return "一般";
        } else if (rateVariation < 0.6) {
            return "变化较大";
        } else {
            return "变化很大";
        }
    }
    
    /**
     * 获取心率区间分布
     * 
     * @return 区间分布字符串
     */
    public String getHeartRateZoneDistribution() {
        int totalTime = heartRateZone1Time + heartRateZone2Time + heartRateZone3Time + 
                       heartRateZone4Time + heartRateZone5Time;
        
        if (totalTime <= 0) {
            return "无数据";
        }
        
        double zone1Percent = (heartRateZone1Time * 100.0) / totalTime;
        double zone2Percent = (heartRateZone2Time * 100.0) / totalTime;
        double zone3Percent = (heartRateZone3Time * 100.0) / totalTime;
        double zone4Percent = (heartRateZone4Time * 100.0) / totalTime;
        double zone5Percent = (heartRateZone5Time * 100.0) / totalTime;
        
        return String.format("恢复:%.1f%% 有氧基础:%.1f%% 有氧强化:%.1f%% 乳酸阈值:%.1f%% 无氧爆发:%.1f%%",
                zone1Percent, zone2Percent, zone3Percent, zone4Percent, zone5Percent);
    }
    
    /**
     * 获取简要统计摘要
     * 
     * @return 简要摘要
     */
    public String getBriefSummary() {
        return String.format("%.1fkm in %d分钟 - %d次划桨 - 评分:%.0f",
                totalDistance / 1000.0, (int)(actualTrainingTime / (60 * 1000)), 
                totalStrokes, performanceScore);
    }
    
    /**
     * 获取详细统计信息
     * 
     * @return 详细统计字符串
     */
    public String getDetailedStatistics() {
        return String.format("训练统计详情:\n" +
                           "总时长: %d分钟\n" +
                           "训练时间: %d分钟\n" +
                           "暂停时间: %d分钟\n" +
                           "暂停次数: %d次\n" +
                           "总距离: %.2f公里\n" +
                           "平均速度: %.1f km/h\n" +
                           "最大速度: %.1f km/h\n" +
                           "最小速度: %.1f km/h\n" +
                           "总划桨: %d次\n" +
                           "平均桨频: %.1f次/分钟\n" +
                           "最大桨频: %.1f次/分钟\n" +
                           "最小桨频: %.1f次/分钟\n" +
                           "平均划桨距离: %.1f米\n" +
                           "平均心率: %d bpm\n" +
                           "最大心率: %d bpm\n" +
                           "最小心率: %d bpm\n" +
                           "效率: %.1f%%\n" +
                           "一致性: %.1f%%\n" +
                           "表现评分: %.0f/100\n" +
                           "训练负荷: %.1f\n" +
                           "消耗卡路里: %.0f\n" +
                           "平均功率: %.0f瓦特\n" +
                           "数据质量: %s (%d/5)\n" +
                           "GPS信号: %d%%\n" +
                           "传感器数据: %d%%",
                (int)(totalDuration / (60 * 1000)),
                (int)(actualTrainingTime / (60 * 1000)),
                (int)(pauseTime / (60 * 1000)),
                pauseCount,
                totalDistance / 1000.0,
                averageSpeed,
                maxSpeed,
                minSpeed,
                totalStrokes,
                averageStrokeRate,
                maxStrokeRate,
                minStrokeRate,
                averageStrokeDistance,
                averageHeartRate,
                maxHeartRate,
                minHeartRate,
                efficiency,
                consistency,
                performanceScore,
                trainingLoad,
                caloriesBurned,
                averagePower,
                getDataQualityDescription(),
                dataQuality,
                gpsSignalQuality,
                sensorDataQuality);
    }
    
    @Override
    public String toString() {
        return String.format("TrainingStatistics{distance=%.1fkm, duration=%dmin, " +
                           "strokes=%d, avgSpeed=%.1fkm/h, avgStrokeRate=%.1f spm, " +
                           "avgHR=%d bpm, score=%.0f}",
                totalDistance / 1000.0, (int)(totalDuration / (60 * 1000)),
                totalStrokes, averageSpeed, averageStrokeRate, averageHeartRate, performanceScore);
    }
}