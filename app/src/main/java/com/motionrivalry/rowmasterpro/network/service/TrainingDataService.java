package com.motionrivalry.rowmasterpro.network.service;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.motionrivalry.rowmasterpro.network.NetworkManager;
import com.motionrivalry.rowmasterpro.network.api.TrainingDataApi;
import com.motionrivalry.rowmasterpro.network.callback.NetworkCallback;
import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.TrainingData;
import com.motionrivalry.rowmasterpro.network.model.TrainingStatistics;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 训练数据服务类
 * 提供训练数据相关的网络操作封装，包括数据上传、下载、统计等
 * 使用单例模式确保全局一致性
 */
public class TrainingDataService {

    private static final String TAG = "TrainingDataService";
    private static TrainingDataService instance;
    private final TrainingDataApi trainingDataApi;
    private final Handler mainHandler;
    private final UserService userService;

    /**
     * 私有构造函数，初始化API接口和依赖服务
     */
    private TrainingDataService() {
        this.trainingDataApi = NetworkManager.getInstance().createApi(TrainingDataApi.class);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.userService = UserService.getInstance();
    }

    /**
     * 获取训练数据服务单例实例
     * 
     * @return TrainingDataService实例
     */
    public static synchronized TrainingDataService getInstance() {
        if (instance == null) {
            instance = new TrainingDataService();
        }
        return instance;
    }

