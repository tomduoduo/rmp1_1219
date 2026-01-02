package com.motionrivalry.rowmasterpro.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用户数据模型
 * 用于用户相关的网络请求和响应
 */
public class UserData {

    @SerializedName("userId")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("loginTime")
    private long loginTime;

    @SerializedName("token")
    private String token;

    @SerializedName("expiresIn")
    private long expiresIn;

    public UserData() {
    }

    public UserData(String userId, String username, String email, String deviceId,
            String deviceType, long loginTime, String token, long expiresIn) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.loginTime = loginTime;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired() {
        if (token == null || expiresIn <= 0) {
            return true;
        }
        return System.currentTimeMillis() > (loginTime + expiresIn * 1000);
    }

    /**
     * 获取剩余有效时间（秒）
     */
    public long getRemainingTime() {
        if (isTokenExpired()) {
            return 0;
        }
        return (loginTime + expiresIn * 1000 - System.currentTimeMillis()) / 1000;
    }

    /**
     * 更新登录时间
     */
    public void updateLoginTime() {
        this.loginTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", loginTime=" + loginTime +
                ", expiresIn=" + expiresIn +
                '}';
    }
}