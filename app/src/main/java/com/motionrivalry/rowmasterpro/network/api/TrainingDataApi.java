package com.motionrivalry.rowmasterpro.network.api;

import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.TrainingData;
import com.motionrivalry.rowmasterpro.network.model.TrainingStatistics;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 训练数据相关API接口
 * 定义所有训练数据相关的REST API端点
 */
public interface TrainingDataApi {

        /**
         * 上传训练数据
         * 
         * @param authToken    认证令牌
         * @param trainingData 训练数据
         * @return 上传结果响应
         */
        @POST("api/training/upload")
        Call<BaseResponse<String>> uploadTrainingData(
                        @Header("Authorization") String authToken,
                        @Body TrainingData trainingData);

        /**
         * 批量上传训练数据
         * 
         * @param authToken        认证令牌
         * @param trainingDataList 训练数据列表
         * @return 上传结果响应
         */
        @POST("api/training/uploadBatch")
        Call<BaseResponse<String>> uploadTrainingDataBatch(
                        @Header("Authorization") String authToken,
                        @Body List<TrainingData> trainingDataList);

        /**
         * 下载训练数据
         * 
         * @param authToken 认证令牌
         * @param dataId    数据ID
         * @return 训练数据响应
         */
        @GET("api/training/download")
        Call<BaseResponse<TrainingData>> downloadTrainingData(
                        @Header("Authorization") String authToken,
                        @Query("dataId") String dataId);

        /**
         * 获取用户训练数据列表
         * 
         * @param authToken 认证令牌
         * @param page      页码
         * @param pageSize  每页数量
         * @return 训练数据列表响应
         */
        @GET("api/training/list")
        Call<BaseResponse<List<TrainingData>>> getUserTrainingData(
                        @Header("Authorization") String authToken,
                        @Query("page") int page,
                        @Query("pageSize") int pageSize);

        /**
         * 获取训练统计信息
         * 
         * @param authToken 认证令牌
         * @param userId    用户ID
         * @param timeRange 时间范围
         * @param startDate 开始日期
         * @param endDate   结束日期
         * @return 训练统计响应
         */
        @GET("api/training/statistics")
        Call<BaseResponse<TrainingStatistics>> getTrainingStatistics(
                        @Header("Authorization") String authToken,
                        @Query("userId") String userId,
                        @Query("timeRange") String timeRange,
                        @Query("startDate") String startDate,
                        @Query("endDate") String endDate);

        /**
         * 同步训练数据
         * 
         * @param authToken   认证令牌
         * @param syncRequest 同步请求数据
         * @return 训练数据列表响应
         */
        @POST("api/training/sync")
        Call<BaseResponse<List<TrainingData>>> syncTrainingData(
                        @Header("Authorization") String authToken,
                        @Body Map<String, Object> syncRequest);

        /**
         * 导出训练数据
         * 
         * @param authToken 认证令牌
         * @param format    导出格式
         * @param startDate 开始日期
         * @param endDate   结束日期
         * @param userId    用户ID
         * @return 导出结果响应
         */
        @GET("api/training/export")
        Call<BaseResponse<String>> exportTrainingData(
                        @Header("Authorization") String authToken,
                        @Query("format") String format,
                        @Query("startDate") String startDate,
                        @Query("endDate") String endDate,
                        @Query("userId") String userId);

        /**
         * 上传训练数据文件
         * 
         * @param authToken 认证令牌
         * @param file      文件
         * @return 上传结果响应
         */
        @Multipart
        @POST("api/training/uploadFile")
        Call<BaseResponse<String>> uploadTrainingFile(
                        @Header("Authorization") String authToken,
                        @Part MultipartBody.Part file);
}