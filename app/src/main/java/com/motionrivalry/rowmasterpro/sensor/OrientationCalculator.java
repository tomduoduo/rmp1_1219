package com.motionrivalry.rowmasterpro.sensor;

/**
 * 方向计算器
 * 根据加速度计和磁力计数据计算设备的方向角
 */
public class OrientationCalculator {
    
    private static final String TAG = "OrientationCalculator";
    
    // 旋转矩阵
    private float[] rotationMatrix;
    private float[] inclinationMatrix;
    
    // 方向角
    private float[] orientation;
    
    // 临时变量
    private float[] accelerometerData;
    private float[] magnetometerData;
    
    /**
     * 构造函数
     */
    public OrientationCalculator() {
        this.rotationMatrix = new float[9];
        this.inclinationMatrix = new float[9];
        this.orientation = new float[3];
        
        this.accelerometerData = new float[3];
        this.magnetometerData = new float[3];
    }
    
    /**
     * 计算方向角
     * 
     * @param accelerometer 加速度计数据
     * @param magnetometer 磁力计数据
     * @return 方向角数组 [偏航角, 横滚角, 俯仰角]（度）
     */
    public float[] calculateOrientation(float[] accelerometer, float[] magnetometer) {
        if (accelerometer == null || magnetometer == null || 
            accelerometer.length < 3 || magnetometer.length < 3) {
            return orientation;
        }
        
        // 复制数据
        System.arraycopy(accelerometer, 0, accelerometerData, 0, 3);
        System.arraycopy(magnetometer, 0, magnetometerData, 0, 3);
        
        // 计算旋转矩阵
        boolean success = calculateRotationMatrix();
        if (!success) {
            return orientation;
        }
        
        // 计算方向角
        calculateOrientationAngles();
        
        return orientation;
    }
    
    /**
     * 计算旋转矩阵
     * 
     * @return true表示成功，false表示失败
     */
    private boolean calculateRotationMatrix() {
        // 使用Android的算法计算旋转矩阵
        // 这里实现简化的版本
        
        // 归一化加速度数据
        float[] accel = normalizeVector(accelerometerData);
        
        // 归一化磁力数据
        float[] mag = normalizeVector(magnetometerData);
        
        // 计算重力方向（Z轴）
        float[] gravity = new float[] {accel[0], accel[1], accel[2]};
        
        // 计算磁场方向的东向量（X轴）
        float[] east = crossProduct(mag, gravity);
        east = normalizeVector(east);
        
        // 计算北向量（Y轴）
        float[] north = crossProduct(gravity, east);
        north = normalizeVector(north);
        
        // 构建旋转矩阵
        rotationMatrix[0] = east[0];
        rotationMatrix[1] = north[0];
        rotationMatrix[2] = gravity[0];
        rotationMatrix[3] = east[1];
        rotationMatrix[4] = north[1];
        rotationMatrix[5] = gravity[1];
        rotationMatrix[6] = east[2];
        rotationMatrix[7] = north[2];
        rotationMatrix[8] = gravity[2];
        
        return true;
    }
    
    /**
     * 计算方向角
     */
    private void calculateOrientationAngles() {
        // 从旋转矩阵提取方向角
        // 使用简化的计算
        
        // 偏航角（Azimuth）- 绕Z轴旋转
        orientation[0] = (float) Math.atan2(rotationMatrix[1], rotationMatrix[4]) * 180.0f / (float) Math.PI;
        
        // 俯仰角（Pitch）- 绕X轴旋转
        orientation[1] = (float) Math.asin(-rotationMatrix[7]) * 180.0f / (float) Math.PI;
        
        // 横滚角（Roll）- 绕Y轴旋转
        orientation[2] = (float) Math.atan2(-rotationMatrix[6], rotationMatrix[8]) * 180.0f / (float) Math.PI;
        
        // 确保角度在0-360度范围内
        normalizeAngles();
    }
    
    /**
     * 归一化角度到0-360度
     */
    private void normalizeAngles() {
        for (int i = 0; i < 3; i++) {
            while (orientation[i] < 0) {
                orientation[i] += 360;
            }
            while (orientation[i] >= 360) {
                orientation[i] -= 360;
            }
        }
    }
    
    /**
     * 向量归一化
     * 
     * @param vector 输入向量
     * @return 归一化后的向量
     */
    private float[] normalizeVector(float[] vector) {
        float[] result = new float[3];
        
        float length = (float) Math.sqrt(vector[0] * vector[0] + 
                                        vector[1] * vector[1] + 
                                        vector[2] * vector[2]);
        
        if (length > 0) {
            result[0] = vector[0] / length;
            result[1] = vector[1] / length;
            result[2] = vector[2] / length;
        } else {
            result[0] = 0;
            result[1] = 0;
            result[2] = 0;
        }
        
        return result;
    }
    
