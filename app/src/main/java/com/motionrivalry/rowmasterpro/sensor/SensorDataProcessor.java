package com.motionrivalry.rowmasterpro.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.motionrivalry.rowmasterpro.utils.TimerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 传感器数据处理器
 * 负责处理所有传感器数据的接收、解析、滤波和计算
 * 提供统一的接口处理加速度计、磁力计、GPS和心率数据
 */
public class SensorDataProcessor {
    
    private static final String TAG = "SensorDataProcessor";
    
    // 上下文
    private Context context;
    
    // 传感器管理器
    private SensorManager sensorManager;
    
    // 数据缓存
    private SensorDataCache dataCache;
    
    // 数据滤波器
    private DataSmoother accelerometerSmoother;
    private DataSmoother strokeRateSmoother;
    private DataSmoother boatSpeedSmoother;
    private DataSmoother boatYawSmoother;
    
    // 状态检测器
    private StrokeDetector strokeDetector;
    private RowingStateMachine stateMachine;
    
    // 计算器
    private OrientationCalculator orientationCalculator;
    private AccelerometerDataProcessor accelerometerProcessor;
    private PolarDataProcessor polarDataProcessor;
    
    // 当前数据
    private SensorData currentData;
    private ProcessedData processedData;
    
    // 回调接口
    private SensorDataCallback callback;
    
    // 配置参数
    private int samplingRate = 32; // Hz
    private int dataWindowSize = 64; // 数据窗口大小
    private double strokeDetectionThreshold = 0.5; // 桨频检测阈值
    
    // 状态
    private boolean isProcessing;
    private long processingStartTime;
    
    /**
     * 传感器数据回调接口
     */
    public interface SensorDataCallback {
        void onSensorDataUpdated(SensorData data);
        void onStrokeDetected(double strokeRate, double strokeCount);
        void onStateChanged(RowingState newState);
        void onHeartRateDataReceived(String deviceId, int heartRate);
        void onLocationDataUpdated(Location location, double speed);
        void onError(String error);
    }
    
    /**
     * 构造函数
     * 
     * @param context 上下文
     */
    public SensorDataProcessor(Context context) {
        this.context = context;
        this.isProcessing = false;
        this.processingStartTime = 0;
        
        initializeComponents();
    }
    
    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 获取传感器管理器
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        
        // 初始化数据缓存
        dataCache = new SensorDataCache(dataWindowSize);
        
        // 初始化滤波器
        accelerometerSmoother = new DataSmoother(8); // 8点移动平均
        strokeRateSmoother = new DataSmoother(16); // 16点移动平均
        boatSpeedSmoother = new DataSmoother(32); // 32点移动平均
        boatYawSmoother = new DataSmoother(16); // 16点移动平均
        
        // 初始化状态检测器
        strokeDetector = new StrokeDetector(strokeDetectionThreshold);
        stateMachine = new RowingStateMachine();
        
        // 初始化计算器
        orientationCalculator = new OrientationCalculator();
        accelerometerProcessor = new AccelerometerDataProcessor();
        polarDataProcessor = new PolarDataProcessor();
        
