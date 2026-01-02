package com.motionrivalry.rowmasterpro.sensor;

/**
 * 划船状态机
 * 管理划船过程中的状态转换
 */
public class RowingStateMachine {
    
    private static final String TAG = "RowingStateMachine";
    
    // 当前状态
    private RowingState currentState;
    
    // 状态转换参数
    private long stateStartTime;
    private long lastStrokeTime;
    private int strokeCountInCurrentState;
    private double averageStrokeRate;
    
    // 状态转换阈值
    private static final long IDLE_TIMEOUT = 5000;          // 空闲超时：5秒
    private static final long READY_TIMEOUT = 10000;      // 准备超时：10秒
    private static final long PAUSE_DETECTION_TIME = 3000; // 暂停检测时间：3秒
    private static final int MIN_STROKES_FOR_ROWING = 3;  // 开始划船的最小划桨次数
    private static final double MIN_ROWING_STROKE_RATE = 10.0; // 最小划船桨频
    
    // 统计信息
    private long totalRowingTime;
    private int totalStrokes;
    private long lastStateChangeTime;
    
    /**
     * 状态变化监听器
     */
    public interface StateChangeListener {
        void onStateChanged(RowingState oldState, RowingState newState, long duration);
        void onRowingStarted();
        void onRowingPaused();
        void onRowingResumed();
        void onRowingCompleted();
    }
    
    private StateChangeListener listener;
    
    /**
     * 构造函数
     */
    public RowingStateMachine() {
        this.currentState = RowingState.IDLE;
        this.stateStartTime = System.currentTimeMillis();
        this.lastStateChangeTime = stateStartTime;
        this.lastStrokeTime = 0;
        this.strokeCountInCurrentState = 0;
        this.averageStrokeRate = 0;
        
        this.totalRowingTime = 0;
        this.totalStrokes = 0;
    }
    
    /**
     * 处理加速度数据
     * 
     * @param acceleration 加速度数据
     */
    public void processAccelerationData(float[] acceleration) {
        long currentTime = System.currentTimeMillis();
        
        switch (currentState) {
            case IDLE:
                handleIdleState(currentTime);
                break;
                
            case READY:
                handleReadyState(currentTime);
                break;
                
            case ROWING:
                handleRowingState(currentTime, acceleration);
                break;
                
            case PAUSED:
                handlePausedState(currentTime);
                break;
                
            case COMPLETED:
                handleCompletedState(currentTime);
                break;
        }
    }
    
    /**
     * 处理桨频检测事件
     * 
     * @param strokeRate 检测到的桨频
     * @param strokeCount 划桨次数
     */
    public void processStrokeDetection(double strokeRate, int strokeCount) {
        long currentTime = System.currentTimeMillis();
        
        // 更新最后划桨时间
        if (strokeCount > 0) {
            lastStrokeTime = currentTime;
            strokeCountInCurrentState++;
            totalStrokes++;
        }
        
        // 更新平均桨频
        updateAverageStrokeRate(strokeRate);
        
        switch (currentState) {
            case IDLE:
                // 检测到划桨，转移到准备状态
                if (strokeRate > 0) {
                    changeState(RowingState.READY);
                }
                break;
                
            case READY:
                // 检测到有效划桨，可能开始划船
                if (strokeRate >= MIN_ROWING_STROKE_RATE && strokeCountInCurrentState >= MIN_STROKES_FOR_ROWING) {
                    changeState(RowingState.ROWING);
                    if (listener != null) {
                        listener.onRowingStarted();
                    }
                }
                break;
                
            case ROWING:
                // 正常划船状态，继续检测
                break;
                
            case PAUSED:
                // 从暂停状态恢复
                if (strokeRate >= MIN_ROWING_STROKE_RATE) {
                    changeState(RowingState.ROWING);
                    if (listener != null) {
                        listener.onRowingResumed();
                    }
                }
                break;
                
            case COMPLETED:
                // 重新开始
                changeState(RowingState.READY);
                break;
        }
    }
    
    /**
     * 处理空闲状态
     * 
     * @param currentTime 当前时间
     */
    private void handleIdleState(long currentTime) {
        // 空闲状态下，如果超时则保持空闲
        long timeInState = currentTime - stateStartTime;
        if (timeInState > IDLE_TIMEOUT) {
            // 可以添加一些空闲状态的处理逻辑
        }
    }
    
    /**
     * 处理准备状态
     * 
     * @param currentTime 当前时间
     */
    private void handleReadyState(long currentTime) {
        // 准备状态下，如果超时则回到空闲
        long timeInState = currentTime - stateStartTime;
        if (timeInState > READY_TIMEOUT) {
            changeState(RowingState.IDLE);
        }
    }
    
    /**
     * 处理划船状态
     * 
     * @param currentTime 当前时间
     * @param acceleration 加速度数据
     */
    private void handleRowingState(long currentTime, float[] acceleration) {
        // 检查是否长时间没有划桨（进入暂停状态）
        if (lastStrokeTime > 0) {
            long timeSinceLastStroke = currentTime - lastStrokeTime;
            if (timeSinceLastStroke > PAUSE_DETECTION_TIME && averageStrokeRate < MIN_ROWING_STROKE_RATE) {
                changeState(RowingState.PAUSED);
                if (listener != null) {
                    listener.onRowingPaused();
                }
            }
        }
    }
    
    /**
     * 处理暂停状态
     * 
     * @param currentTime 当前时间
     */
    private void handlePausedState(long currentTime) {
        // 暂停状态下，如果长时间没有活动则完成
        if (lastStrokeTime > 0) {
            long timeSinceLastStroke = currentTime - lastStrokeTime;
            if (timeSinceLastStroke > PAUSE_DETECTION_TIME * 2) { // 6秒
                changeState(RowingState.COMPLETED);
                if (listener != null) {
                    listener.onRowingCompleted();
                }
            }
        }
    }
    
