package com.motionrivalry.rowmasterpro.sensor;

/**
 * 传感器数据模型
 * 存储所有传感器相关的原始和处理后数据
 */
public class SensorData {
    
    // 时间戳
    public long timestamp;
    public long processingTime;
    
    // 加速度计数据
    public float[] accelerometerRaw;
    public float[] accelerometerSmoothed;
    public long accelerometerTime;
    
    // 磁力计数据
    public float[] magnetometer;
    public long magnetometerTime;
    
    // 线性加速度数据
    public float[] linearAcceleration;
    public long linearAccelerationTime;
    
    // 旋转矢量数据
    public float[] rotationVector;
    public long rotationVectorTime;
    
    // 方向数据（计算得出）
    public float boatYaw;      // 偏航角（度）
    public float boatRoll;     // 横滚角（度）
    public float boatPitch;    // 俯仰角（度）
    public long orientationTime;
    
    // GPS数据
    public double latitude;
    public double longitude;
    public double altitude;
    public float gpsAccuracy;
    public long gpsTime;
    public int gpsSignalQuality; // 0-100
    
    // 船速数据
    public double boatSpeed;       // km/h
    public double boatSpeedRaw;    // km/h
    public double boatSpeedMax;    // km/h
    
    // 桨频数据
    public double strokeRate;        // 次/分钟
    public double strokeRateSmoothed; // 次/分钟
    public int strokeCount;
    public long lastStrokeTime;
    
    // 心率数据
    public int heartRate;
    public String heartRateDeviceId;
    public long heartRateTime;
    
    // 状态
    public RowingState rowingState;
    public boolean isRecording;
    public long recordingStartTime;
    
    // 重力向量（计算得出）
    public float[] gravityVector;
    public float totalLinearAcceleration;
    
