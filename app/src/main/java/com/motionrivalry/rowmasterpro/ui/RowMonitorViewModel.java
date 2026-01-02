package com.motionrivalry.rowmasterpro.ui;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.motionrivalry.rowmasterpro.sensor.ProcessedData;
import com.motionrivalry.rowmasterpro.sensor.RowingState;
import com.motionrivalry.rowmasterpro.sensor.SensorData;
import com.motionrivalry.rowmasterpro.sensor.SensorDataProcessor;
import com.motionrivalry.rowmasterpro.training.TrainingProgress;
import com.motionrivalry.rowmasterpro.training.TrainingSessionData;
import com.motionrivalry.rowmasterpro.training.TrainingSessionManager;
import com.motionrivalry.rowmasterpro.training.TrainingStatistics;

/**
 * RowMonitor视图模型
 * 负责管理UI状态和业务逻辑，遵循MVVM架构模式
 */
public class RowMonitorViewModel extends ViewModel {

    private static final String TAG = "RowMonitorViewModel";

    // 传感器数据处理
    private SensorDataProcessor sensorDataProcessor;
    private TrainingSessionManager trainingSessionManager;

    // UI状态数据
    private MutableLiveData<SensorData> sensorData;
    private MutableLiveData<ProcessedData> processedData;
    private MutableLiveData<TrainingProgress> trainingProgress;
    private MutableLiveData<RowingState> rowingState;
    private MutableLiveData<Boolean> isRecording;
    private MutableLiveData<String> errorMessage;

    // 训练会话状态
    private MutableLiveData<String> sessionId;
    private MutableLiveData<TrainingSessionData> sessionData;
    private MutableLiveData<TrainingStatistics> sessionStatistics;

    // 配置参数
    private int samplingRate = 32; // Hz
    private boolean autoRecord = true;

    /**
     * 构造函数
     */
    public RowMonitorViewModel() {
        initializeLiveData();
    }

    /**
     * 初始化LiveData
     */
    private void initializeLiveData() {
        sensorData = new MutableLiveData<>(new SensorData());
        processedData = new MutableLiveData<>(new ProcessedData());
        trainingProgress = new MutableLiveData<>(new TrainingProgress());
        rowingState = new MutableLiveData<>(RowingState.IDLE);
        isRecording = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>("");

        sessionId = new MutableLiveData<>("");
        sessionData = new MutableLiveData<>(new TrainingSessionData());
        sessionStatistics = new MutableLiveData<>(new TrainingStatistics());
    }

    /**
     * 初始化（在Activity的onCreate中调用）
     * 
     * @param context 上下文
     */
    public void initialize(Context context) {
        // 初始化传感器数据处理器
        sensorDataProcessor = new SensorDataProcessor(context);
        sensorDataProcessor.setSamplingRate(samplingRate);
        sensorDataProcessor.setCallback(new SensorDataProcessor.SensorDataCallback() {
            @Override
            public void onSensorDataUpdated(SensorData data) {
                handleSensorDataUpdate(data);
            }

            @Override
            public void onStrokeDetected(double strokeRate, double strokeCount) {
                handleStrokeDetection(strokeRate, strokeCount);
            }

            @Override
            public void onStateChanged(RowingState newState) {
                handleStateChange(newState);
            }

            @Override
            public void onHeartRateDataReceived(String deviceId, int heartRate) {
                handleHeartRateData(deviceId, heartRate);
            }

            @Override
            public void onLocationDataUpdated(Location location, double speed) {
                handleLocationData(location, speed);
            }

            @Override
            public void onError(String error) {
                handleSensorError(error);
            }
        });

        // 初始化训练会话管理器
        trainingSessionManager = new TrainingSessionManager(context, sensorDataProcessor);
        trainingSessionManager.setCallback(new TrainingSessionManager.TrainingSessionCallback() {
            @Override
            public void onSessionStarted(String sessionId) {
                handleSessionStarted(sessionId);
            }

            @Override
            public void onSessionPaused() {
                handleSessionPaused();
            }

            @Override
            public void onSessionResumed() {
                handleSessionResumed();
            }

            @Override
            public void onSessionCompleted(TrainingSessionData data) {
                handleSessionCompleted(data);
            }

            @Override
            public void onSessionCancelled() {
                handleSessionCancelled();
            }

            @Override
            public void onProgressUpdate(TrainingProgress progress) {
                handleProgressUpdate(progress);
            }

            @Override
            public void onTargetReached(String targetType) {
                handleTargetReached(targetType);
            }

            @Override
            public void onError(String error) {
                handleTrainingError(error);
            }
        });

        // 设置自动记录
        trainingSessionManager.setAutoRecord(autoRecord);
    }

