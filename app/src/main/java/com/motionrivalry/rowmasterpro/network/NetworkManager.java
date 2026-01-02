package com.motionrivalry.rowmasterpro.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

/**
 * 统一网络管理器
 * 提供网络请求的统一入口和管理
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static final int DEFAULT_TIMEOUT = 30; // 秒
    private static final int DEFAULT_MAX_RETRIES = 3;

    private static NetworkManager instance;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private Context context;

    // 服务器配置
    private String baseUrl = ""; // 可以根据环境动态设置

    private NetworkManager() {
        // 私有构造函数，防止直接实例化
    }

    /**
     * 获取单例实例
     */
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    /**
     * 初始化网络管理器
     */
    public void init(Context context, String baseUrl) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl;
        initOkHttpClient();
        initRetrofit();
    }

    /**
     * 初始化OkHttpClient
     */
    private void initOkHttpClient() {
        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 网络请求拦截器
        Interceptor networkInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // 添加通用请求头
                Request.Builder requestBuilder = request.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json");

                // 执行请求
                Response response = chain.proceed(requestBuilder.build());

                // 统一错误处理
                if (!response.isSuccessful()) {
                    handleErrorResponse(response.code(), response.message());
                }

                return response;
            }
        };

        // 重试拦截器
        Interceptor retryInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = null;
                IOException exception = null;

                int tryCount = 0;
                while (tryCount < DEFAULT_MAX_RETRIES) {
                    try {
                        response = chain.proceed(request);
                        if (response.isSuccessful()) {
                            return response;
                        }

                        // 如果响应不成功但不是网络错误，不重试
                        if (response.code() >= 400 && response.code() < 500) {
                            return response;
                        }

                        // 关闭响应
                        if (response.body() != null) {
                            response.body().close();
                        }

                    } catch (IOException e) {
                        exception = e;
                        Log.w(TAG, "Request failed on attempt " + (tryCount + 1), e);
                    }

                    tryCount++;

                    // 指数退避策略
                    if (tryCount < DEFAULT_MAX_RETRIES) {
                        try {
                            Thread.sleep((long) Math.pow(2, tryCount) * 1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Retry interrupted", e);
                        }
                    }
                }

                // 所有重试都失败
                if (exception != null) {
                    throw exception;
                } else if (response != null) {
                    return response;
                } else {
                    throw new IOException("Unknown request failure");
                }
            }
        };

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(networkInterceptor)
                .addInterceptor(retryInterceptor)
                .build();
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    /**
     * 获取Retrofit实例
     */
    public Retrofit getRetrofit() {
        if (retrofit == null) {
            throw new IllegalStateException("NetworkManager not initialized. Call init() first.");
        }
        return retrofit;
    }

    /**
     * 创建API接口实例
     * 
     * @param serviceClass API接口类
     * @param <T>          API接口类型
     * @return API接口实例
     */
    public <T> T createApi(Class<T> serviceClass) {
        if (retrofit == null) {
            throw new IllegalStateException("NetworkManager not initialized. Call init() first.");
        }
        return retrofit.create(serviceClass);
    }

    /**
     * 获取OkHttpClient实例
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 检查网络连接状态
     */
    public boolean isNetworkAvailable() {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }

    /**
     * 处理错误响应
     */
    private void handleErrorResponse(int code, String message) {
        Log.e(TAG, "HTTP Error: " + code + " - " + message);

        // 可以根据错误码进行不同的处理
        switch (code) {
            case 401:
                // 未授权，可能需要重新登录
                handleUnauthorized();
                break;
            case 403:
                // 权限不足
                handleForbidden();
                break;
            case 404:
                // 资源不存在
                handleNotFound();
                break;
            case 500:
            case 502:
            case 503:
                // 服务器错误
                handleServerError();
                break;
            default:
                // 其他错误
                handleGenericError(code, message);
                break;
        }
    }

    /**
     * 处理未授权错误
     */
    private void handleUnauthorized() {
        Log.w(TAG, "Unauthorized access detected");
        // 可以触发重新登录或跳转到登录页面
    }

    /**
     * 处理权限不足错误
     */
    private void handleForbidden() {
        Log.w(TAG, "Access forbidden");
        // 可以显示权限不足的提示
    }

    /**
     * 处理资源不存在错误
     */
    private void handleNotFound() {
        Log.w(TAG, "Resource not found");
        // 可以显示资源不存在的提示
    }

    /**
     * 处理服务器错误
     */
    private void handleServerError() {
        Log.e(TAG, "Server error occurred");
        // 可以显示服务器错误的提示
    }

    /**
     * 处理通用错误
     */
    private void handleGenericError(int code, String message) {
        Log.e(TAG, "Generic error: " + code + " - " + message);
        // 可以显示通用错误提示
    }

    /**
     * 设置基础URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        // 重新初始化Retrofit
        initRetrofit();
    }

    /**
     * 获取当前基础URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        if (okHttpClient != null) {
            okHttpClient.dispatcher().executorService().shutdown();
            okHttpClient.connectionPool().evictAll();
        }
    }
}