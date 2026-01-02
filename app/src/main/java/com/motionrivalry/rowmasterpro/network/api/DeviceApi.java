package com.motionrivalry.rowmasterpro.network.api;

import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.DeviceInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 设备相关API接口
 * 定义所有设备相关的REST API端点
 */
public interface DeviceApi {

    /**
     * 绑定设备
     * 
     * @param authToken  认证令牌
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @return 设备信息响应
     */
    @POST("api/devices/bind")
    Call<BaseResponse<DeviceInfo>> bindDevice(
            @Header("Authorization") String authToken,
            @Query("deviceId") String deviceId,
            @Query("deviceName") String deviceName,
            @Query("deviceType") String deviceType);

    /**
     * 解绑设备
     * 
     * @param authToken 认证令牌
     * @param deviceId  设备ID
     * @return 解绑结果响应
     */
    @DELETE("api/devices/{deviceId}/unbind")
    Call<BaseResponse<Void>> unbindDevice(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId);

    /**
     * 获取用户绑定的设备列表
     * 
     * @param authToken 认证令牌
     * @return 设备列表响应
     */
    @GET("api/devices")
    Call<BaseResponse<List<DeviceInfo>>> getUserDevices(
            @Header("Authorization") String authToken);

    /**
     * 获取设备详情
     * 
     * @param authToken 认证令牌
     * @param deviceId  设备ID
     * @return 设备信息响应
     */
    @GET("api/devices/{deviceId}")
    Call<BaseResponse<DeviceInfo>> getDeviceInfo(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId);

    /**
     * 更新设备信息
     * 
     * @param authToken  认证令牌
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @return 更新后的设备信息响应
     */
    @PUT("api/devices/{deviceId}")
    Call<BaseResponse<DeviceInfo>> updateDeviceInfo(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId,
            @Query("deviceName") String deviceName,
            @Query("deviceType") String deviceType);

    /**
     * 检查固件更新
     * 
     * @param authToken      认证令牌
     * @param deviceId       设备ID
     * @param currentVersion 当前固件版本
     * @return 固件更新信息响应
     */
    @GET("api/devices/{deviceId}/firmware/check")
    Call<BaseResponse<String>> checkFirmwareUpdate(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId,
            @Query("currentVersion") String currentVersion);

    /**
     * 更新设备状态
     * 
     * @param authToken 认证令牌
     * @param deviceId  设备ID
     * @param status    设备状态
     * @return 更新结果响应
     */
    @PUT("api/devices/{deviceId}/status")
    Call<BaseResponse<Void>> updateDeviceStatus(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId,
            @Query("status") String status);

    /**
     * 发送设备控制命令
     * 
     * @param authToken  认证令牌
     * @param deviceId   设备ID
     * @param command    控制命令
     * @param parameters 命令参数
     * @return 命令执行结果响应
     */
    @POST("api/devices/{deviceId}/command")
    Call<BaseResponse<String>> sendDeviceCommand(
            @Header("Authorization") String authToken,
            @Path("deviceId") String deviceId,
            @Query("command") String command,
            @Query("parameters") String parameters);
}