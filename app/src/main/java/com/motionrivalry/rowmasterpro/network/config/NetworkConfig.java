package com.motionrivalry.rowmasterpro.network.config;

/**
 * 网络配置管理器
 * 统一管理网络相关的配置参数
 */
public class NetworkConfig {

    // 环境类型
    public enum Environment {
        DEVELOPMENT("dev"),
        TESTING("test"),
        STAGING("staging"),
        PRODUCTION("prod");

        private final String value;

        Environment(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // 默认配置
    private static final String DEFAULT_BASE_URL_DEV = "https://dev-api.rowmasterpro.com/";
    private static final String DEFAULT_BASE_URL_TEST = "https://test-api.rowmasterpro.com/";
    private static final String DEFAULT_BASE_URL_STAGING = "https://staging-api.rowmasterpro.com/";
    private static final String DEFAULT_BASE_URL_PROD = "https://api.rowmasterpro.com/";

    // 超时配置
    private static final int DEFAULT_CONNECT_TIMEOUT = 30; // 秒
    private static final int DEFAULT_READ_TIMEOUT = 30; // 秒
    private static final int DEFAULT_WRITE_TIMEOUT = 30; // 秒

    // 重试配置
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_RETRY_DELAY = 1000; // 毫秒

    // 缓存配置
    private static final int DEFAULT_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int DEFAULT_CACHE_MAX_AGE = 60; // 秒

    // 当前配置
    private static Environment currentEnvironment = Environment.DEVELOPMENT;
    private static String customBaseUrl = null;

    /**
     * 获取当前环境的基础URL
     */
    public static String getBaseUrl() {
        if (customBaseUrl != null) {
            return customBaseUrl;
        }

        switch (currentEnvironment) {
            case DEVELOPMENT:
                return DEFAULT_BASE_URL_DEV;
            case TESTING:
                return DEFAULT_BASE_URL_TEST;
            case STAGING:
                return DEFAULT_BASE_URL_STAGING;
            case PRODUCTION:
                return DEFAULT_BASE_URL_PROD;
            default:
                return DEFAULT_BASE_URL_DEV;
        }
    }

    /**
     * 设置自定义基础URL
     */
    public static void setCustomBaseUrl(String baseUrl) {
        customBaseUrl = baseUrl;
    }

    /**
     * 清除自定义基础URL，恢复默认配置
     */
    public static void clearCustomBaseUrl() {
        customBaseUrl = null;
    }

    /**
     * 获取当前环境
     */
    public static Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * 设置当前环境
     */
    public static void setCurrentEnvironment(Environment environment) {
        currentEnvironment = environment;
    }

    /**
     * 获取连接超时时间（秒）
     */
    public static int getConnectTimeout() {
        return DEFAULT_CONNECT_TIMEOUT;
    }

    /**
     * 获取读取超时时间（秒）
     */
    public static int getReadTimeout() {
        return DEFAULT_READ_TIMEOUT;
    }

    /**
     * 获取写入超时时间（秒）
     */
    public static int getWriteTimeout() {
        return DEFAULT_WRITE_TIMEOUT;
    }

    /**
     * 获取最大重试次数
     */
    public static int getMaxRetries() {
        return DEFAULT_MAX_RETRIES;
    }

    /**
     * 获取重试延迟时间（毫秒）
     */
    public static int getRetryDelay() {
        return DEFAULT_RETRY_DELAY;
    }

    /**
     * 获取缓存大小（字节）
     */
    public static int getCacheSize() {
        return DEFAULT_CACHE_SIZE;
    }

    /**
     * 获取缓存最大有效期（秒）
     */
    public static int getCacheMaxAge() {
        return DEFAULT_CACHE_MAX_AGE;
    }

    /**
     * 检查是否为开发环境
     */
    public static boolean isDevelopment() {
        return currentEnvironment == Environment.DEVELOPMENT;
    }

    /**
     * 检查是否为测试环境
     */
    public static boolean isTesting() {
        return currentEnvironment == Environment.TESTING;
    }

    /**
     * 检查是否为预发布环境
     */
    public static boolean isStaging() {
        return currentEnvironment == Environment.STAGING;
    }

    /**
     * 检查是否为生产环境
     */
    public static boolean isProduction() {
        return currentEnvironment == Environment.PRODUCTION;
    }

    /**
     * 获取当前环境的显示名称
     */
    public static String getEnvironmentName() {
        switch (currentEnvironment) {
            case DEVELOPMENT:
                return "开发环境";
            case TESTING:
                return "测试环境";
            case STAGING:
                return "预发布环境";
            case PRODUCTION:
                return "生产环境";
            default:
                return "未知环境";
        }
    }

    /**
     * 获取完整的API URL
     */
    public static String getFullApiUrl(String endpoint) {
        String baseUrl = getBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        // 移除endpoint的前导斜杠
        if (endpoint.startsWith("/")) {
            endpoint = endpoint.substring(1);
        }

        return baseUrl + endpoint;
    }

    /**
     * 获取日志级别
     * 开发环境显示详细日志，生产环境只显示错误日志
     */
    public static String getLogLevel() {
        switch (currentEnvironment) {
            case DEVELOPMENT:
            case TESTING:
                return "DEBUG";
            case STAGING:
                return "INFO";
            case PRODUCTION:
                return "ERROR";
            default:
                return "INFO";
        }
    }

    /**
     * 是否应该启用网络日志
     */
    public static boolean shouldEnableNetworkLogging() {
        return isDevelopment() || isTesting();
    }

    /**
     * 是否应该启用缓存
     */
    public static boolean shouldEnableCache() {
        return !isDevelopment(); // 开发环境不启用缓存，便于调试
    }

    /**
     * 获取用户代理字符串
     */
    public static String getUserAgent() {
        return "RowMasterPro/1.0 (Android; " + getEnvironmentName() + ")";
    }
}