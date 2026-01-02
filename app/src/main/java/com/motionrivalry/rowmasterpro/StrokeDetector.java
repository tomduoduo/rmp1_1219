package com.motionrivalry.rowmasterpro;

import java.util.Arrays;

/**
 * 桨频检测器 - 从Speedometer.java提取的专用桨频检测组件
 * 功能：实时检测划船桨频，基于加速度数据进行智能分析
 * 设计原则：100%功能对等 + 类型安全修复 + 状态封装
 */
public class StrokeDetector {

    // ========== 配置参数（构造函数设置） ==========

    /** 最小桨频间隔（毫秒） - 防止重复检测 */
    private final double minStrokeGap;

    /** 最小加速度阈值 - 触发桨频检测的加速度值 */
    private final double minBoatAccel;

    /** 最大空闲时间（毫秒） - 超过此时间重置计数 */
    private final double strokeIdleMax;

    // ========== 核心状态（类型安全修复） ==========

    /** 上次桨频时间戳（修复：double→long） */
    private long strokeCache;

    /** 桨频计数（修复：double→int） */
    private int strokeCount;

    /** 检测器运行状态（新增：替代mStartStatus） */
    private boolean isRunning;

    // ========== 加速度缓存数组（长度30） ==========

    /** 加速度样本缓存数组 */
    private final double[] acclCacheSamples;

    /** 加速度缓存指针 */
    private int acclCachePointer;

    /** 加速度缓存有效大小 */
    private int acclCacheSize;

    // ========== 桨频缓存数组（长度2，暂不改为6） ==========

    /** 桨频样本缓存数组 */
    private final double[] SRCacheSamples;

    /** 桨频缓存指针 */
    private int SRCachePointer;

    /** 桨频缓存有效大小 */
    private int SRCacheSize;

    // ========== 构造函数 ==========

    /**
     * 创建桨频检测器实例
     * 
     * @param minStrokeGap  最小桨频间隔（毫秒）
     * @param minBoatAccel  最小加速度阈值
     * @param strokeIdleMax 最大空闲时间（毫秒）
     */
    public StrokeDetector(double minStrokeGap, double minBoatAccel, double strokeIdleMax) {
        this.minStrokeGap = minStrokeGap;
        this.minBoatAccel = minBoatAccel;
        this.strokeIdleMax = strokeIdleMax;

        // 初始化缓存数组
        this.acclCacheSamples = new double[30]; // 原长度30
        this.SRCacheSamples = new double[2]; // 原长度2（暂不改为6）

        // 初始化状态
        reset();
    }

    // ========== 核心控制方法 ==========

    /**
     * 启动桨频检测
     */
    public void start() {
        this.isRunning = true;
        reset();
    }

    /**
     * 停止桨频检测
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * 检测桨频（核心算法）
     * 
     * @param accelBoat 当前加速度值
     * @param timestamp 当前时间戳（毫秒）
     * @return 检测结果
     */
    public StrokeResult detectStroke(double accelBoat, long timestamp) {
        // 阶段3实现：核心桨频检测算法
        // 1. 更新加速度缓存
        updateAccelCache(accelBoat);

        // 2. 计算平滑加速度
        double smoothedAccel = calculateAverage(acclCacheSamples, acclCacheSize);

        // 3. 检查检测器是否运行
        if (!isRunning) {
            return new StrokeResult(false, 0.0, 0.0, strokeCount);
        }

        // 4. 计算桨频间隔
        long strokeGap = timestamp - strokeCache;

        // 5. 检查是否超过最大空闲时间或首次检测
        android.util.Log.d("StrokeDetector", "strokeCache: " + strokeCache + ", strokeGap: " + strokeGap);
        if (strokeCache == 0) {
            android.util.Log.d("StrokeDetector", "首次检测：跳过时间间隔检查");
        }
        if (strokeCache == 0 || strokeGap > strokeIdleMax) {
            // 修复：当 strokeCache 为 0 时，直接允许首次检测通过
            strokeCache = timestamp; // 更新 strokeCache，防止后续无限制进入
            return new StrokeResult(false, 0.0, 0.0, strokeCount);
        }

        // 6. 核心检测逻辑：加速度超过阈值且间隔足够长
        if (smoothedAccel > minBoatAccel && strokeGap > minStrokeGap) {
            // 更新上次桨频时间戳
            strokeCache = timestamp;

            // 计算瞬时桨频（修复：除零保护）
            double instantRate;
            if (strokeCount < 1) {
                instantRate = 0.0;
            } else {
                if (strokeGap > 0) {
                    instantRate = 60000.0 / strokeGap; // 修复：除零保护
                } else {
                    instantRate = 0.0;
                }
            }

            // 更新桨频缓存
            updateStrokeRateCache(instantRate);

            // 计算平滑桨频
            double smoothedRate = getCurrentStrokeRate();

            // 增加桨频计数
            strokeCount++;

            // 返回检测结果
            return new StrokeResult(true, smoothedRate, instantRate, strokeCount);
        }

        // 7. 未检测到新桨频，返回当前状态
        return new StrokeResult(false, getCurrentStrokeRate(), 0.0, strokeCount);
    }

