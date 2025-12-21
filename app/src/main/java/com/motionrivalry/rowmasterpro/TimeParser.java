package com.motionrivalry.rowmasterpro;

/**
 * 时间解析工具类
 * 用于统一处理Chronometer控件的时间解析逻辑
 */
public class TimeParser {
    
    /**
     * 经过时间数据类
     */
    public static class ElapsedTime {
        public final int hour;
        public final int minute;
        public final int second;
        
        /**
         * 构造函数
         * 
         * @param hour 小时数
         * @param minute 分钟数
         * @param second 秒数
         */
        public ElapsedTime(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
        
        /**
         * 获取总秒数
         * 
         * @return 总秒数
         */
        public int getTotalSeconds() {
            return hour * 3600 + minute * 60 + second;
        }
        
        /**
         * 获取总分钟数（带小数）
         * 
         * @return 总分钟数
         */
        public double getTotalMinutes() {
            return getTotalSeconds() / 60.0;
        }
        
        /**
         * 获取显示文本格式
         * 
         * @return 格式化的时间字符串（HH:MM:SS）
         */
        public String getDisplayText() {
            return hour + ":" + minute + ":" + second;
        }
    }
    
    /**
     * 解析Chronometer控件的时间
     * 
     * @param chronometer Chronometer控件实例
     * @return 解析后的时间数据
     */
    public static ElapsedTime parseChronometer(android.widget.Chronometer chronometer) {
        String text = chronometer.getText().toString();
        int hour = 0, minute, second;
        
        // 根据时间字符串长度判断格式（HH:MM:SS 或 MM:SS）
        if (text.length() <= 5) {
            // MM:SS格式
            String[] parts = text.split(":");
            minute = Integer.parseInt(parts[0]);
            second = Integer.parseInt(parts[1]);
        } else {
            // HH:MM:SS格式
            String[] parts = text.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
            second = Integer.parseInt(parts[2]);
        }
        
        return new ElapsedTime(hour, minute, second);
    }
}