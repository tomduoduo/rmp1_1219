package com.motionrivalry.rowmasterpro.sensor;

/**
 * 加速度计数据处理器
 * 处理加速度计数据的滤波、校正和特征提取
 */
public class AccelerometerDataProcessor {
    
    private static final String TAG = "AccelerometerDataProcessor";
    
    // 重力加速度
    private static final float GRAVITY = 9.81f;
    
    // 数据缓存
    private float[] gravityVector;
    private float[] linearAcceleration;
    
    // 校准参数
    private float[] biasOffset;
    private boolean isCalibrated;
    
    // 统计信息
    private double totalAcceleration;
    private int sampleCount;
    
    /**
     * 构造函数
     */
    public AccelerometerDataProcessor() {
        this.gravityVector = new float[3];
        this.linearAcceleration = new float[3];
        this.biasOffset = new float[3];
        this.isCalibrated = false;
        
        this.totalAcceleration = 0;
        this.sampleCount = 0;
    }
    
    /**
     * 处理加速度数据
     * 
     * @param rawData 原始加速度数据
     * @param sensorData 传感器数据对象
     */
    public void processData(float[] rawData, SensorData sensorData) {
        if (rawData == null || rawData.length < 3) {
            return;
        }
        
        // 应用偏差校正
        float[] correctedData = applyBiasCorrection(rawData);
        
        // 分离重力和线性加速度
        separateGravityAndLinearAcceleration(correctedData);
        
        // 更新传感器数据
        updateSensorData(sensorData);
        
        // 更新统计信息
        updateStatistics(correctedData);
    }
    
    /**
     * 应用偏差校正
     * 
     * @param rawData 原始数据
     * @return 校正后的数据
     */
    private float[] applyBiasCorrection(float[] rawData) {
        float[] corrected = new float[3];
        
        if (isCalibrated) {
            for (int i = 0; i < 3; i++) {
                corrected[i] = rawData[i] - biasOffset[i];
            }
        } else {
            System.arraycopy(rawData, 0, corrected, 0, 3);
        }
        
        return corrected;
    }
    
    /**
     * 分离重力和线性加速度
     * 
     * @param correctedData 校正后的数据
     */
    private void separateGravityAndLinearAcceleration(float[] correctedData) {
        // 简化的重力分离算法
        // 使用低通滤波器估计重力方向
        
        final float alpha = 0.8f; // 低通滤波器系数
        
        // 分离重力分量
        gravityVector[0] = alpha * gravityVector[0] + (1 - alpha) * correctedData[0];
        gravityVector[1] = alpha * gravityVector[1] + (1 - alpha) * correctedData[1];
        gravityVector[2] = alpha * gravityVector[2] + (1 - alpha) * correctedData[2];
        
        // 计算线性加速度（去除重力）
        linearAcceleration[0] = correctedData[0] - gravityVector[0];
        linearAcceleration[1] = correctedData[1] - gravityVector[1];
        linearAcceleration[2] = correctedData[2] - gravityVector[2];
    }
    
    /**
     * 更新传感器数据
     * 
     * @param sensorData 传感器数据对象
     */
    private void updateSensorData(SensorData sensorData) {
        // 更新重力向量
        sensorData.gravityVector = gravityVector.clone();
        
        // 更新线性加速度
        sensorData.linearAcceleration = linearAcceleration.clone();
        
        // 计算总加速度
        float totalAccel = (float) Math.sqrt(
            linearAcceleration[0] * linearAcceleration[0] +
            linearAcceleration[1] * linearAcceleration[1] +
            linearAcceleration[2] * linearAcceleration[2]
        );
        
        sensorData.totalLinearAcceleration = totalAccel;
    }
    
    /**
     * 更新统计信息
     * 
     * @param correctedData 校正后的数据
     */
    private void updateStatistics(float[] correctedData) {
        // 计算总加速度
        double totalAccel = Math.sqrt(
            correctedData[0] * correctedData[0] +
            correctedData[1] * correctedData[1] +
            correctedData[2] * correctedData[2]
        );
        
        totalAcceleration += totalAccel;
        sampleCount++;
    }
    
    /**
     * 校准加速度计
     * 
     * @param calibrationData 校准数据
     */
    public void calibrate(float[][] calibrationData) {
        if (calibrationData == null || calibrationData.length == 0) {
            return;
        }
        
        // 计算平均偏差
        float[] average = new float[3];
        for (float[] data : calibrationData) {
            if (data != null && data.length >= 3) {
                for (int i = 0; i < 3; i++) {
                    average[i] += data[i];
                }
            }
        }
        
        for (int i = 0; i < 3; i++) {
            average[i] /= calibrationData.length;
        }
        
        // 假设设备静止时应该只有重力在Z轴
        biasOffset[0] = average[0];
        biasOffset[1] = average[1];
        biasOffset[2] = average[2] - GRAVITY;
        
        isCalibrated = true;
    }
    
    /**
     * 获取重力向量
     * 
     * @return 重力向量
     */
    public float[] getGravityVector() {
        return gravityVector.clone();
    }
    
    /**
     * 获取线性加速度
     * 
     * @return 线性加速度
     */
    public float[] getLinearAcceleration() {
        return linearAcceleration.clone();
    }
    
    /**
     * 获取平均加速度
     * 
     * @return 平均加速度
     */
    public double getAverageAcceleration() {
        return sampleCount > 0 ? totalAcceleration / sampleCount : 0;
    }
    
    /**
     * 是否已校准
     * 
     * @return true表示已校准
     */
    public boolean isCalibrated() {
        return isCalibrated;
    }
    
    /**
     * 重置处理器
     */
    public void reset() {
        for (int i = 0; i < 3; i++) {
            gravityVector[i] = 0;
            linearAcceleration[i] = 0;
            biasOffset[i] = 0;
        }
        
        totalAcceleration = 0;
        sampleCount = 0;
        isCalibrated = false;
    }
    
    /**
     * 检测运动状态
     * 
     * @param linearAcceleration 线性加速度
     * @return 运动状态
     */
    public static MotionState detectMotionState(float[] linearAcceleration) {
        if (linearAcceleration == null || linearAcceleration.length < 3) {
            return MotionState.STATIONARY;
        }
        
        // 计算总线性加速度
        float totalLinearAccel = (float) Math.sqrt(
            linearAcceleration[0] * linearAcceleration[0] +
            linearAcceleration[1] * linearAcceleration[1] +
            linearAcceleration[2] * linearAcceleration[2]
        );
        
        // 根据加速度大小判断运动状态
        if (totalLinearAccel < 0.1f) {
            return MotionState.STATIONARY;
        } else if (totalLinearAccel < 0.5f) {
            return MotionState.WALKING;
        } else if (totalLinearAccel < 2.0f) {
            return MotionState.RUNNING;
        } else {
            return MotionState.VIGOROUS;
        }
    }
    
    /**
     * 运动状态枚举
     */
    public enum MotionState {
        STATIONARY,  // 静止
        WALKING,     // 步行
        RUNNING,     // 跑步
        VIGOROUS     // 剧烈运动
    }
    
    @Override
    public String toString() {
        return String.format("AccelerometerDataProcessor{calibrated=%s, avgAccel=%.2f}",
                isCalibrated, getAverageAcceleration());
    }
}