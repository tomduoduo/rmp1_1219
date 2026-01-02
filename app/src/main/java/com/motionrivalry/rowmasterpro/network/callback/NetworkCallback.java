package com.motionrivalry.rowmasterpro.network.callback;

/**
 * 网络请求通用回调接口
 * 统一处理网络请求的成功和失败情况
 * 
 * @param <T> 响应数据的类型
 */
public interface NetworkCallback<T> {

    /**
     * 网络请求成功回调
     * 
     * @param data 响应数据
     */
    void onSuccess(T data);

    /**
     * 网络请求失败回调
     * 
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     */
    void onError(int errorCode, String errorMessage);

    /**
     * 网络请求开始（可选）
     * 可用于显示加载状态
     */
    default void onStart() {
        // 默认空实现
    }

    /**
     * 网络请求结束（可选）
     * 可用于隐藏加载状态
     */
    default void onComplete() {
        // 默认空实现
    }
}