    /**
     * 处理完成状态
     * 
     * @param currentTime 当前时间
     */
    private void handleCompletedState(long currentTime) {
        // 完成状态下，可以添加一些清理逻辑
        long timeInState = currentTime - stateStartTime;
        // 可以添加自动重置逻辑
    }
    
    /**
     * 手动开始划船
     */
    public void startRowing() {
        if (currentState == RowingState.IDLE || currentState == RowingState.COMPLETED) {
            changeState(RowingState.READY);
        }
    }
    
    /**
     * 手动暂停划船
     */
    public void pauseRowing() {
        if (currentState == RowingState.ROWING) {
            changeState(RowingState.PAUSED);
            if (listener != null) {
                listener.onRowingPaused();
            }
        }
    }
    
    /**
     * 手动恢复划船
     */
    public void resumeRowing() {
        if (currentState == RowingState.PAUSED) {
            changeState(RowingState.ROWING);
            if (listener != null) {
                listener.onRowingResumed();
            }
        }
    }
    
    /**
     * 手动完成划船
     */
    public void completeRowing() {
        if (currentState == RowingState.ROWING || currentState == RowingState.PAUSED) {
            changeState(RowingState.COMPLETED);
            if (listener != null) {
                listener.onRowingCompleted();
            }
        }
    }
    
    /**
     * 重置状态机
     */
    public void reset() {
        RowingState oldState = currentState;
        currentState = RowingState.IDLE;
        
        stateStartTime = System.currentTimeMillis();
        lastStateChangeTime = stateStartTime;
        lastStrokeTime = 0;
        strokeCountInCurrentState = 0;
        averageStrokeRate = 0;
        
        totalRowingTime = 0;
        totalStrokes = 0;
        
        if (listener != null && oldState != RowingState.IDLE) {
            listener.onStateChanged(oldState, RowingState.IDLE, 0);
        }
    }
    
    /**
     * 改变状态
     * 
     * @param newState 新状态
     */
    private void changeState(RowingState newState) {
        if (currentState == newState) {
            return;
        }
        
        RowingState oldState = currentState;
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - stateStartTime;
        
        // 更新划船时间统计
        if (oldState == RowingState.ROWING) {
            totalRowingTime += duration;
        }
        
        currentState = newState;
        stateStartTime = currentTime;
        strokeCountInCurrentState = 0;
        
        // 通知监听器
        if (listener != null) {
            listener.onStateChanged(oldState, newState, duration);
        }
    }
    
    /**
     * 更新平均桨频
     * 
     * @param strokeRate 当前桨频
     */
    private void updateAverageStrokeRate(double strokeRate) {
        // 简单的移动平均算法
        if (averageStrokeRate == 0) {
            averageStrokeRate = strokeRate;
        } else {
            averageStrokeRate = (averageStrokeRate * 0.8) + (strokeRate * 0.2);
        }
    }
    
    /**
     * 获取当前状态
     * 
     * @return 当前状态
     */
    public RowingState getCurrentState() {
        return currentState;
    }
    
    /**
     * 获取当前状态持续时间
     * 
     * @return 持续时间（毫秒）
     */
    public long getCurrentStateDuration() {
        return System.currentTimeMillis() - stateStartTime;
    }
    
    /**
     * 获取总划船时间
     * 
     * @return 总划船时间（毫秒）
     */
    public long getTotalRowingTime() {
        long total = totalRowingTime;
        if (currentState == RowingState.ROWING) {
            total += (System.currentTimeMillis() - stateStartTime);
        }
        return total;
    }
    
    /**
     * 获取总划桨次数
     * 
     * @return 总划桨次数
     */
    public int getTotalStrokes() {
        return totalStrokes;
    }
    
    /**
     * 获取平均桨频
     * 
     * @return 平均桨频（次/分钟）
     */
    public double getAverageStrokeRate() {
        return averageStrokeRate;
    }
    
    /**
     * 获取当前状态划桨次数
     * 
     * @return 当前状态划桨次数
     */
    public int getStrokeCountInCurrentState() {
        return strokeCountInCurrentState;
    }
    
    /**
     * 获取最后划桨时间
     * 
     * @return 最后划桨时间（毫秒）
     */
    public long getLastStrokeTime() {
        return lastStrokeTime;
    }
    
    /**
     * 设置状态变化监听器
     * 
     * @param listener 监听器
     */
    public void setStateChangeListener(StateChangeListener listener) {
        this.listener = listener;
    }
    
    /**
     * 获取状态描述
     * 
     * @param state 状态
     * @return 状态描述
     */
    public static String getStateDescription(RowingState state) {
        switch (state) {
            case IDLE:
                return "等待开始";
            case READY:
                return "准备开始";
            case ROWING:
                return "正在划船";
            case PAUSED:
                return "暂停中";
            case COMPLETED:
                return "训练完成";
            case ERROR:
                return "发生错误";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 获取状态统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        long currentTime = System.currentTimeMillis();
        long totalTime = currentTime - lastStateChangeTime;
        long rowingTime = getTotalRowingTime();
        
        return String.format("状态机统计 - 当前状态: %s, 持续时间: %d秒, " +
                           "总划船时间: %d秒, 总划桨次数: %d, 平均桨频: %.1f spm",
                getStateDescription(currentState),
                getCurrentStateDuration() / 1000,
                rowingTime / 1000,
                totalStrokes,
                averageStrokeRate);
    }
    
    @Override
    public String toString() {
        return String.format("RowingStateMachine{state=%s, duration=%dms, strokes=%d, rate=%.1f spm}",
                currentState, getCurrentStateDuration(), totalStrokes, averageStrokeRate);
    }
}