    /**
     * 开始传感器数据处理
     */
    public void startSensorProcessing() {
        if (sensorDataProcessor != null) {
            sensorDataProcessor.startProcessing();
        }
    }

    /**
     * 停止传感器数据处理
     */
    public void stopSensorProcessing() {
        if (sensorDataProcessor != null) {
            sensorDataProcessor.stopProcessing();
        }
    }

    /**
     * 开始训练会话
     * 
     * @param sessionName 会话名称
     */
    public void startTrainingSession(String sessionName) {
        if (trainingSessionManager != null) {
            trainingSessionManager.startSession(sessionName);
        }
    }

    /**
     * 暂停训练会话
     */
    public void pauseTrainingSession() {
        if (trainingSessionManager != null) {
            trainingSessionManager.pauseSession();
        }
    }

    /**
     * 恢复训练会话
     */
    public void resumeTrainingSession() {
        if (trainingSessionManager != null) {
            trainingSessionManager.resumeSession();
        }
    }

    /**
     * 完成训练会话
     */
    public void completeTrainingSession() {
        if (trainingSessionManager != null) {
            trainingSessionManager.completeSession();
        }
    }

    /**
     * 取消训练会话
     */
    public void cancelTrainingSession() {
        if (trainingSessionManager != null) {
            trainingSessionManager.cancelSession();
        }
    }

    /**
     * 处理传感器数据更新
     * 
     * @param data 传感器数据
     */
    private void handleSensorDataUpdate(SensorData data) {
        sensorData.postValue(data);

        // 更新处理后的数据
        ProcessedData processed = new ProcessedData();
        processed.copyFrom(data);
        processedData.postValue(processed);

        // 更新训练进度
        updateTrainingProgress();
    }

    /**
     * 处理桨频检测
     * 
     * @param strokeRate  桨频
     * @param strokeCount 划桨次数
     */
    private void handleStrokeDetection(double strokeRate, double strokeCount) {
        // 桨频检测事件可以通过LiveData通知UI
        // 这里可以添加额外的逻辑
    }

    /**
     * 处理状态变化
     * 
     * @param newState 新状态
     */
    private void handleStateChange(RowingState newState) {
        rowingState.postValue(newState);
    }

    /**
     * 处理心率数据
     * 
     * @param deviceId  设备ID
     * @param heartRate 心率
     */
    private void handleHeartRateData(String deviceId, int heartRate) {
        // 心率数据更新
    }

    /**
     * 处理位置数据
     * 
     * @param location 位置
     * @param speed    速度
     */
    private void handleLocationData(Location location, double speed) {
        // 位置数据更新
        if (sensorDataProcessor != null) {
            sensorDataProcessor.processLocationUpdate(location);
        }
    }

    /**
     * 处理位置数据（公开方法，供Activity调用）
     * 
     * @param location 位置信息
     */
    public void processLocationData(Location location) {
        if (sensorDataProcessor != null) {
            sensorDataProcessor.processLocationUpdate(location);
        }
    }

    /**
     * 处理心率数据（公开方法，供Activity调用）
     * 
     * @param deviceId  设备ID
     * @param heartRate 心率值
     */
    public void processHeartRateData(String deviceId, int heartRate) {
        if (sensorDataProcessor != null) {
            sensorDataProcessor.processHeartRateData(deviceId, heartRate);
        }
    }

    /**
     * 处理传感器错误
     * 
     * @param error 错误信息
     */
    private void handleSensorError(String error) {
        errorMessage.postValue("传感器错误: " + error);
    }

    /**
     * 处理会话开始
     * 
     * @param sessionId 会话ID
     */
    private void handleSessionStarted(String sessionId) {
        this.sessionId.postValue(sessionId);
        isRecording.postValue(true);

        // 可以在这里添加会话开始的UI逻辑
    }

    /**
     * 处理会话暂停
     */
    private void handleSessionPaused() {
        // 会话暂停处理
    }

    /**
     * 处理会话恢复
     */
    private void handleSessionResumed() {
        // 会话恢复处理
    }

    /**
     * 处理会话完成
     * 
     * @param data 会话数据
     */
    private void handleSessionCompleted(TrainingSessionData data) {
        sessionData.postValue(data);
        isRecording.postValue(false);

        // 获取统计信息
        if (trainingSessionManager != null) {
            TrainingStatistics statistics = trainingSessionManager.getStatistics();
            sessionStatistics.postValue(statistics);
        }
    }

