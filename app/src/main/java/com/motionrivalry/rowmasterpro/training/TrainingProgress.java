package com.motionrivalry.rowmasterpro.training;

/**
 * 训练进度信息
 * 用于实时更新训练进度
 */
public class TrainingProgress {
    
    // 会话信息
    public String sessionId;
    public long elapsedTime;        // 已用时间（毫秒）
    public long remainingTime;       // 剩余时间（毫秒）
    
    // 距离信息
    public double totalDistance;     // 总距离（米）
    public double remainingDistance; // 剩余距离（米）
    public double distanceProgress;  // 距离进度（0-1）
    
    // 速度信息
    public double currentSpeed;      // 当前速度（km/h）
    public double averageSpeed;      // 平均速度（km/h）
    public double maxSpeed;         // 最大速度（km/h）
    
    // 桨频信息
    public int strokeCount;         // 划桨次数
    public double currentStrokeRate; // 当前桨频（次/分钟）
    public double averageStrokeRate; // 平均桨频（次/分钟）
    public double maxStrokeRate;    // 最大桨频（次/分钟）
    
    // 心率信息
    public int currentHeartRate;     // 当前心率（bpm）
    public double averageHeartRate;     // 平均心率（bpm）
    public int maxHeartRate;        // 最大心率（bpm）
    
    // 效率信息
    public double efficiency;        // 效率指标（0-100）
    public double consistency;       // 一致性指标（0-100）
    public double performanceScore;  // 表现评分（0-100）
    
    // 目标信息
    public boolean hasTargetTime;    // 是否有时间目标
    public boolean hasTargetDistance; // 是否有距离目标
    public boolean hasTargetStrokeRate; // 是否有桨频目标
    public boolean targetReached;    // 是否达成目标
    
    // 状态信息
    public String status;            // 状态描述
    public boolean isPaused;         // 是否暂停
    public int pauseCount;          // 暂停次数
    
    /**
     * 默认构造函数
     */
    public TrainingProgress() {
        this.elapsedTime = 0;
        this.remainingTime = 0;
        this.totalDistance = 0;
        this.remainingDistance = 0;
        this.distanceProgress = 0;
        this.currentSpeed = 0;
        this.averageSpeed = 0;
        this.maxSpeed = 0;
        this.strokeCount = 0;
        this.currentStrokeRate = 0;
        this.averageStrokeRate = 0;
        this.maxStrokeRate = 0;
        this.currentHeartRate = 0;
        this.averageHeartRate = 0;
        this.maxHeartRate = 0;
        this.efficiency = 0;
        this.consistency = 0;
        this.performanceScore = 0;
        this.hasTargetTime = false;
        this.hasTargetDistance = false;
        this.hasTargetStrokeRate = false;
        this.targetReached = false;
        this.status = "准备开始";
        this.isPaused = false;
        this.pauseCount = 0;
    }
    
    /**
     * 获取时间进度（0-1）
     * 
     * @param targetTime 目标时间（毫秒）
     * @return 时间进度
     */
    public double getTimeProgress(long targetTime) {
        if (targetTime <= 0) {
            return 0;
        }
        return Math.min(1.0, (double) elapsedTime / targetTime);
    }
    
    /**
     * 获取距离进度（0-1）
     * 
     * @param targetDistance 目标距离（米）
     * @return 距离进度
     */
    public double getDistanceProgress(double targetDistance) {
        if (targetDistance <= 0) {
            return 0;
        }
        return Math.min(1.0, totalDistance / targetDistance);
    }
    
    /**
     * 获取预计完成时间（毫秒）
     * 
     * @param targetDistance 目标距离（米）
     * @return 预计完成时间
     */
    public long getEstimatedCompletionTime(double targetDistance) {
        if (targetDistance <= 0 || averageSpeed <= 0) {
            return 0;
        }
        
        double remainingDistanceKm = (targetDistance - totalDistance) / 1000.0;
        double remainingHours = remainingDistanceKm / averageSpeed;
        return (long) (remainingHours * 3600 * 1000); // 转换为毫秒
    }
    