    /**
     * 重置检测器状态
     */
    public void reset() {
        this.strokeCache = 0;
        this.strokeCount = 0;
        // 修复：保持运行状态，不重置 isRunning

        // 重置加速度缓存
        this.acclCachePointer = 0;
        this.acclCacheSize = 0;
        Arrays.fill(acclCacheSamples, 0.0);

        // 重置桨频缓存
        this.SRCachePointer = 0;
        this.SRCacheSize = 0;
        Arrays.fill(SRCacheSamples, 0.0);
    }

    // ========== 查询方法 ==========

    /**
     * 获取当前桨频计数
     * 
     * @return 桨频总数
     */
    public int getStrokeCount() {
        return strokeCount;
    }

    /**
     * 获取当前平均桨频
     * 
     * @return 平均桨频（桨/分钟）
     */
    public double getCurrentStrokeRate() {
        // 阶段3实现：平滑桨频计算
        if (SRCacheSize == 0) {
            return 0.0; // 修复：边界检查
        }
        return calculateAverage(SRCacheSamples, SRCacheSize);
    }

    /**
     * 检查检测器是否运行中
     * 
     * @return 运行状态
     */
    public boolean isRunning() {
        return isRunning;
    }

    // ========== 辅助方法（private） ==========

    /**
     * 计算数组平均值（原doubleArrAverage方法）
     * 
     * @param array 目标数组
     * @param size  有效大小
     * @return 平均值
     */
    private double calculateAverage(double[] array, int size) {
        // 阶段3实现：数组平均值计算
        // 修复：边界检查
        if (size <= 0) {
            return 0.0;
        }

        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += array[i];
        }
        return sum / size;
    }

    /**
     * 更新加速度缓存
     * 
     * @param value 新加速度值
     */
    private void updateAccelCache(double value) {
        // 阶段3实现：加速度缓存更新（直接复制原逻辑）
        if (acclCacheSize < acclCacheSamples.length) {
            acclCacheSamples[acclCachePointer++] = value;
            acclCacheSize++;
        } else {
            acclCachePointer = acclCachePointer % acclCacheSamples.length; // 修复：边界检查
            acclCacheSamples[acclCachePointer++] = value;
        }
    }

    /**
     * 更新桨频缓存
     * 
     * @param rate 新桨频值
     */
    private void updateStrokeRateCache(double rate) {
        // 阶段3实现：桨频缓存更新（与加速度缓存相同逻辑）
        if (SRCacheSize < SRCacheSamples.length) {
            SRCacheSamples[SRCachePointer++] = rate;
            SRCacheSize++;
        } else {
            SRCachePointer = SRCachePointer % SRCacheSamples.length; // 修复：边界检查
            SRCacheSamples[SRCachePointer++] = rate;
        }
    }

    // ========== 结果类 ==========

    /**
     * 桨频检测结果封装类
     */
    public static class StrokeResult {
        /** 是否检测到新桨频 */
        public final boolean isNewStroke;

        /** 平滑后的桨频（桨/分钟） */
        public final double strokeRate;

        /** 瞬时桨频（桨/分钟） */
        public final double instantaneousRate;

        /** 当前桨频计数 */
        public final int strokeCount;

        public StrokeResult(boolean isNewStroke, double strokeRate,
                double instantaneousRate, int strokeCount) {
            this.isNewStroke = isNewStroke;
            this.strokeRate = strokeRate;
            this.instantaneousRate = instantaneousRate;
            this.strokeCount = strokeCount;
        }
    }
}