    /**
     * 处理会话取消
     */
    private void handleSessionCancelled() {
        isRecording.postValue(false);
        sessionId.postValue("");
    }

    /**
     * 处理进度更新
     * 
     * @param progress 训练进度
     */
    private void handleProgressUpdate(TrainingProgress progress) {
        trainingProgress.postValue(progress);
    }

    /**
     * 处理目标达成
     * 
     * @param targetType 目标类型
     */
    private void handleTargetReached(String targetType) {
        // 目标达成处理
        String message = "";
        switch (targetType) {
            case "duration":
                message = "时间目标已达成！";
                break;
            case "distance":
                message = "距离目标已达成！";
                break;
            case "strokeRate":
                message = "桨频目标已达成！";
                break;
        }
        errorMessage.postValue(message);
    }

    /**
     * 处理训练错误
     * 
     * @param error 错误信息
     */
    private void handleTrainingError(String error) {
        errorMessage.postValue("训练错误: " + error);
    }

    /**
     * 更新训练进度
     */
    private void updateTrainingProgress() {
        // 基于当前传感器数据更新训练进度
        if (trainingSessionManager != null && trainingSessionManager.isSessionActive()) {
            // 进度更新已在TrainingSessionManager中处理
        }
    }

    /**
     * 更新训练进度（公开方法，供外部调用）
     * 
     * @param progress 训练进度
     */
    public void updateTrainingProgress(TrainingProgress progress) {
        if (progress != null) {
            trainingProgress.postValue(progress);
        }
    }

    /**
     * 设置目标参数
     * 
     * @param targetDuration   目标持续时间（分钟）
     * @param targetDistance   目标距离（米）
     * @param targetStrokeRate 目标桨频（次/分钟）
     */
    public void setTargets(int targetDuration, double targetDistance, int targetStrokeRate) {
        if (trainingSessionManager != null) {
            trainingSessionManager.setTargetDuration(targetDuration);
            trainingSessionManager.setTargetDistance(targetDistance);
            trainingSessionManager.setTargetStrokeRate(targetStrokeRate);
        }
    }

    /**
     * 设置采样率
     * 
     * @param rate 采样率（Hz）
     */
    public void setSamplingRate(int rate) {
        this.samplingRate = rate;
        if (sensorDataProcessor != null) {
            sensorDataProcessor.setSamplingRate(rate);
        }
    }

    /**
     * 设置自动记录
     * 
     * @param autoRecord 是否自动记录
     */
    public void setAutoRecord(boolean autoRecord) {
        this.autoRecord = autoRecord;
        if (trainingSessionManager != null) {
            trainingSessionManager.setAutoRecord(autoRecord);
        }
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        stopSensorProcessing();
        if (trainingSessionManager != null && trainingSessionManager.isSessionActive()) {
            trainingSessionManager.cancelSession();
        }
    }

    // LiveData获取方法

    public LiveData<SensorData> getSensorData() {
        return sensorData;
    }

    public LiveData<ProcessedData> getProcessedData() {
        return processedData;
    }

    public LiveData<TrainingProgress> getTrainingProgress() {
        return trainingProgress;
    }

    public LiveData<RowingState> getRowingState() {
        return rowingState;
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSessionId() {
        return sessionId;
    }

    public LiveData<TrainingSessionData> getSessionData() {
        return sessionData;
    }

    public LiveData<TrainingStatistics> getSessionStatistics() {
        return sessionStatistics;
    }

    // 直接获取当前值的方法

    public SensorData getCurrentSensorData() {
        return sensorData.getValue();
    }

    public ProcessedData getCurrentProcessedData() {
        return processedData.getValue();
    }

    public TrainingProgress getCurrentTrainingProgress() {
        return trainingProgress.getValue();
    }

    public RowingState getCurrentRowingState() {
        return rowingState.getValue();
    }

    public boolean getCurrentIsRecording() {
        return isRecording.getValue() != null ? isRecording.getValue() : false;
    }

    public String getCurrentSessionId() {
        return sessionId.getValue();
    }

    public TrainingSessionData getCurrentSessionData() {
        return sessionData.getValue();
    }

    public TrainingStatistics getCurrentSessionStatistics() {
        return sessionStatistics.getValue();
    }

    // 获取处理器的方法

    public SensorDataProcessor getSensorDataProcessor() {
        return sensorDataProcessor;
    }

    public TrainingSessionManager getTrainingSessionManager() {
        return trainingSessionManager;
    }
}