    /**
     * 向量叉积
     * 
     * @param a 向量A
     * @param b 向量B
     * @return 叉积结果
     */
    private float[] crossProduct(float[] a, float[] b) {
        float[] result = new float[3];
        
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];
        
        return result;
    }
    
    /**
     * 计算倾斜补偿后的磁力数据
     * 
     * @param magnetometer 磁力计数据
     * @param accelerometer 加速度计数据
     * @return 倾斜补偿后的磁力数据
     */
    public float[] calculateTiltCompensatedMagnetometer(float[] magnetometer, float[] accelerometer) {
        if (magnetometer == null || accelerometer == null) {
            return magnetometer;
        }
        
        // 简化的倾斜补偿计算
        float pitch = (float) Math.atan2(accelerometer[0], Math.sqrt(accelerometer[1] * accelerometer[1] + accelerometer[2] * accelerometer[2]));
        float roll = (float) Math.atan2(accelerometer[1], Math.sqrt(accelerometer[0] * accelerometer[0] + accelerometer[2] * accelerometer[2]));
        
        float[] compensated = new float[3];
        
        // 应用倾斜补偿
        compensated[0] = magnetometer[0] * (float) Math.cos(pitch) + magnetometer[2] * (float) Math.sin(pitch);
        compensated[1] = magnetometer[0] * (float) Math.sin(roll) * (float) Math.sin(pitch) + 
                        magnetometer[1] * (float) Math.cos(roll) - 
                        magnetometer[2] * (float) Math.sin(roll) * (float) Math.cos(pitch);
        compensated[2] = -magnetometer[0] * (float) Math.cos(roll) * (float) Math.sin(pitch) + 
                        magnetometer[1] * (float) Math.sin(roll) + 
                        magnetometer[2] * (float) Math.cos(roll) * (float) Math.cos(pitch);
        
        return compensated;
    }
    
    /**
     * 获取当前方向角
     * 
     * @return 方向角数组
     */
    public float[] getOrientation() {
        return orientation.clone();
    }
    
    /**
     * 获取偏航角（Azimuth）
     * 
     * @return 偏航角（度）
     */
    public float getAzimuth() {
        return orientation[0];
    }
    
    /**
     * 获取俯仰角（Pitch）
     * 
     * @return 俯仰角（度）
     */
    public float getPitch() {
        return orientation[1];
    }
    
    /**
     * 获取横滚角（Roll）
     * 
     * @return 横滚角（度）
     */
    public float getRoll() {
        return orientation[2];
    }
    
    /**
     * 获取旋转矩阵
     * 
     * @return 旋转矩阵
     */
    public float[] getRotationMatrix() {
        return rotationMatrix.clone();
    }
    
    /**
     * 重置计算器
     */
    public void reset() {
        for (int i = 0; i < 9; i++) {
            rotationMatrix[i] = 0;
            inclinationMatrix[i] = 0;
        }
        
        for (int i = 0; i < 3; i++) {
            orientation[i] = 0;
            accelerometerData[i] = 0;
            magnetometerData[i] = 0;
        }
        
        // 设置单位矩阵
        rotationMatrix[0] = 1;
        rotationMatrix[4] = 1;
        rotationMatrix[8] = 1;
    }
    
    /**
     * 将角度转换为方向描述
     * 
     * @param azimuth 偏航角
     * @return 方向描述
     */
    public static String getDirectionDescription(float azimuth) {
        if (azimuth >= 337.5f || azimuth < 22.5f) {
            return "北";
        } else if (azimuth >= 22.5f && azimuth < 67.5f) {
            return "东北";
        } else if (azimuth >= 67.5f && azimuth < 112.5f) {
            return "东";
        } else if (azimuth >= 112.5f && azimuth < 157.5f) {
            return "东南";
        } else if (azimuth >= 157.5f && azimuth < 202.5f) {
            return "南";
        } else if (azimuth >= 202.5f && azimuth < 247.5f) {
            return "西南";
        } else if (azimuth >= 247.5f && azimuth < 292.5f) {
            return "西";
        } else {
            return "西北";
        }
    }
    
    @Override
    public String toString() {
        return String.format("OrientationCalculator{azimuth=%.1f°, pitch=%.1f°, roll=%.1f°}",
                orientation[0], orientation[1], orientation[2]);
    }
}