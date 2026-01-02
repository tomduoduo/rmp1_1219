package com.motionrivalry.rowmasterpro.sensor;

/**
 * 桨频检测器
 * 通过分析加速度数据检测桨频和划桨次数
 */
public class StrokeDetector {
    
    private static final String TAG = "StrokeDetector";
    
    // 检测参数
    private double threshold;          // 检测阈值
    private final int minStrokeInterval;     // 最小划桨间隔（毫秒）
    private final int maxStrokeInterval;     // 最大划桨间隔（毫秒）
    
    // 状态变量
    private boolean isAboveThreshold;
    private long lastStrokeTime;
    private long lastPeakTime;
    private int strokeCount;
    private double currentStrokeRate;        // 桨频（次/分钟）
    
    // 峰值检测
    private double lastValue;
    private boolean isRising;
    private double maxValue;
    private long maxValueTime;
    
    // 历史数据
    private long[] strokeIntervals;          // 划桨间隔历史
    private int intervalIndex;
    private int intervalCount;
    
    // 状态
    private boolean isEnabled;
    private long detectionStartTime;
    
    /**
     * 桨频检测结果
     */
    public static class StrokeResult {
        public boolean isStrokeDetected;
        public double strokeRate;          // 桨频（次/分钟）
        public int strokeCount;
        public long strokeTime;
        public double strokePower;         // 划桨力度
        
        public StrokeResult() {
            this.isStrokeDetected = false;
            this.strokeRate = 0;
            this.strokeCount = 0;
            this.strokeTime = 0;
            this.strokePower = 0;
        }
    }
    
    /**
     * 构造函数
     * 
     * @param threshold 检测阈值
     */
    public StrokeDetector(double threshold) {
        this.threshold = threshold;
        this.minStrokeInterval = 300;    // 最小300ms（200次/分钟）
        this.maxStrokeInterval = 5000;   // 最大5秒（12次/分钟）
        
        this.isAboveThreshold = false;
        this.lastStrokeTime = 0;
        this.lastPeakTime = 0;
        this.strokeCount = 0;
        this.currentStrokeRate = 0;
        
        this.lastValue = 0;
        this.isRising = false;
        this.maxValue = 0;
        this.maxValueTime = 0;
        
        this.strokeIntervals = new long[8]; // 保存最近8次间隔
        this.intervalIndex = 0;
        this.intervalCount = 0;
        
        this.isEnabled = true;
        this.detectionStartTime = System.currentTimeMillis();
    }
    
