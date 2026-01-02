package com.motionrivalry.rowmasterpro.network.service;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.motionrivalry.rowmasterpro.network.NetworkManager;
import com.motionrivalry.rowmasterpro.network.api.DeviceApi;
import com.motionrivalry.rowmasterpro.network.callback.NetworkCallback;
import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.DeviceInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设备服务类
 * 提供设备相关的网络操作封装，包括设备绑定、状态管理、固件更新等
 * 使用单例模式确保全局一致性
 */
public class DeviceService {

    private static final String TAG = "DeviceService";
    private static DeviceService instance;
    private final DeviceApi deviceApi;
    private final Handler mainHandler;
    private final UserService userService;

    // 设备状态缓存
    private DeviceInfo currentDevice;
    private boolean isDeviceBound = false;

    /**
     * 私有构造函数，初始化API接口和依赖服务
     */
    private DeviceService() {
        this.deviceApi = NetworkManager.getInstance().createApi(DeviceApi.class);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.userService = UserService.getInstance();
    }

    /**
     * 获取设备服务单例实例
     * 
     * @return DeviceService实例
     */
    public static synchronized DeviceService getInstance() {
        if (instance == null) {
            instance = new DeviceService();
        }
        return instance;
    }

    /**
     * 绑定设备
     * 
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param callback   绑定结果回调
     */
    public void bindDevice(String deviceId, String deviceName, String deviceType,
            NetworkCallback<DeviceInfo> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(deviceName)) {
            handleError(callback, 400, "设备ID和设备名称不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<DeviceInfo>> call = deviceApi.bindDevice(authToken, deviceId, deviceName, deviceType);
        call.enqueue(new Callback<BaseResponse<DeviceInfo>>() {
            @Override
            public void onResponse(Call<BaseResponse<DeviceInfo>> call, Response<BaseResponse<DeviceInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<DeviceInfo> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        // 更新设备缓存
                        currentDevice = baseResponse.getData();
                        isDeviceBound = true;

                        handleSuccess(callback, currentDevice, "设备绑定成功");
                    } else {
                        handleError(callback, baseResponse.getCode(), baseResponse.getMessage());
                    }
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<DeviceInfo>> call, Throwable t) {
                handleNetworkError(callback, t, "设备绑定失败");
            }
        });
    }

