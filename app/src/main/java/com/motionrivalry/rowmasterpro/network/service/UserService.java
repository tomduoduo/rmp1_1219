package com.motionrivalry.rowmasterpro.network.service;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.motionrivalry.rowmasterpro.network.NetworkManager;
import com.motionrivalry.rowmasterpro.network.api.UserApi;
import com.motionrivalry.rowmasterpro.network.callback.NetworkCallback;
import com.motionrivalry.rowmasterpro.network.model.BaseResponse;
import com.motionrivalry.rowmasterpro.network.model.UserData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户服务类
 * 提供用户相关的网络操作封装，包括登录、注册、用户信息管理等
 * 使用单例模式确保全局一致性
 */
public class UserService {

    private static final String TAG = "UserService";
    private static UserService instance;
    private final UserApi userApi;
    private final Handler mainHandler;

    // 用户状态缓存
    private UserData currentUser;
    private String authToken;
    private boolean isLoggedIn = false;

    /**
     * 私有构造函数，初始化API接口和主线程处理器
     */
    private UserService() {
        this.userApi = NetworkManager.getInstance().createApi(UserApi.class);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取用户服务单例实例
     * 
     * @return UserService实例
     */
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @param callback 登录结果回调
     */
    public void login(String username, String password, NetworkCallback<UserData> callback) {
        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            handleError(callback, 400, "用户名和密码不能为空");
            return;
        }

        Call<BaseResponse<UserData>> call = userApi.login(username, password);
        call.enqueue(new Callback<BaseResponse<UserData>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserData>> call, Response<BaseResponse<UserData>> response) {
                handleLoginResponse(response, callback);
            }

            @Override
            public void onFailure(Call<BaseResponse<UserData>> call, Throwable t) {
                handleNetworkError(callback, t, "登录失败");
            }
        });
    }

    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱
     * @param callback 注册结果回调
     */
    public void register(String username, String password, String email, NetworkCallback<UserData> callback) {
        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            handleError(callback, 400, "用户名、密码和邮箱不能为空");
            return;
        }

        Call<BaseResponse<UserData>> call = userApi.register(username, password, email);
        call.enqueue(new Callback<BaseResponse<UserData>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserData>> call, Response<BaseResponse<UserData>> response) {
                handleResponse(response, callback, "注册成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<UserData>> call, Throwable t) {
                handleNetworkError(callback, t, "注册失败");
            }
        });
    }

    /**
     * 获取用户信息
     * 
     * @param callback 获取结果回调
     */
    public void getUserInfo(NetworkCallback<UserData> callback) {
        if (!isLoggedIn || TextUtils.isEmpty(authToken)) {
            handleError(callback, 401, "用户未登录");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        Call<BaseResponse<UserData>> call = userApi.getUserInfo(authToken);
        call.enqueue(new Callback<BaseResponse<UserData>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserData>> call, Response<BaseResponse<UserData>> response) {
                handleResponse(response, callback, null);
            }

            @Override
            public void onFailure(Call<BaseResponse<UserData>> call, Throwable t) {
                handleNetworkError(callback, t, "获取用户信息失败");
            }
        });
    }

    /**
     * 用户登出
     * 
     * @param callback 登出结果回调
     */
    public void logout(NetworkCallback<Void> callback) {
        if (!isLoggedIn || TextUtils.isEmpty(authToken)) {
            handleError(callback, 401, "用户未登录");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        Call<BaseResponse<Void>> call = userApi.logout(authToken);
        call.enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 清理本地缓存
                    clearUserCache();
                    handleSuccess(callback, null, "登出成功");
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                handleNetworkError(callback, t, "登出失败");
            }
        });
    }

    /**
     * 修改密码
     * 
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param callback    修改结果回调
     */
    public void changePassword(String oldPassword, String newPassword, NetworkCallback<Void> callback) {
        if (!isLoggedIn || TextUtils.isEmpty(authToken)) {
            handleError(callback, 401, "用户未登录");
            return;
        }

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            handleError(callback, 400, "旧密码和新密码不能为空");
            return;
        }

        if (callback != null) {
            mainHandler.post(callback::onStart);
        }

        Call<BaseResponse<Void>> call = userApi.changePassword(authToken, oldPassword, newPassword);
        call.enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                handleResponse(response, callback, "密码修改成功");
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                handleNetworkError(callback, t, "密码修改失败");
            }
        });
    }

    /**
     * 处理登录响应
     * 登录成功后更新本地缓存状态
     */
    private void handleLoginResponse(Response<BaseResponse<UserData>> response, NetworkCallback<UserData> callback) {
        if (response.isSuccessful() && response.body() != null) {
            BaseResponse<UserData> baseResponse = response.body();
            if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                // 更新本地缓存
                currentUser = baseResponse.getData();
                authToken = currentUser.getToken();
                isLoggedIn = true;

                handleSuccess(callback, currentUser, "登录成功");
            } else {
                handleError(callback, baseResponse.getCode(), baseResponse.getMessage());
            }
        } else {
            handleApiError(response, callback);
        }
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
                clearUserCache();
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
     * 清理用户缓存
     */
    private void clearUserCache() {
        currentUser = null;
        authToken = null;
        isLoggedIn = false;
    }

    /**
     * 获取当前用户信息
     * 
     * @return 当前用户信息，未登录时返回null
     */
    public UserData getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取认证令牌
     * 
     * @return 认证令牌，未登录时返回null
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * 检查用户是否已登录
     * 
     * @return true表示已登录，false表示未登录
     */
    public boolean isLoggedIn() {
        return isLoggedIn && !TextUtils.isEmpty(authToken) &&
                currentUser != null && !currentUser.isTokenExpired();
    }

    /**
     * 更新本地用户信息
     * 
     * @param userData 新的用户信息
     */
    public void updateLocalUser(UserData userData) {
        if (userData != null) {
            this.currentUser = userData;
            if (!TextUtils.isEmpty(userData.getToken())) {
                this.authToken = userData.getToken();
            }
        }
    }
}