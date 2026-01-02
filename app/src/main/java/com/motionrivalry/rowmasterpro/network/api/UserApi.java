package com.motionrivalry.rowmasterpro.network.api;

import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 用户相关API接口
 * 定义所有用户相关的REST API端点
 */
public interface UserApi {

        /**
         * 用户登录
         * 
         * @param username 用户名
         * @param password 密码
         * @return 用户数据响应
         */
        @POST("api/user/login")
        Call<BaseResponse<UserData>> login(
                        @Query("username") String username,
                        @Query("password") String password);

        /**
         * 用户登出
         * 
         * @param token 认证令牌
         * @return 基础响应
         */
        @POST("api/user/logout")
        Call<BaseResponse<Void>> logout(@Header("Authorization") String token);

        /**
         * 用户注册
         * 
         * @param username 用户名
         * @param password 密码
         * @param email    邮箱
         * @return 用户数据响应
         */
        @POST("api/user/register")
        Call<BaseResponse<UserData>> register(
                        @Query("username") String username,
                        @Query("password") String password,
                        @Query("email") String email);

        /**
         * 获取用户信息
         * 
         * @param token 认证令牌
         * @return 用户数据响应
         */
        @GET("api/user/info")
        Call<BaseResponse<UserData>> getUserInfo(@Header("Authorization") String token);

        /**
         * 更新用户信息
         * 
         * @param token    认证令牌
         * @param username 用户名
         * @param email    邮箱
         * @return 用户数据响应
         */
        @POST("api/user/update")
        Call<BaseResponse<UserData>> updateUserInfo(
                        @Header("Authorization") String token,
                        @Query("username") String username,
                        @Query("email") String email);

        /**
         * 验证令牌有效性
         * 
         * @param token 认证令牌
         * @return 基础响应
         */
        @GET("api/user/validate")
        Call<BaseResponse<Void>> validateToken(@Header("Authorization") String token);

        /**
         * 刷新令牌
         * 
         * @param token 当前令牌
         * @return 用户数据响应（包含新令牌）
         */
        @POST("api/user/refresh")
        Call<BaseResponse<UserData>> refreshToken(@Header("Authorization") String token);

        /**
         * 修改密码
         * 
         * @param token       认证令牌
         * @param oldPassword 旧密码
         * @param newPassword 新密码
         * @return 基础响应
         */
        @POST("api/user/changePassword")
        Call<BaseResponse<Void>> changePassword(
                        @Header("Authorization") String token,
                        @Query("oldPassword") String oldPassword,
                        @Query("newPassword") String newPassword);

        /**
         * 重置密码
         * 
         * @param email 邮箱地址
         * @return 基础响应
         */
        @POST("api/user/resetPassword")
        Call<BaseResponse<Void>> resetPassword(@Query("email") String email);

        /**
         * 设备绑定
         * 
         * @param token      认证令牌
         * @param deviceId   设备ID
         * @param deviceName 设备名称
         * @return 基础响应
         */
        @POST("api/user/bindDevice")
        Call<BaseResponse<Void>> bindDevice(
                        @Header("Authorization") String token,
                        @Query("deviceId") String deviceId,
                        @Query("deviceName") String deviceName);

        /**
         * 设备解绑
         * 
         * @param token    认证令牌
         * @param deviceId 设备ID
         * @return 基础响应
         */
        @POST("api/user/unbindDevice")
        Call<BaseResponse<Void>> unbindDevice(
                        @Header("Authorization") String token,
                        @Query("deviceId") String deviceId);
}