    /**
     * 检测桨频
     * 
     * @param acceleration 加速度值（通常使用Y轴）
     * @return 检测结果
     */
    public StrokeResult detectStroke(double acceleration) {
        StrokeResult result = new StrokeResult();
        
        if (!isEnabled) {
            return result;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // 峰值检测
        detectPeak(acceleration, currentTime);
        
        // 阈值检测
        if (acceleration > threshold) {
            if (!isAboveThreshold) {
                // 从阈值下方到上方 - 可能的划桨开始
                isAboveThreshold = true;
                
                // 检查是否满足划桨条件
                if (isValidStroke(currentTime)) {
                    // 检测到划桨
                    strokeCount++;
                    currentStrokeRate = calculateStrokeRate(currentTime);
                    
                    result.isStrokeDetected = true;
                    result.strokeRate = currentStrokeRate;
                    result.strokeCount = strokeCount;
                    result.strokeTime = currentTime;
                    result.strokePower = maxValue; // 使用峰值作为力度指标
                    
                    // 记录划桨间隔
                    if (lastStrokeTime > 0) {
                        long interval = currentTime - lastStrokeTime;
                        addStrokeInterval(interval);
                    }
                    
                    lastStrokeTime = currentTime;
                    
                    // 重置峰值检测
                    maxValue = 0;
                    maxValueTime = 0;
                }
            }
        } else {
            if (isAboveThreshold) {
                // 从阈值上方到下方 - 划桨结束
                isAboveThreshold = false;
            }
        }
        
        lastValue = acceleration;
        
        return result;
    }
    
    /**
     * 检测峰值
     * 
     * @param value 当前值
     * @param time 当前时间
     */
    private void detectPeak(double value, long time) {
        // 检测上升沿
        if (value > lastValue) {
            if (!isRising) {
                // 开始上升
                isRising = true;
            }
        } else if (value < lastValue) {
            if (isRising) {
                // 到达峰值（下降沿）
                isRising = false;
                
                // 记录峰值
                if (lastValue > maxValue) {
                    maxValue = lastValue;
                    maxValueTime = time;
                }
            }
        }
        
        // 更新峰值时间
        if (value > maxValue) {
            maxValue = value;
            maxValueTime = time;
        }
    }
    
    /**
     * 检查是否为有效划桨
     * 
     * @param currentTime 当前时间
     * @return true表示有效划桨
     */
    private boolean isValidStroke(long currentTime) {
        // 检查时间间隔
        if (lastStrokeTime > 0) {
            long timeSinceLastStroke = currentTime - lastStrokeTime;
            
            // 检查最小间隔
            if (timeSinceLastStroke < minStrokeInterval) {
                return false; // 太快，不是有效划桨
            }
            
            // 检查最大间隔（重新开始计数）
            if (timeSinceLastStroke > maxStrokeInterval) {
                // 间隔太长，可能是重新开始
                resetStrokeCount();
                return true;
            }
        }
        
        // 检查峰值是否足够大
        if (maxValue < threshold * 1.2) { // 峰值至少是阈值的1.2倍
            return false;
        }
        
        return true;
    }
    
    /**
     * 计算桨频
     * 
     * @param currentTime 当前时间
     * @return 桨频（次/分钟）
     */
    private double calculateStrokeRate(long currentTime) {
        if (intervalCount == 0) {
            return 0;
        }
        
        // 计算平均间隔
        long totalInterval = 0;
        for (int i = 0; i < intervalCount; i++) {
            totalInterval += strokeIntervals[i];
        }
        double averageInterval = (double) totalInterval / intervalCount;
        
        // 转换为桨频（次/分钟）
        return 60000.0 / averageInterval; // 60000ms = 1分钟
    }
    
    /**
     * 添加划桨间隔
     * 
     * @param interval 间隔时间（毫秒）
     */
    private void addStrokeInterval(long interval) {
        strokeIntervals[intervalIndex] = interval;
        intervalIndex = (intervalIndex + 1) % strokeIntervals.length;
        
        if (intervalCount < strokeIntervals.length) {
            intervalCount++;
        }
    }
    
    /**
     * 重置划桨计数
     */
    public void resetStrokeCount() {
        strokeCount = 0;
        currentStrokeRate = 0;
        lastStrokeTime = 0;
        lastPeakTime = 0;
        
        maxValue = 0;
        maxValueTime = 0;
        
        intervalIndex = 0;
        intervalCount = 0;
        for (int i = 0; i < strokeIntervals.length; i++) {
            strokeIntervals[i] = 0;
        }
    }
    
    /**
     * 完全重置检测器
     */
    public void reset() {
        resetStrokeCount();
        
        isAboveThreshold = false;
        lastValue = 0;
        isRising = false;
        
        isEnabled = true;
        detectionStartTime = System.currentTimeMillis();
    }
    
    /**
     * 设置检测阈值
     * 
     * @param threshold 阈值
     */
    public void setThreshold(double threshold) {
        // 这里可以添加阈值范围检查
        this.threshold = threshold;
    }
    
    /**
     * 获取检测阈值
     * 
     * @return 阈值
     */
    public double getThreshold() {
        return threshold;
    }
    
    /**
     * 获取当前桨频
     * 
     * @return 桨频（次/分钟）
     */
    public double getCurrentStrokeRate() {
        return currentStrokeRate;
    }
    
    /**
     * 获取划桨次数
     * 
     * @return 划桨次数
     */
    public int getStrokeCount() {
        return strokeCount;
    }
    
    /**
     * 获取最后划桨时间
     * 
     * @return 最后划桨时间戳
     */
    public long getLastStrokeTime() {
        return lastStrokeTime;
    }
    
    /**
     * 设置检测器状态
     * 
     * @param enabled true表示启用
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
    
    /**
     * 是否启用
     * 
     * @return true表示启用
     */
    public boolean isEnabled() {
        return isEnabled;
    }
    
    /**
     * 获取检测统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - detectionStartTime;
        
        double strokesPerMinute = 0;
        if (elapsedTime > 0 && strokeCount > 0) {
            strokesPerMinute = (double) strokeCount * 60000 / elapsedTime;
        }
        
        return String.format("划桨检测 - 次数: %d, 频率: %.1f spm, 用时: %d秒", 
                strokeCount, currentStrokeRate, elapsedTime / 1000);
    }
    
    @Override
    public String toString() {
        return String.format("StrokeDetector{threshold=%.2f, count=%d, rate=%.1f spm}",
                threshold, strokeCount, currentStrokeRate);
    }
}