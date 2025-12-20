package com.motionrivalry.rowmasterpro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 传感器数据处理器
 * 功能：处理加速度计和磁场传感器数据，计算船只姿态和修正加速度
 */
public class SensorProcessor implements SensorEventListener {

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] accelerometerLinearValues = new float[3];

    // 回调接口
    public interface SensorDataListener {
        void onSensorDataUpdated(SensorData data);
    }

    private SensorDataListener listener;

    /**
     * 构造函数
     */
    public SensorProcessor() {
        // 初始化传感器数据数组
        accelerometerValues = new float[3];
        magneticFieldValues = new float[3];
        accelerometerLinearValues = new float[3];
    }

    /**
     * 设置传感器数据监听器
     * @param listener 监听器实例
     */
    public void setListener(SensorDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 处理传感器事件
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, 3);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticFieldValues, 0, 3);
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            System.arraycopy(event.values, 0, accelerometerLinearValues, 0, 3);
        }

        // 计算并通知
        calculateAndNotify();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 处理精度变化，当前不需要特殊处理
    }

    /**
     * 计算船只姿态和修正加速度
     */
    private void calculateAndNotify() {
        float[] values = new float[3];
        float[] R = new float[9];
        
        // 计算旋转矩阵和方向
        if (SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)) {
            SensorManager.getOrientation(R, values);

            // 转换为角度
            float roll = (float) Math.toDegrees(values[1]);
            float yaw = (float) Math.toDegrees(values[0]);
            float pitch = (float) Math.toDegrees(values[2]);

            // 计算修正加速度
            double boatAcceleration = calculateTiltCompensatedAcceleration(values);

            // 计算偏航角
            float boatYawAngle = calculateBoatYawAngle(boatAcceleration);

            // 通知监听器
            if (listener != null) {
                SensorData data = new SensorData(roll, yaw, pitch, boatAcceleration, boatYawAngle);
                listener.onSensorDataUpdated(data);
            }
        }
    }

    /**
     * 计算倾斜补偿加速度
     * @param orientationValues 方向值数组
     * @return 修正后的加速度值
     */
    private double calculateTiltCompensatedAcceleration(float[] orientationValues) {
        float xAcclLinear = accelerometerLinearValues[0];
        float yAcclLinear = accelerometerLinearValues[1];
        float zAcclLinear = accelerometerLinearValues[2];

        double tiltAngleX = orientationValues[2] * Math.PI / 180;
        double tiltCosX = Math.cos(tiltAngleX);
        double tiltAngleZ = (90 - Math.abs(orientationValues[2])) * Math.PI / 180;
        double tiltCosZ = Math.cos(tiltAngleZ);

        double xAcclActual = -xAcclLinear * tiltCosX;
        double zAcclActual = zAcclLinear * tiltCosZ;

        return xAcclActual + zAcclActual;
    }

    /**
     * 计算船只偏航角
     * @param boatAcceleration 船只加速度
     * @return 偏航角度
     */
    private float calculateBoatYawAngle(double boatAcceleration) {
        float yAcclLinear = accelerometerLinearValues[1];
        
        if (Math.abs(boatAcceleration) < 0.001) {
            return 0.0f;
        }
        
        double boatYawTan = yAcclLinear / boatAcceleration;
        return (float) (Math.atan(boatYawTan) * 180 / Math.PI);
    }

    // ========== 测试辅助方法 ==========
    /**
     * 检查传感器是否已注册（测试用）
     * @return 传感器注册状态
     */
    public boolean areSensorsRegistered() { return listener != null; }
    
    /**
     * 获取加速度计数值（测试用）
     * @return 加速度计数值数组的副本
     */
    public float[] getAccelerometerValues() { return accelerometerValues.clone(); }
    
    /**
     * 获取磁场传感器数值（测试用）
     * @return 磁场传感器数值数组的副本
     */
    public float[] getMagneticFieldValues() { return magneticFieldValues.clone(); }
    
    /**
     * 获取线性加速度计数值（测试用）
     * @return 线性加速度计数值数组的副本
     */
    public float[] getAccelerometerLinearValues() { return accelerometerLinearValues.clone(); }
    
    /**
     * 获取监听器（测试用）
     * @return 监听器实例
     */
    public SensorDataListener getListener() { return listener; }
    
    /**
     * 触发计算（测试用）
     */
    public void triggerCalculation() { calculateAndNotify(); }

    /**
     * 传感器数据封装类
     */
    public static class SensorData {
        public final float roll;
        public final float yaw;
        public final float pitch;
        public final double boatAcceleration;
        public final float boatYawAngle;

        /**
         * 构造函数
         * @param roll 横滚角
         * @param yaw 偏航角
         * @param pitch 俯仰角
         * @param boatAcceleration 船只加速度
         * @param boatYawAngle 船只偏航角
         */
        public SensorData(float roll, float yaw, float pitch, double boatAcceleration, float boatYawAngle) {
            this.roll = roll;
            this.yaw = yaw;
            this.pitch = pitch;
            this.boatAcceleration = boatAcceleration;
            this.boatYawAngle = boatYawAngle;
        }
    }
}