    /**
     * 获取速度趋势
     * 
     * @return 速度趋势（"上升", "下降", "稳定"）
     */
    public String getSpeedTrend() {
        if (currentSpeed > averageSpeed * 1.05) {
            return "上升";
        } else if (currentSpeed < averageSpeed * 0.95) {
            return "下降";
        } else {
            return "稳定";
        }
    }
    
    /**
     * 获取桨频趋势
     * 
     * @return 桨频趋势（"上升", "下降", "稳定"）
     */
    public String getStrokeRateTrend() {
        if (currentStrokeRate > averageStrokeRate * 1.1) {
            return "上升";
        } else if (currentStrokeRate < averageStrokeRate * 0.9) {
            return "下降";
        } else {
            return "稳定";
        }
    }
    
    /**
     * 获取心率区间描述
     * 
     * @return 心率区间描述
     */
    public String getHeartRateZoneDescription() {
        if (currentHeartRate < 100) {
            return "恢复区间";
        } else if (currentHeartRate < 120) {
            return "有氧基础";
        } else if (currentHeartRate < 140) {
            return "有氧强化";
        } else if (currentHeartRate < 160) {
            return "乳酸阈值";
        } else {
            return "无氧爆发";
        }
    }
    
    /**
     * 获取效率等级
     * 
     * @return 效率等级（"很低", "低", "中等", "高", "极高"）
     */
    public String getEfficiencyLevel() {
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
     * 获取表现等级
     * 
     * @return 表现等级（"新手", "初级", "中级", "高级", "专业"）
     */
    public String getPerformanceLevel() {
        if (performanceScore < 20) {
            return "新手";
        } else if (performanceScore < 40) {
            return "初级";
        } else if (performanceScore < 60) {
            return "中级";
        } else if (performanceScore < 80) {
            return "高级";
        } else {
            return "专业";
        }
    }
    
    /**
     * 获取进度百分比
     * 
     * @param targetTime 目标时间（毫秒）
     * @param targetDistance 目标距离（米）
     * @return 综合进度百分比
     */
    public double getOverallProgress(long targetTime, double targetDistance) {
        double timeProgress = getTimeProgress(targetTime);
        double distanceProgress = getDistanceProgress(targetDistance);
        
        if (targetTime > 0 && targetDistance > 0) {
            return (timeProgress + distanceProgress) / 2.0;
        } else if (targetTime > 0) {
            return timeProgress;
        } else if (targetDistance > 0) {
            return distanceProgress;
        } else {
            return 0;
        }
    }
    
    /**
     * 获取简要状态
     * 
     * @return 简要状态字符串
     */
    public String getBriefStatus() {
        if (isPaused) {
            return String.format("暂停中 - %d次划桨", strokeCount);
        } else {
            return String.format("%.1fkm - %d次划桨 - %.1f spm",
                    totalDistance / 1000.0, strokeCount, currentStrokeRate);
        }
    }
    
    /**
     * 获取详细状态
     * 
     * @return 详细状态字符串
     */
    public String getDetailedStatus() {
        long elapsedMinutes = elapsedTime / (60 * 1000);
        long elapsedSeconds = (elapsedTime / 1000) % 60;
        
        return String.format("训练进度 - 时间: %02d:%02d, 距离: %.2fkm, " +
                           "速度: %.1f km/h, 桨频: %.1f spm, 心率: %d bpm, " +
                           "效率: %.0f%%, 评分: %.0f/100",
                elapsedMinutes, elapsedSeconds,
                totalDistance / 1000.0,
                currentSpeed,
                currentStrokeRate,
                currentHeartRate,
                efficiency,
                performanceScore);
    }
    
    @Override
    public String toString() {
        return String.format("TrainingProgress{distance=%.1fkm, speed=%.1fkm/h, " +
                           "strokeRate=%.1f spm, heartRate=%d bpm, score=%.0f}",
                totalDistance / 1000.0, currentSpeed, currentStrokeRate,
                currentHeartRate, performanceScore);
    }
}