package com.motionrivalry.rowmasterpro;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DataLogger类用于统一管理CSV文件操作
 * 封装了CSV文件的创建、数据记录和关闭功能
 */
public class DataLogger {
    /** CSV文件表头 */
    private static final String[] HEADERS = {
        "time", "distance", "stroke_rate", "boat_speed",
        "boat_acceleration", "boat_roll", "boat_yaw"
    };
    
    private CSVWriter writer;
    private String filePath;
    private long startTime;
    private boolean isOpen = false;
    
    /**
     * 创建CSV日志文件
     * @param filePath 文件路径
     * @throws IOException 文件创建异常
     */
    public void createLog(String filePath) throws IOException {
        this.filePath = filePath;
        this.startTime = System.currentTimeMillis();
        FileWriter fileWriter = new FileWriter(filePath);
        writer = new CSVWriter(fileWriter);
        writer.writeNext(HEADERS);
        isOpen = true;
    }
    
    /**
     * 记录划船数据到CSV文件
     * @param data 划船数据对象
     */
    public void logData(RowingData data) {
        if (!isOpen || writer == null) return;
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        try {
            writer.writeNext(new String[] {
                String.valueOf(elapsedTime),
                String.valueOf(data.distance),
                data.strokeRate, data.speed,
                String.valueOf(data.acceleration),
                String.valueOf(data.roll),
                String.valueOf(data.yawAngle)
            });
        } catch (Exception e) {
            // 发生异常时写入默认数据
            writer.writeNext(new String[] {
                String.valueOf(elapsedTime), "0", "0", "0", "0", "0", "0"
            });
        }
    }
    
    /**
     * 关闭CSV文件
     * @throws IOException 文件关闭异常
     */
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
            isOpen = false;
        }
    }
    
    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() { return filePath; }
    
    /**
     * 检查文件是否打开
     * @return 文件打开状态
     */
    public boolean isOpen() { return isOpen; }
    
    /**
     * 划船数据封装类
     */
    public static class RowingData {
        public final double distance;
        public final String strokeRate;
        public final String speed;
        public final double acceleration;
        public final float roll;
        public final float yawAngle;
        
        /**
         * 划船数据构造函数
         * @param distance 距离
         * @param strokeRate 划桨频率
         * @param speed 船速
         * @param acceleration 加速度
         * @param roll 滚动角度
         * @param yawAngle 偏航角度
         */
        public RowingData(double distance, String strokeRate, String speed,
                         double acceleration, float roll, float yawAngle) {
            this.distance = distance;
            this.strokeRate = strokeRate;
            this.speed = speed;
            this.acceleration = acceleration;
            this.roll = roll;
            this.yawAngle = yawAngle;
        }
    }
}