    /**
     * 默认构造函数
     */
    public SensorData() {
        this.timestamp = System.currentTimeMillis();
        this.accelerometerRaw = new float[3];
        this.accelerometerSmoothed = new float[3];
        this.magnetometer = new float[3];
        this.linearAcceleration = new float[3];
        this.rotationVector = new float[4];
        this.gravityVector = new float[3];
        this.rowingState = RowingState.IDLE;
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 要复制的数据
     */
    public SensorData(SensorData other) {
        this.timestamp = other.timestamp;
        this.processingTime = other.processingTime;
        
        // 复制数组数据
        if (other.accelerometerRaw != null) {
            this.accelerometerRaw = other.accelerometerRaw.clone();
        }
        if (other.accelerometerSmoothed != null) {
            this.accelerometerSmoothed = other.accelerometerSmoothed.clone();
        }
        if (other.magnetometer != null) {
            this.magnetometer = other.magnetometer.clone();
        }
        if (other.linearAcceleration != null) {
            this.linearAcceleration = other.linearAcceleration.clone();
        }
        if (other.rotationVector != null) {
            this.rotationVector = other.rotationVector.clone();
        }
        if (other.gravityVector != null) {
            this.gravityVector = other.gravityVector.clone();
        }
        
        // 复制基本数据
        this.accelerometerTime = other.accelerometerTime;
        this.magnetometerTime = other.magnetometerTime;
        this.linearAccelerationTime = other.linearAccelerationTime;
        this.rotationVectorTime = other.rotationVectorTime;
        
        this.boatYaw = other.boatYaw;
        this.boatRoll = other.boatRoll;
        this.boatPitch = other.boatPitch;
        this.orientationTime = other.orientationTime;
        
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.altitude = other.altitude;
        this.gpsAccuracy = other.gpsAccuracy;
        this.gpsTime = other.gpsTime;
        this.gpsSignalQuality = other.gpsSignalQuality;
        
        this.boatSpeed = other.boatSpeed;
        this.boatSpeedRaw = other.boatSpeedRaw;
        this.boatSpeedMax = other.boatSpeedMax;
        
        this.strokeRate = other.strokeRate;
        this.strokeRateSmoothed = other.strokeRateSmoothed;
        this.strokeCount = other.strokeCount;
        this.lastStrokeTime = other.lastStrokeTime;
        
        this.heartRate = other.heartRate;
        this.heartRateDeviceId = other.heartRateDeviceId;
        this.heartRateTime = other.heartRateTime;
        
        this.rowingState = other.rowingState;
        this.isRecording = other.isRecording;
        this.recordingStartTime = other.recordingStartTime;
    }
    
    /**
     * 重置所有数据
     */
    public void reset() {
        this.timestamp = System.currentTimeMillis();
        this.processingTime = 0;
        
        // 重置数组
        if (accelerometerRaw != null) {
            for (int i = 0; i < accelerometerRaw.length; i++) {
                accelerometerRaw[i] = 0;
            }
        }
        if (accelerometerSmoothed != null) {
            for (int i = 0; i < accelerometerSmoothed.length; i++) {
                accelerometerSmoothed[i] = 0;
            }
        }
        if (magnetometer != null) {
            for (int i = 0; i < magnetometer.length; i++) {
                magnetometer[i] = 0;
            }
        }
        if (linearAcceleration != null) {
            for (int i = 0; i < linearAcceleration.length; i++) {
                linearAcceleration[i] = 0;
            }
        }
        if (rotationVector != null) {
            for (int i = 0; i < rotationVector.length; i++) {
                rotationVector[i] = 0;
            }
        }
        
        // 重置时间戳
        accelerometerTime = 0;
        magnetometerTime = 0;
        linearAccelerationTime = 0;
        rotationVectorTime = 0;
        orientationTime = 0;
        gpsTime = 0;
        heartRateTime = 0;
        lastStrokeTime = 0;
        recordingStartTime = 0;
        
        // 重置数值
        boatYaw = 0;
        boatRoll = 0;
        boatPitch = 0;
        latitude = 0;
        longitude = 0;
        altitude = 0;
        gpsAccuracy = 0;
        gpsSignalQuality = 0;
        boatSpeed = 0;
        boatSpeedRaw = 0;
        boatSpeedMax = 0;
        strokeRate = 0;
        strokeRateSmoothed = 0;
        strokeCount = 0;
        this.heartRate = 0;
        this.heartRateDeviceId = null;
        
        // 重置重力向量
        if (gravityVector != null) {
            for (int i = 0; i < gravityVector.length; i++) {
                gravityVector[i] = 0;
            }
        }
        this.totalLinearAcceleration = 0;
        
        // 重置状态
        rowingState = RowingState.IDLE;
        isRecording = false;
    }
    
    /**
     * 获取CSV格式的数据行
     * 
     * @return CSV数据数组
     */
    public String[] getCSVDataRow() {
        return new String[] {
            String.valueOf(timestamp),
            String.valueOf(latitude),
            String.valueOf(longitude),
            String.valueOf(boatSpeed),
            String.valueOf(strokeRate),
            String.valueOf(strokeCount),
            String.valueOf(heartRate),
            String.valueOf(boatYaw),
            String.valueOf(boatRoll),
            String.valueOf(boatPitch)
        };
    }
    
    /**
     * 获取CSV表头
     * 
     * @return CSV表头数组
     */
    public static String[] getCSVHeader() {
        return new String[] {
            "Timestamp",
            "Latitude",
            "Longitude",
            "BoatSpeed_kmh",
            "StrokeRate_spm",
            "StrokeCount",
            "HeartRate_bpm",
            "BoatYaw_deg",
            "BoatRoll_deg",
            "BoatPitch_deg"
        };
    }
    
    @Override
    public String toString() {
        return String.format("SensorData{timestamp=%d, boatSpeed=%.2f, strokeRate=%.1f, heartRate=%d, state=%s}",
                timestamp, boatSpeed, strokeRate, heartRate, rowingState);
    }
}