        // 初始化当前数据
        currentData = new SensorData();
        processedData = new ProcessedData();
    }
    
    /**
     * 开始数据处理
     */
    public void startProcessing() {
        if (isProcessing) {
            return;
        }
        
        isProcessing = true;
        processingStartTime = System.currentTimeMillis();
        
        // 注册传感器监听器
        registerSensors();
        
        // 启动数据更新定时器
        startDataUpdateTimer();
        
        Log.d(TAG, "传感器数据处理已启动");
    }
    
    /**
     * 停止数据处理
     */
    public void stopProcessing() {
        if (!isProcessing) {
            return;
        }
        
        isProcessing = false;
        
        // 注销传感器监听器
        unregisterSensors();
        
        // 停止数据更新定时器
        stopDataUpdateTimer();
        
        Log.d(TAG, "传感器数据处理已停止");
    }
    
    /**
     * 处理传感器事件
     * 
     * @param event 传感器事件
     */
    public void processSensorEvent(SensorEvent event) {
        if (!isProcessing) {
            return;
        }
        
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                processAccelerometerData(event);
                break;
                
            case Sensor.TYPE_MAGNETIC_FIELD:
                processMagnetometerData(event);
                break;
                
            case Sensor.TYPE_LINEAR_ACCELERATION:
                processLinearAccelerationData(event);
                break;
                
            case Sensor.TYPE_ROTATION_VECTOR:
                processRotationVectorData(event);
                break;
        }
    }
    
    /**
     * 处理位置更新
     * 
     * @param location 位置信息
     */
    public void processLocationUpdate(Location location) {
        if (!isProcessing || location == null) {
            return;
        }
        
        // 更新GPS数据
        currentData.latitude = location.getLatitude();
        currentData.longitude = location.getLongitude();
        currentData.altitude = location.getAltitude();
        currentData.gpsAccuracy = location.getAccuracy();
        currentData.gpsTime = location.getTime();
        
        // 计算船速（平滑处理）
        double rawSpeed = location.getSpeed(); // m/s
        double smoothedSpeed = boatSpeedSmoother.smooth(rawSpeed);
        currentData.boatSpeed = smoothedSpeed * 3.6; // 转换为km/h
        currentData.boatSpeedRaw = rawSpeed * 3.6;
        
        // 计算GPS信号质量
        currentData.gpsSignalQuality = calculateGpsSignalQuality(location);
        
        // 通知回调
        if (callback != null) {
            callback.onLocationDataUpdated(location, currentData.boatSpeed);
        }
    }
    
    /**
     * 处理心率数据
     * 
     * @param deviceId 设备ID
     * @param heartRate 心率值
     */
    public void processHeartRateData(String deviceId, int heartRate) {
        if (!isProcessing) {
            return;
        }
        
        // 更新心率数据
        polarDataProcessor.updateHeartRate(deviceId, heartRate);
        currentData.heartRate = heartRate;
        currentData.heartRateDeviceId = deviceId;
        currentData.heartRateTime = System.currentTimeMillis();
        
        // 通知回调
        if (callback != null) {
            callback.onHeartRateDataReceived(deviceId, heartRate);
        }
    }
    
    /**
     * 处理加速度计数据
     * 
     * @param event 传感器事件
     */
    private void processAccelerometerData(SensorEvent event) {
        // 获取原始数据
        float[] rawData = event.values.clone();
        
        // 数据滤波
        float[] smoothedData = accelerometerSmoother.smooth(rawData);
        
        // 更新当前数据
        currentData.accelerometerRaw = rawData;
        currentData.accelerometerSmoothed = smoothedData;
        currentData.accelerometerTime = event.timestamp;
        
        // 处理加速度数据
        accelerometerProcessor.processData(smoothedData, currentData);
        
        // 检测桨频
        detectStrokeFromAcceleration(smoothedData);
    }
    
    /**
     * 处理磁力计数据
     * 
     * @param event 传感器事件
     */
    private void processMagnetometerData(SensorEvent event) {
        currentData.magnetometer = event.values.clone();
        currentData.magnetometerTime = event.timestamp;
        
        // 计算方向
        updateOrientation();
    }
    
    /**
     * 处理线性加速度数据
     * 
     * @param event 传感器事件
     */
    private void processLinearAccelerationData(SensorEvent event) {
        currentData.linearAcceleration = event.values.clone();
        currentData.linearAccelerationTime = event.timestamp;
    }
    
    /**
     * 处理旋转矢量数据
     * 
     * @param event 传感器事件
     */
    private void processRotationVectorData(SensorEvent event) {
        currentData.rotationVector = event.values.clone();
        currentData.rotationVectorTime = event.timestamp;
        
        // 更新方向
        updateOrientation();
    }
    
    /**
     * 更新方向信息
     */
    private void updateOrientation() {
        if (currentData.accelerometerSmoothed == null || currentData.magnetometer == null) {
            return;
        }
        
        // 计算方向角
        float[] orientation = orientationCalculator.calculateOrientation(
                currentData.accelerometerSmoothed, 
                currentData.magnetometer);
        
        // 平滑处理
        float smoothedYaw = boatYawSmoother.smooth(orientation[0]);
        
        currentData.boatYaw = smoothedYaw;
        currentData.boatRoll = orientation[1];
        currentData.boatPitch = orientation[2];
        currentData.orientationTime = System.currentTimeMillis();
    }
    
    /**
     * 从加速度检测桨频
     * 
     * @param acceleration 加速度数据
     */
    private void detectStrokeFromAcceleration(float[] acceleration) {
        // 使用Y轴加速度检测桨频
        float accelY = acceleration[1];
        
        // 检测桨频
        StrokeDetector.StrokeResult result = strokeDetector.detectStroke(accelY);
        
        if (result.isStrokeDetected) {
            // 更新桨频数据
            currentData.strokeRate = result.strokeRate;
            currentData.strokeCount = result.strokeCount;
            currentData.lastStrokeTime = result.strokeTime;
            
            // 平滑处理桨频
            double smoothedStrokeRate = strokeRateSmoother.smooth(result.strokeRate);
            currentData.strokeRateSmoothed = smoothedStrokeRate;
            
            // 更新划船状态
            stateMachine.processStrokeDetection(result.strokeRate, result.strokeCount);
            
            // 通知回调
            if (callback != null) {
                callback.onStrokeDetected(smoothedStrokeRate, result.strokeCount);
            }
        }
        
        // 更新状态机
        stateMachine.processAccelerationData(acceleration);
    }
    
    /**
     * 计算GPS信号质量
     * 
     * @param location 位置信息
     * @return 信号质量（0-100）
     */
    private int calculateGpsSignalQuality(Location location) {
        float accuracy = location.getAccuracy();
        
        if (accuracy <= 5) return 100;      // 极好
        if (accuracy <= 10) return 80;     // 很好
        if (accuracy <= 20) return 60;     // 好
        if (accuracy <= 50) return 40;     // 一般
        if (accuracy <= 100) return 20;     // 差
        return 0;                            // 很差
    }
    
    /**
     * 注册传感器监听器
     */
    private void registerSensors() {
        // 注册加速度计
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, 
                    SensorManager.SENSOR_DELAY_GAME);
        }
        
        // 注册磁力计
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer != null) {
            sensorManager.registerListener(sensorListener, magnetometer, 
                    SensorManager.SENSOR_DELAY_GAME);
        }
        
        // 注册线性加速度
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (linearAcceleration != null) {
            sensorManager.registerListener(sensorListener, linearAcceleration, 
                    SensorManager.SENSOR_DELAY_GAME);
        }
        
        // 注册旋转矢量
        Sensor rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationVector != null) {
            sensorManager.registerListener(sensorListener, rotationVector, 
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
    /**
     * 注销传感器监听器
     */
    private void unregisterSensors() {
        sensorManager.unregisterListener(sensorListener);
    }
    
    /**
     * 启动数据更新定时器
     */
    private void startDataUpdateTimer() {
        TimerManager.getInstance().scheduleAtFixedRate("sensorDataUpdate", new Runnable() {
            @Override
            public void run() {
                updateSensorData();
            }
        }, 0, 1000 / samplingRate); // 根据采样率更新
    }
    
    /**
     * 停止数据更新定时器
     */
    private void stopDataUpdateTimer() {
        TimerManager.getInstance().cancelTask("sensorDataUpdate");
    }
    
    /**
     * 更新传感器数据
     */
    private void updateSensorData() {
        // 复制当前数据到处理数据
        processedData.copyFrom(currentData);
        
        // 更新处理时间
        processedData.processingTime = System.currentTimeMillis();
        
        // 通知回调
        if (callback != null) {
            callback.onSensorDataUpdated(currentData);
        }
    }
    
    /**
     * 设置数据回调
     * 
     * @param callback 回调接口
     */
    public void setCallback(SensorDataCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 获取当前传感器数据
     * 
     * @return 当前数据
     */
    public SensorData getCurrentData() {
        return new SensorData(currentData);
    }
    
    /**
     * 获取处理后的数据
     * 
     * @return 处理后的数据
     */
    public ProcessedData getProcessedData() {
        return new ProcessedData(processedData);
    }
    
    /**
     * 重置所有数据
     */
    public void reset() {
        currentData.reset();
        processedData.reset();
        dataCache.clear();
        
        accelerometerSmoother.reset();
        strokeRateSmoother.reset();
        boatSpeedSmoother.reset();
        boatYawSmoother.reset();
        
        strokeDetector.reset();
        stateMachine.reset();
        
        polarDataProcessor.reset();
    }
    
    /**
     * 设置采样率
     * 
     * @param rate 采样率（Hz）
     */
    public void setSamplingRate(int rate) {
        this.samplingRate = rate;
    }
    
    /**
     * 获取采样率
     * 
     * @return 采样率
     */
    public int getSamplingRate() {
        return samplingRate;
    }
    
    /**
     * 是否正在处理数据
     * 
     * @return true表示正在处理
     */
    public boolean isProcessing() {
        return isProcessing;
    }
    
    /**
     * 获取处理开始时间
     * 
     * @return 开始时间戳
     */
    public long getProcessingStartTime() {
        return processingStartTime;
    }
    
    /**
     * 传感器监听器
     */
    private final android.hardware.SensorEventListener sensorListener = 
            new android.hardware.SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            processSensorEvent(event);
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 精度变化处理
            Log.d(TAG, "传感器精度变化: " + sensor.getName() + " = " + accuracy);
        }
    };
}