    /**
     * 解绑设备
     * 
     * @param deviceId 设备ID
     * @param callback 解绑结果回调
     */
    public void unbindDevice(String deviceId, NetworkCallback<Void> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId)) {
            handleError(callback, 400, "设备ID不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<Void>> call = deviceApi.unbindDevice(authToken, deviceId);
        call.enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 清理设备缓存
                    if (currentDevice != null && deviceId.equals(currentDevice.getDeviceId())) {
                        currentDevice = null;
                        isDeviceBound = false;
                    }

                    handleSuccess(callback, null, "设备解绑成功");
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                handleNetworkError(callback, t, "设备解绑失败");
            }
        });
    }

    /**
     * 获取用户绑定的设备列表
     * 
     * @param callback 获取结果回调
     */
    public void getUserDevices(NetworkCallback<List<DeviceInfo>> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<List<DeviceInfo>>> call = deviceApi.getUserDevices(authToken);
        call.enqueue(new Callback<BaseResponse<List<DeviceInfo>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<DeviceInfo>>> call,
                    Response<BaseResponse<List<DeviceInfo>>> response) {
                handleResponse(response, callback, "设备列表获取成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<List<DeviceInfo>>> call, Throwable t) {
                handleNetworkError(callback, t, "设备列表获取失败");
            }
        });
    }

    /**
     * 获取设备详情
     * 
     * @param deviceId 设备ID
     * @param callback 获取结果回调
     */
    public void getDeviceInfo(String deviceId, NetworkCallback<DeviceInfo> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId)) {
            handleError(callback, 400, "设备ID不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<DeviceInfo>> call = deviceApi.getDeviceInfo(authToken, deviceId);
        call.enqueue(new Callback<BaseResponse<DeviceInfo>>() {
            @Override
            public void onResponse(Call<BaseResponse<DeviceInfo>> call, Response<BaseResponse<DeviceInfo>> response) {
                handleResponse(response, callback, "设备信息获取成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<DeviceInfo>> call, Throwable t) {
                handleNetworkError(callback, t, "设备信息获取失败");
            }
        });
    }

    /**
     * 更新设备信息
     * 
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param callback   更新结果回调
     */
    public void updateDeviceInfo(String deviceId, String deviceName, String deviceType,
            NetworkCallback<DeviceInfo> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(deviceName)) {
            handleError(callback, 400, "设备ID和设备名称不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<DeviceInfo>> call = deviceApi.updateDeviceInfo(authToken, deviceId, deviceName, deviceType);
        call.enqueue(new Callback<BaseResponse<DeviceInfo>>() {
            @Override
            public void onResponse(Call<BaseResponse<DeviceInfo>> call, Response<BaseResponse<DeviceInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<DeviceInfo> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        // 更新设备缓存
                        currentDevice = baseResponse.getData();

                        handleSuccess(callback, currentDevice, "设备信息更新成功");
                    } else {
                        handleError(callback, baseResponse.getCode(), baseResponse.getMessage());
                    }
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<DeviceInfo>> call, Throwable t) {
                handleNetworkError(callback, t, "设备信息更新失败");
            }
        });
    }

    /**
     * 检查固件更新
     * 
     * @param deviceId       设备ID
     * @param currentVersion 当前固件版本
     * @param callback       检查结果回调
     */
    public void checkFirmwareUpdate(String deviceId, String currentVersion, NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(currentVersion)) {
            handleError(callback, 400, "设备ID和当前版本不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<String>> call = deviceApi.checkFirmwareUpdate(authToken, deviceId, currentVersion);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "固件更新检查完成");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "固件更新检查失败");
            }
        });
    }

    /**
     * 更新设备状态
     * 
     * @param deviceId 设备ID
     * @param status   设备状态（online/offline/maintenance）
     * @param callback 更新结果回调
     */
    public void updateDeviceStatus(String deviceId, String status, NetworkCallback<Void> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(status)) {
            handleError(callback, 400, "设备ID和状态不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<Void>> call = deviceApi.updateDeviceStatus(authToken, deviceId, status);
        call.enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                handleResponse(response, callback, "设备状态更新成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                handleNetworkError(callback, t, "设备状态更新失败");
            }
        });
    }

    /**
     * 发送设备控制命令
     * 
     * @param deviceId   设备ID
     * @param command    控制命令
     * @param parameters 命令参数
     * @param callback   发送结果回调
     */
    public void sendDeviceCommand(String deviceId, String command, String parameters,
            NetworkCallback<String> callback) {
        if (!checkLoginStatus(callback)) {
            return;
        }

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(command)) {
            handleError(callback, 400, "设备ID和命令不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        String authToken = userService.getAuthToken();
        Call<BaseResponse<String>> call = deviceApi.sendDeviceCommand(authToken, deviceId, command, parameters);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                handleResponse(response, callback, "设备命令发送成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                handleNetworkError(callback, t, "设备命令发送失败");
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

    /**
     * 获取当前设备信息
     * 
     * @return 当前设备信息，未绑定时返回null
     */
    public DeviceInfo getCurrentDevice() {
        return currentDevice;
    }

    /**
     * 检查设备是否已绑定
     * 
     * @return true表示已绑定，false表示未绑定
     */
    public boolean isDeviceBound() {
        return isDeviceBound && currentDevice != null;
    }

    /**
     * 更新本地设备信息
     * 
     * @param deviceInfo 新的设备信息
     */
    public void updateLocalDevice(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            this.currentDevice = deviceInfo;
            this.isDeviceBound = true;
        }
    }

    /**
     * 清理设备缓存
     */
    public void clearDeviceCache() {
        currentDevice = null;
        isDeviceBound = false;
    }
}