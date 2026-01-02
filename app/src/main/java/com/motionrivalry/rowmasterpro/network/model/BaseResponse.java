package com.motionrivalry.rowmasterpro.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * 基础响应模型
 * 所有网络响应的统一包装
 */
public class BaseResponse<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("success")
    private boolean success;

    public BaseResponse() {
    }

    public BaseResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 检查响应是否成功
     */
    public boolean isSuccessful() {
        return success && code == 200;
    }

    /**
     * 获取错误信息
     */
    public String getErrorMessage() {
        if (isSuccessful()) {
            return null;
        }
        return message != null ? message : "Unknown error";
    }
}