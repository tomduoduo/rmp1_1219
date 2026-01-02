package com.motionrivalry.rowmasterpro.training;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.motionrivalry.rowmasterpro.data.DataRecorder;
import com.motionrivalry.rowmasterpro.sensor.ProcessedData;
import com.motionrivalry.rowmasterpro.sensor.RowingState;
import com.motionrivalry.rowmasterpro.sensor.SensorData;
import com.motionrivalry.rowmasterpro.sensor.SensorDataProcessor;

/**
 * 训练会话管理器
 * 负责管理整个训练会话的生命周期，包括数据记录、状态管理和统计计算
 */
public class TrainingSessionManager {
    
    private static final String TAG = "TrainingSessionManager";
    
    // 上下文
    private Context context;
    
    // 依赖组件
    private SensorDataProcessor sensorDataProcessor;
    private DataRecorder dataRecorder;
    
    // 会话状态
    private boolean isSessionActive;
    private long sessionStartTime;
    private long sessionEndTime;
    private String sessionId;
    
    // 训练数据
    private TrainingSessionData sessionData;
    private TrainingStatistics statistics;
    
    // 配置参数
    private int targetStrokeRate;
    private int targetDuration;
    private double targetDistance;
    private boolean autoRecord;
    
    // 回调接口
    private TrainingSessionCallback callback;
    
    /**
     * 训练会话回调接口
     */
    public interface TrainingSessionCallback {
        void onSessionStarted(String sessionId);
        void onSessionPaused();
        void onSessionResumed();
        void onSessionCompleted(TrainingSessionData data);
        void onSessionCancelled();
        void onProgressUpdate(TrainingProgress progress);
        void onTargetReached(String targetType);
        void onError(String error);
    }
    
    /**
     * 构造函数
     * 
     * @param context 上下文
     * @param sensorDataProcessor 传感器数据处理器
     */
    public TrainingSessionManager(Context context, SensorDataProcessor sensorDataProcessor) {
        this.context = context;
        this.sensorDataProcessor = sensorDataProcessor;
        this.isSessionActive = false;
        this.sessionStartTime = 0;
        this.sessionEndTime = 0;
        
        // 初始化数据记录器
        String baseDir = context.getFilesDir() + "/training_data/";
        this.dataRecorder = new DataRecorder(baseDir);
        
        // 初始化会话数据
        this.sessionData = new TrainingSessionData();
        this.statistics = new TrainingStatistics();
        
        // 设置默认配置
        this.targetStrokeRate = 0; // 无目标
        this.targetDuration = 0;   // 无目标
        this.targetDistance = 0;   // 无目标
        this.autoRecord = true;    // 自动记录
        
        // 设置传感器数据回调
        setupSensorDataCallback();
    }
    