    /**
     * 上传训练数据
     * 
     * @param trainingData 训练数据
     * @param callback     上传结果回调
     */
    public void uploadTrainingData(TrainingData trainingData, NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (trainingData == null) {
            handleError(callback, 400, "训练数据不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<String>> call = trainingDataApi.uploadTrainingData(authToken, trainingData);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "训练数据上传成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "训练数据上传失败");
            }
        });
    }

    /**
     * 批量上传训练数据
     * 
     * @param trainingDataList 训练数据列表
     * @param callback         上传结果回调
     */
    public void uploadTrainingDataBatch(List<TrainingData> trainingDataList, NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (trainingDataList == null || trainingDataList.isEmpty()) {
            handleError(callback, 400, "训练数据列表不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<String>> call = trainingDataApi.uploadTrainingDataBatch(authToken, trainingDataList);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "批量训练数据上传成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "批量训练数据上传失败");
            }
        });
    }

    /**
     * 下载训练数据
     * 
     * @param dataId   数据ID
     * @param callback 下载结果回调
     */
    public void downloadTrainingData(String dataId, NetworkCallback<TrainingData> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(dataId)) {
            handleError(callback, 400, "数据ID不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<TrainingData>> call = trainingDataApi.downloadTrainingData(authToken, dataId);
        call.enqueue(new Callback<BaseResponse<TrainingData>>() {
            @Override
            public void onResponse(Call<BaseResponse<TrainingData>> call,
                    Response<BaseResponse<TrainingData>> response) {
                handleResponse(response, callback, "训练数据下载成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<TrainingData>> call, Throwable t) {
                handleNetworkError(callback, t, "训练数据下载失败");
            }
        });
    }

    /**
     * 获取用户训练数据列表
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @param callback 获取结果回调
     */
    public void getUserTrainingData(int page, int pageSize, NetworkCallback<List<TrainingData>> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (page < 1 || pageSize < 1) {
            handleError(callback, 400, "页码和每页数量必须大于0");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<List<TrainingData>>> call = trainingDataApi.getUserTrainingData(authToken, page, pageSize);
        call.enqueue(new Callback<BaseResponse<List<TrainingData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<TrainingData>>> call,
                    Response<BaseResponse<List<TrainingData>>> response) {
                handleResponse(response, callback, "训练数据列表获取成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<List<TrainingData>>> call, Throwable t) {
                handleNetworkError(callback, t, "训练数据列表获取失败");
            }
        });
    }

    /**
     * 获取训练统计信息
     * 
     * @param userId    用户ID
     * @param timeRange 时间范围（day/week/month/year）
     * @param callback  获取结果回调
     */
    public void getTrainingStatistics(String userId, String timeRange, NetworkCallback<TrainingStatistics> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(timeRange)) {
            handleError(callback, 400, "用户ID和时间范围不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        // 获取当前日期作为结束日期，一个月前作为开始日期
        String endDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, -1);
        String startDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(cal.getTime());

        Call<BaseResponse<TrainingStatistics>> call = trainingDataApi.getTrainingStatistics(authToken, userId,
                timeRange, startDate, endDate);
        call.enqueue(new Callback<BaseResponse<TrainingStatistics>>() {
            @Override
            public void onResponse(Call<BaseResponse<TrainingStatistics>> call,
                    Response<BaseResponse<TrainingStatistics>> response) {
                handleResponse(response, callback, "训练统计信息获取成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<TrainingStatistics>> call, Throwable t) {
                handleNetworkError(callback, t, "训练统计信息获取失败");
            }
        });
    }

    /**
     * 同步训练数据
     * 
     * @param syncRequest 同步请求参数
     * @param callback    同步结果回调
     */
    public void syncTrainingData(Map<String, Object> syncRequest, NetworkCallback<List<TrainingData>> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        Call<BaseResponse<List<TrainingData>>> call = trainingDataApi.syncTrainingData(userService.getAuthToken(),
                syncRequest);
        call.enqueue(new Callback<BaseResponse<List<TrainingData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<TrainingData>>> call,
                    Response<BaseResponse<List<TrainingData>>> response) {
                handleResponse(response, callback, "训练数据同步成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<List<TrainingData>>> call, Throwable t) {
                handleNetworkError(callback, t, "训练数据同步失败");
            }
        });
    }

    /**
     * 导出训练数据
     * 
     * @param format    导出格式（csv/json/xlsx）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param callback  导出结果回调
     */
    public void exportTrainingData(String format, String startDate, String endDate, NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(format) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            handleError(callback, 400, "导出格式、开始日期和结束日期不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        String userId = userService.getCurrentUser() != null ? userService.getCurrentUser().getUserId() : "";
        Call<BaseResponse<String>> call = trainingDataApi.exportTrainingData(authToken, format, startDate, endDate,
                userId);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "训练数据导出成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "训练数据导出失败");
            }
        });
    }

    /**
     * 上传训练数据文件
     * 
     * @param file     数据文件
     * @param callback 上传结果回调
     */
    public void uploadTrainingFile(File file, NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (file == null || !file.exists()) {
            handleError(callback, 400, "文件不存在");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        // 创建文件请求体
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String authToken = userService.getAuthToken();
        Call<BaseResponse<String>> call = trainingDataApi.uploadTrainingFile(authToken, body);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "训练文件上传成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "训练文件上传失败");
            }
        });
    }

    /**
     * 检查登录状态
     * 
     * @param callback 回调接口
     * @return true表示已登录，false表示未登录
     */
    private boolean checkLoginStatus(NetworkCallback<?> callback) {
        if (!userService.isLoggedIn()) {
            handleError(callback, 401, "用户未登录");
            return false;
        }
        return true;
    }

    /**
     * 通用响应处理
     */
    private <T> void handleResponse(Response<BaseResponse<T>> response, NetworkCallback<T> callback,
            String successMessage) {
        if (response.isSuccessful() && response.body() != null) {
            BaseResponse<T> baseResponse = response.body();
            if (baseResponse.isSuccess()) {
                handleSuccess(callback, baseResponse.getData(), successMessage);
            } else {
                handleError(callback, baseResponse.getCode(), baseResponse.getMessage());
            }
        } else {
            handleApiError(response, callback);
        }
    }

    /**
     * 处理API错误响应
     */
    private <T> void handleApiError(Response<?> response, NetworkCallback<T> callback) {
        int errorCode = response.code();
        String errorMessage = "网络请求失败";

        switch (errorCode) {
            case 401:
                errorMessage = "未授权，请重新登录";
                break;
            case 403:
                errorMessage = "权限不足";
                break;
            case 404:
                errorMessage = "请求的资源不存在";
                break;
            case 500:
                errorMessage = "服务器内部错误";
                break;
            default:
                errorMessage = "网络请求失败 (" + errorCode + ")";
                break;
        }

        handleError(callback, errorCode, errorMessage);
    }

    /**
     * 处理网络错误
     */
    private <T> void handleNetworkError(NetworkCallback<T> callback, Throwable t, String defaultMessage) {
        Log.e(TAG, defaultMessage, t);
        String errorMessage = t.getMessage();
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = defaultMessage;
        }
        handleError(callback, -1, errorMessage);
    }

    /**
     * 处理成功响应
     */
    private <T> void handleSuccess(NetworkCallback<T> callback, T data, String message) {
        if (callback != null) {
            mainHandler.post(() -> {
                callback.onSuccess(data);
                callback.onComplete();
            });
        }

        if (message != null) {
            Log.i(TAG, message);
        }
    }

    /**
     * 处理错误响应
     */
    private <T> void handleError(NetworkCallback<T> callback, int errorCode, String errorMessage) {
        Log.e(TAG, "Error " + errorCode + ": " + errorMessage);

        if (callback != null) {
            mainHandler.post(() -> {
                callback.onError(errorCode, errorMessage);
                callback.onComplete();
            });
        }
    }
}