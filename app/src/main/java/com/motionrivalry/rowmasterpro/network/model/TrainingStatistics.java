package com.motionrivalry.rowmasterpro.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 训练统计信息模型类
 * 用于存储和传输训练统计数据
 */
public class TrainingStatistics {

    @SerializedName("userId")
    private String userId;

    @SerializedName("timeRange")
    private String timeRange; // 时间范围（day/week/month/year）

    @SerializedName("startDate")
    private String startDate; // 开始日期

    @SerializedName("endDate")
    private String endDate; // 结束日期

    @SerializedName("totalTrainings")
    private int totalTrainings; // 总训练次数

    @SerializedName("totalDuration")
    private int totalDuration; // 总训练时长（秒）

    @SerializedName("totalDistance")
    private double totalDistance; // 总训练距离（米）

    @SerializedName("totalCalories")
    private int totalCalories; // 总消耗卡路里

    @SerializedName("averageStrokeRate")
    private double averageStrokeRate; // 平均桨频

    @SerializedName("averageSpeed")
    private double averageSpeed; // 平均速度（米/秒）

    @SerializedName("maxSpeed")
    private double maxSpeed; // 最大速度（米/秒）

    @SerializedName("totalStrokeCount")
    private int totalStrokeCount; // 总划桨次数

    @SerializedName("longestTraining")
    private int longestTraining; // 最长训练时长（秒）

    @SerializedName("longestDistance")
    private double longestDistance; // 最长训练距离（米）

    @SerializedName("bestSpeed")
    private double bestSpeed; // 最佳速度（米/秒）

    @SerializedName("trainingDays")
    private int trainingDays; // 训练天数

    @SerializedName("weeklyData")
    private List<WeeklyData> weeklyData; // 每周数据

    @SerializedName("monthlyData")
    private List<MonthlyData> monthlyData; // 每月数据

    @SerializedName("generatedAt")
    private long generatedAt; // 生成时间戳

    public TrainingStatistics() {
        this.generatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTotalTrainings() {
        return totalTrainings;
    }

    public void setTotalTrainings(int totalTrainings) {
        this.totalTrainings = totalTrainings;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getAverageStrokeRate() {
        return averageStrokeRate;
    }

    public void setAverageStrokeRate(double averageStrokeRate) {
        this.averageStrokeRate = averageStrokeRate;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getTotalStrokeCount() {
        return totalStrokeCount;
    }

    public void setTotalStrokeCount(int totalStrokeCount) {
        this.totalStrokeCount = totalStrokeCount;
    }

    public int getLongestTraining() {
        return longestTraining;
    }

    public void setLongestTraining(int longestTraining) {
        this.longestTraining = longestTraining;
    }

    public double getLongestDistance() {
        return longestDistance;
    }

    public void setLongestDistance(double longestDistance) {
        this.longestDistance = longestDistance;
    }

    public double getBestSpeed() {
        return bestSpeed;
    }

    public void setBestSpeed(double bestSpeed) {
        this.bestSpeed = bestSpeed;
    }

    public int getTrainingDays() {
        return trainingDays;
    }

    public void setTrainingDays(int trainingDays) {
        this.trainingDays = trainingDays;
    }

    public List<WeeklyData> getWeeklyData() {
        return weeklyData;
    }

    public void setWeeklyData(List<WeeklyData> weeklyData) {
        this.weeklyData = weeklyData;
    }

    public List<MonthlyData> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyData> monthlyData) {
        this.monthlyData = monthlyData;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * 获取总训练时长描述
     * 
     * @return 格式化的时长字符串
     */
    public String getTotalDurationDescription() {
        int hours = totalDuration / 3600;
        int minutes = (totalDuration % 3600) / 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else {
            return String.format("%d分钟", minutes);
        }
    }

    /**
     * 获取总距离描述
     * 
     * @return 格式化的距离字符串
     */
    public String getTotalDistanceDescription() {
        if (totalDistance >= 1000) {
            return String.format("%.2f公里", totalDistance / 1000);
        } else {
            return String.format("%.0f米", totalDistance);
        }
    }

    /**
     * 获取平均速度描述
     * 
     * @return 格式化的速度字符串
     */
    public String getAverageSpeedDescription() {
        double speedKmh = averageSpeed * 3.6; // 转换为公里/小时
        return String.format("%.1f公里/小时", speedKmh);
    }

    /**
     * 获取最佳速度描述
     * 
     * @return 格式化的速度字符串
     */
    public String getBestSpeedDescription() {
        double speedKmh = bestSpeed * 3.6; // 转换为公里/小时
        return String.format("%.1f公里/小时", speedKmh);
    }

    /**
     * 获取平均桨频描述
     * 
     * @return 格式化的桨频字符串
     */
    public String getAverageStrokeRateDescription() {
        return String.format("%.1f次/分钟", averageStrokeRate);
    }

    /**
     * 获取最长训练时长描述
     * 
     * @return 格式化的时长字符串
     */
    public String getLongestTrainingDescription() {
        int hours = longestTraining / 3600;
        int minutes = (longestTraining % 3600) / 60;
        int seconds = longestTraining % 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    /**
     * 获取最长距离描述
     * 
     * @return 格式化的距离字符串
     */
    public String getLongestDistanceDescription() {
        if (longestDistance >= 1000) {
            return String.format("%.2f公里", longestDistance / 1000);
        } else {
            return String.format("%.0f米", longestDistance);
        }
    }

    @Override
    public String toString() {
        return "TrainingStatistics{" +
                "userId='" + userId + '\'' +
                ", timeRange='" + timeRange + '\'' +
                ", totalTrainings=" + totalTrainings +
                ", totalDuration=" + totalDuration +
                ", totalDistance=" + totalDistance +
                ", totalCalories=" + totalCalories +
                ", averageStrokeRate=" + averageStrokeRate +
                ", averageSpeed=" + averageSpeed +
                ", maxSpeed=" + maxSpeed +
                ", trainingDays=" + trainingDays +
                '}';
    }

    /**
     * 每周数据内部类
     */
    public static class WeeklyData {
        @SerializedName("week")
        private int week;

        @SerializedName("trainings")
        private int trainings;

        @SerializedName("duration")
        private int duration;

        @SerializedName("distance")
        private double distance;

        @SerializedName("calories")
        private int calories;

        // Getters and Setters
        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public int getTrainings() {
            return trainings;
        }

        public void setTrainings(int trainings) {
            this.trainings = trainings;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }
    }

    /**
     * 每月数据内部类
     */
    public static class MonthlyData {
        @SerializedName("month")
        private int month;

        @SerializedName("trainings")
        private int trainings;

        @SerializedName("duration")
        private int duration;

        @SerializedName("distance")
        private double distance;

        @SerializedName("calories")
        private int calories;

        // Getters and Setters
        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getTrainings() {
            return trainings;
        }

        public void setTrainings(int trainings) {
            this.trainings = trainings;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }
    }
}