    /**
     * 开始训练会话
     * 
     * @param sessionName 会话名称
     * @return true表示成功，false表示失败
     */
    public boolean startSession(String sessionName) {
        if (isSessionActive) {
            Log.w(TAG, "会话已在进行中");
            return false;
        }
        
        try {
            // 生成会话ID
            sessionId = generateSessionId();
            sessionStartTime = System.currentTimeMillis();
            sessionEndTime = 0;
            
            // 重置会话数据
            sessionData.reset();
            sessionData.sessionId = sessionId;
            sessionData.sessionName = sessionName;
            sessionData.startTime = sessionStartTime;
            
            // 重置统计信息
            statistics.reset();
            
            // 启动传感器数据处理
            sensorDataProcessor.startProcessing();
            
            // 开始数据记录（如果启用自动记录）
            if (autoRecord) {
                startDataRecording();
            }
            
            // 更新状态
            isSessionActive = true;
            
            // 通知回调
            if (callback != null) {
                callback.onSessionStarted(sessionId);
            }
            
            Log.i(TAG, "训练会话已启动: " + sessionId);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "启动训练会话失败", e);
            if (callback != null) {
                callback.onError("启动训练会话失败: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * 暂停训练会话
     * 
     * @return true表示成功，false表示失败
     */
    public boolean pauseSession() {
        if (!isSessionActive) {
            Log.w(TAG, "会话未在进行中");
            return false;
        }
        
        try {
            // 暂停传感器数据处理
            sensorDataProcessor.stopProcessing();
            
            // 暂停数据记录
            if (autoRecord) {
                stopDataRecording();
            }
            
            // 更新会话数据
            sessionData.pauseCount++;
            sessionData.lastPauseTime = System.currentTimeMillis();
            
            // 通知回调
            if (callback != null) {
                callback.onSessionPaused();
            }
            
            Log.i(TAG, "训练会话已暂停");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "暂停训练会话失败", e);
            if (callback != null) {
                callback.onError("暂停训练会话失败: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * 恢复训练会话
     * 
     * @return true表示成功，false表示失败
     */
    public boolean resumeSession() {
        if (!isSessionActive) {
            Log.w(TAG, "会话未在进行中");
            return false;
        }
        
        try {
            // 恢复传感器数据处理
            sensorDataProcessor.startProcessing();
            
            // 恢复数据记录
            if (autoRecord) {
                startDataRecording();
            }
            
            // 更新会话数据
            sessionData.resumeCount++;
            sessionData.totalPauseTime += System.currentTimeMillis() - sessionData.lastPauseTime;
            
            // 通知回调
            if (callback != null) {
                callback.onSessionResumed();
            }
            
            Log.i(TAG, "训练会话已恢复");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "恢复训练会话失败", e);
            if (callback != null) {
                callback.onError("恢复训练会话失败: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * 完成训练会话
     * 
     * @return true表示成功，false表示失败
     */
    public boolean completeSession() {
        if (!isSessionActive) {
            Log.w(TAG, "会话未在进行中");
            return false;
        }
        
        try {
            sessionEndTime = System.currentTimeMillis();
            
            // 停止传感器数据处理
            sensorDataProcessor.stopProcessing();
            
            // 停止数据记录
            if (autoRecord) {
                stopDataRecording();
            }
            
            // 更新会话数据
            sessionData.endTime = sessionEndTime;
            sessionData.totalDuration = sessionEndTime - sessionStartTime;
            sessionData.isCompleted = true;
            
            // 计算最终统计
            calculateFinalStatistics();
            
            // 更新状态
            isSessionActive = false;
            
            // 通知回调
            if (callback != null) {
                callback.onSessionCompleted(sessionData);
            }
            
            Log.i(TAG, "训练会话已完成: " + sessionId);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "完成训练会话失败", e);
            if (callback != null) {
                callback.onError("完成训练会话失败: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * 取消训练会话
     * 
     * @return true表示成功，false表示失败
     */
    public boolean cancelSession() {
        if (!isSessionActive) {
            Log.w(TAG, "会话未在进行中");
            return false;
        }
        
        try {
            sessionEndTime = System.currentTimeMillis();
            
            // 停止传感器数据处理
            sensorDataProcessor.stopProcessing();
            
            // 停止数据记录（删除数据）
            if (autoRecord) {
                cancelDataRecording();
            }
            
            // 更新会话数据
            sessionData.endTime = sessionEndTime;
            sessionData.isCancelled = true;
            
            // 更新状态
            isSessionActive = false;
            
            // 通知回调
            if (callback != null) {
                callback.onSessionCancelled();
            }
            
            Log.i(TAG, "训练会话已取消: " + sessionId);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "取消训练会话失败", e);
            if (callback != null) {
                callback.onError("取消训练会话失败: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * 开始数据记录
     */
    private void startDataRecording() {
        String fileName = "training_" + sessionId;
        String[] headers = SensorData.getCSVHeader();
        
        if (dataRecorder.startRecording(fileName, headers)) {
            Log.i(TAG, "数据记录已启动: " + fileName);
        } else {
            Log.e(TAG, "数据记录启动失败");
        }
    }
    
    /**
     * 停止数据记录
     */
    private void stopDataRecording() {
        String filePath = dataRecorder.stopRecording();
        if (filePath != null) {
            sessionData.dataFilePath = filePath;
            Log.i(TAG, "数据记录已停止: " + filePath);
        }
    }
    
    /**
     * 取消数据记录
     */
    private void cancelDataRecording() {
        dataRecorder.stopRecording();
        // 删除数据文件
        if (sessionData.dataFilePath != null) {
            dataRecorder.deleteDataFile(sessionData.dataFilePath);
            sessionData.dataFilePath = null;
        }
    }
    
    /**
     * 处理传感器数据更新
     * 
     * @param data 传感器数据
     */
    private void handleSensorDataUpdate(SensorData data) {
        if (!isSessionActive) {
            return;
        }
        
        // 更新会话数据
        updateSessionData(data);
        
        // 记录数据
        if (autoRecord && dataRecorder.isRecording()) {
            dataRecorder.recordData(data.getCSVDataRow());
        }
        
        // 检查目标达成
        checkTargetAchievement();
        
        // 更新进度
        updateProgress();
    }
    
    /**
     * 更新会话数据
     * 
     * @param data 传感器数据
     */
    private void updateSessionData(SensorData data) {
        // 更新距离（基于GPS）
        if (sessionData.lastLocation != null && data.latitude != 0 && data.longitude != 0) {
            float[] results = new float[1];
            android.location.Location.distanceBetween(
                sessionData.lastLocation.getLatitude(), sessionData.lastLocation.getLongitude(),
                data.latitude, data.longitude, results);
            sessionData.totalDistance += results[0];
        }
        
        // 更新位置
        if (data.latitude != 0 && data.longitude != 0) {
            sessionData.lastLocation = new Location("sensor");
            sessionData.lastLocation.setLatitude(data.latitude);
            sessionData.lastLocation.setLongitude(data.longitude);
        }
        
        // 更新桨频统计
        if (data.strokeRate > 0) {
            sessionData.strokeRateSum += data.strokeRate;
            sessionData.strokeRateCount++;
            
            if (data.strokeRate > sessionData.maxStrokeRate) {
                sessionData.maxStrokeRate = data.strokeRate;
            }
            
            if (data.strokeRate < sessionData.minStrokeRate || sessionData.minStrokeRate == 0) {
                sessionData.minStrokeRate = data.strokeRate;
            }
        }
        
        // 更新心率统计
        if (data.heartRate > 0) {
            sessionData.heartRateSum += data.heartRate;
            sessionData.heartRateCount++;
            
            if (data.heartRate > sessionData.maxHeartRate) {
                sessionData.maxHeartRate = data.heartRate;
            }
            
            if (data.heartRate < sessionData.minHeartRate || sessionData.minHeartRate == 0) {
                sessionData.minHeartRate = data.heartRate;
            }
        }
        
        // 更新划桨次数
        if (data.strokeCount > sessionData.strokeCount) {
            sessionData.strokeCount = data.strokeCount;
        }
        
        // 更新速度统计
        if (data.boatSpeed > 0) {
            sessionData.speedSum += data.boatSpeed;
            sessionData.speedCount++;
            
            if (data.boatSpeed > sessionData.maxSpeed) {
                sessionData.maxSpeed = data.boatSpeed;
            }
        }
    }
    
    /**
     * 检查目标达成
     */
    private void checkTargetAchievement() {
        if (callback == null) {
            return;
        }
        
        // 检查时间目标
        if (targetDuration > 0) {
            long currentDuration = System.currentTimeMillis() - sessionStartTime;
            if (currentDuration >= targetDuration * 60 * 1000) { // 转换为毫秒
                callback.onTargetReached("duration");
                targetDuration = 0; // 避免重复通知
            }
        }
        
        // 检查距离目标
        if (targetDistance > 0 && sessionData.totalDistance >= targetDistance) {
            callback.onTargetReached("distance");
            targetDistance = 0; // 避免重复通知
        }
        
        // 检查桨频目标
        if (targetStrokeRate > 0) {
            double currentStrokeRate = getAverageStrokeRate();
            if (currentStrokeRate >= targetStrokeRate) {
                callback.onTargetReached("strokeRate");
                targetStrokeRate = 0; // 避免重复通知
            }
        }
    }
    
    /**
     * 更新进度
     */
    private void updateProgress() {
        if (callback == null) {
            return;
        }
        
        TrainingProgress progress = new TrainingProgress();
        progress.sessionId = sessionId;
        progress.elapsedTime = System.currentTimeMillis() - sessionStartTime;
        progress.totalDistance = sessionData.totalDistance;
        progress.strokeCount = sessionData.strokeCount;
        progress.averageStrokeRate = getAverageStrokeRate();
        progress.averageHeartRate = getAverageHeartRate();
        progress.averageSpeed = getAverageSpeed();
        progress.currentSpeed = getCurrentSpeed();
        
        callback.onProgressUpdate(progress);
    }
    
    /**
     * 计算最终统计
     */
    private void calculateFinalStatistics() {
        // 计算平均桨频
        if (sessionData.strokeRateCount > 0) {
            statistics.averageStrokeRate = sessionData.strokeRateSum / sessionData.strokeRateCount;
        }
        
        // 计算平均心率
        if (sessionData.heartRateCount > 0) {
            statistics.averageHeartRate = sessionData.heartRateSum / sessionData.heartRateCount;
        }
        
        // 计算平均速度
        if (sessionData.speedCount > 0) {
            statistics.averageSpeed = sessionData.speedSum / sessionData.speedCount;
        }
        
        // 计算效率
        if (statistics.averageSpeed > 0 && statistics.averageStrokeRate > 0) {
            statistics.efficiency = (statistics.averageSpeed / statistics.averageStrokeRate) * 100;
        }
        
        // 设置其他统计信息
        statistics.totalDuration = sessionData.totalDuration;
        statistics.totalDistance = sessionData.totalDistance;
        statistics.totalStrokes = sessionData.strokeCount;
        statistics.maxStrokeRate = sessionData.maxStrokeRate;
        statistics.minStrokeRate = sessionData.minStrokeRate;
        statistics.maxHeartRate = sessionData.maxHeartRate;
        statistics.minHeartRate = sessionData.minHeartRate;
        statistics.maxSpeed = sessionData.maxSpeed;
    }
    
    /**
     * 生成会话ID
     * 
     * @return 会话ID
     */
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }
    
    /**
     * 设置传感器数据回调
     */
    private void setupSensorDataCallback() {
        sensorDataProcessor.setCallback(new SensorDataProcessor.SensorDataCallback() {
            @Override
            public void onSensorDataUpdated(SensorData data) {
                handleSensorDataUpdate(data);
            }
            
            @Override
            public void onStrokeDetected(double strokeRate, double strokeCount) {
                // 桨频检测事件
                Log.d(TAG, "检测到划桨: " + strokeRate + " spm");
            }
            
            @Override
            public void onStateChanged(RowingState newState) {
                // 状态变化事件
                Log.d(TAG, "划船状态变化: " + newState);
            }
            
            @Override
            public void onHeartRateDataReceived(String deviceId, int heartRate) {
                // 心率数据事件
                Log.d(TAG, "心率数据: " + heartRate + " bpm");
            }
            
            @Override
            public void onLocationDataUpdated(Location location, double speed) {
                // 位置数据事件
                Log.d(TAG, "位置更新: " + speed + " km/h");
            }
            
            @Override
            public void onError(String error) {
                // 错误事件
                Log.e(TAG, "传感器数据处理错误: " + error);
                if (callback != null) {
                    callback.onError("传感器数据处理错误: " + error);
                }
            }
        });
    }
    
    // Getter和Setter方法
    
    /**
     * 设置会话回调
     * 
     * @param callback 回调接口
     */
    public void setCallback(TrainingSessionCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 获取当前会话ID
     * 
     * @return 会话ID
     */
    public String getCurrentSessionId() {
        return sessionId;
    }
    
    /**
     * 获取会话数据
     * 
     * @return 会话数据
     */
    public TrainingSessionData getSessionData() {
        return sessionData;
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息
     */
    public TrainingStatistics getStatistics() {
        return statistics;
    }
    
    /**
     * 是否会话活跃
     * 
     * @return true表示会话活跃
     */
    public boolean isSessionActive() {
        return isSessionActive;
    }
    
    /**
     * 获取会话开始时间
     * 
     * @return 开始时间戳
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }
    
    /**
     * 获取会话持续时间
     * 
     * @return 持续时间（毫秒）
     */
    public long getSessionDuration() {
        if (sessionStartTime == 0) {
            return 0;
        }
        
        long endTime = sessionEndTime > 0 ? sessionEndTime : System.currentTimeMillis();
        return endTime - sessionStartTime;
    }
    
    /**
     * 获取平均桨频
     * 
     * @return 平均桨频
     */
    public double getAverageStrokeRate() {
        return statistics.averageStrokeRate;
    }
    
    /**
     * 获取平均心率
     * 
     * @return 平均心率
     */
    public double getAverageHeartRate() {
        return statistics.averageHeartRate;
    }
    
    /**
     * 获取平均速度
     * 
     * @return 平均速度
     */
    public double getAverageSpeed() {
        return statistics.averageSpeed;
    }
    
    /**
     * 获取当前速度
     * 
     * @return 当前速度
     */
    public double getCurrentSpeed() {
        return sessionData.lastLocation != null ? sessionData.lastSpeed : 0;
    }
    
    /**
     * 获取总距离
     * 
     * @return 总距离
     */
    public double getTotalDistance() {
        return sessionData.totalDistance;
    }
    
    /**
     * 获取总划桨次数
     * 
     * @return 总划桨次数
     */
    public int getTotalStrokes() {
        return sessionData.strokeCount;
    }
    
    /**
     * 设置目标桨频
     * 
     * @param targetStrokeRate 目标桨频
     */
    public void setTargetStrokeRate(int targetStrokeRate) {
        this.targetStrokeRate = targetStrokeRate;
    }
    
    /**
     * 设置目标持续时间（分钟）
     * 
     * @param targetDuration 目标持续时间
     */
    public void setTargetDuration(int targetDuration) {
        this.targetDuration = targetDuration;
    }
    
    /**
     * 设置目标距离（米）
     * 
     * @param targetDistance 目标距离
     */
    public void setTargetDistance(double targetDistance) {
        this.targetDistance = targetDistance;
    }
    
    /**
     * 设置自动记录
     * 
     * @param autoRecord 是否自动记录
     */
    public void setAutoRecord(boolean autoRecord) {
        this.autoRecord = autoRecord;
    }
    
    /**
     * 获取数据记录器
     * 
     * @return 数据记录器
     */
    public DataRecorder getDataRecorder() {
        return dataRecorder;
    }
}