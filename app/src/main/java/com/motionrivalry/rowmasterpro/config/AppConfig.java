package com.motionrivalry.rowmasterpro.config;

/**
 * 应用全局配置类
 * 集中管理所有硬编码的配置参数
 */
public class AppConfig {
    
    // ========== 网络配置 ========== 
    public static final String SERVER_BASE_URL = ""; 
    public static final String UPLOAD_PATH = SERVER_BASE_URL + "/upload"; 
    public static final String UPDATE_PATH = SERVER_BASE_URL + "/update"; 
    public static final String LOGIN_PATH = SERVER_BASE_URL + "/login"; 
    public static final String ATHLETE_LIST_PATH = SERVER_BASE_URL + "/athlete_list"; 
    public static final int NETWORK_CONNECT_TIMEOUT_MS = 3000; 
    public static final int NETWORK_READ_TIMEOUT_MS = 10000; 
    
    // ========== 文件路径配置 ========== 
    public static final String DATA_FOLDER_NAME = "xsens"; 
    public static final String LOGIN_INFO_FOLDER = "loginInfo"; 
    public static final String LOGIN_INFO_FILE = "data.csv"; 
    public static final String ATHLETE_LIST_FOLDER = "athlete_list"; 
    public static final String HRM_PLAN_FOLDER = "hrm_plan_list"; 
    public static final String GUEST_FOLDER = "游客"; 
    public static final String PRIVATE_FOLDER = "专用"; 
    
    // ========== 权限配置 ========== 
    public static final String[] REQUIRED_PERMISSIONS = { 
        android.Manifest.permission.BLUETOOTH_CONNECT, 
        android.Manifest.permission.BLUETOOTH_SCAN, 
        android.Manifest.permission.READ_EXTERNAL_STORAGE, 
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 
        android.Manifest.permission.ACCESS_FINE_LOCATION, 
        android.Manifest.permission.ACCESS_COARSE_LOCATION, 
        android.Manifest.permission.INTERNET, 
        android.Manifest.permission.BLUETOOTH, 
        android.Manifest.permission.BLUETOOTH_ADMIN, 
        android.Manifest.permission.READ_PHONE_STATE, 
        android.Manifest.permission.ACCESS_WIFI_STATE, 
        android.Manifest.permission.ACCESS_NETWORK_STATE, 
        android.Manifest.permission.CHANGE_WIFI_STATE 
    }; 
    
    // ========== 默认值配置 ========== 
    public static final String DEFAULT_USERNAME = "guest"; 
    public static final String DEFAULT_PASSWORD = "guest"; 
    public static final String DEFAULT_LANGUAGE = "chn"; 
    public static final String LANGUAGE_ENGLISH = "eng"; 
    public static final String LANGUAGE_CHINESE = "chn"; 
    
    // ========== 硬件配置 ========== 
    public static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00"; 
    public static final int TABLET_DPI_THRESHOLD = 250; 
    
    // ========== 响应码配置 ========== 
    public static final String RESPONSE_CODE_SUCCESS = "u200"; 
    public static final String RESPONSE_CODE_EMPTY = "u410"; 
    public static final String RESPONSE_CODE_WRONG = "u411"; 
    public static final String RESPONSE_CODE_FAIL = "u412"; 
    public static final String RESPONSE_CODE_DEVICE_CONFLICT = "u413"; 
    
    // ========== 调试配置 ========== 
    public static final boolean DEBUG_ENABLED = true; 
    public static final String LOG_TAG_PREFIX = "RMP_"; 
    
    private AppConfig() { 
        throw new AssertionError("Cannot instantiate AppConfig"); 
    } 
}