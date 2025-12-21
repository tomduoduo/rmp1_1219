package com.motionrivalry.rowmasterpro.config;

import android.graphics.Color;

/**
 * UI相关配置
 * 包含颜色、尺寸、动画时间等UI常量
 */
public class UIConfig {
    
    // ========== 颜色配置 ========== 
    public static final int COLOR_PRIMARY_GREEN = Color.parseColor("#4CAF50"); 
    public static final int COLOR_WARNING_ORANGE = Color.parseColor("#FF9800"); 
    public static final int COLOR_ERROR_RED = Color.parseColor("#F44336"); 
    public static final int COLOR_TEXT_GRAY = Color.parseColor("#6C6C6C"); 
    public static final int COLOR_DARK_GRAY = Color.parseColor("#424242"); 
    public static final int COLOR_WHITE = Color.parseColor("#FFFFFF"); 
    public static final int COLOR_OVERLAY = Color.parseColor("#80000000"); 
    
    // ========== 心率分区配置 ========== 
    public static final int HEART_RATE_ZONE_1 = 90; 
    public static final int HEART_RATE_ZONE_2 = 150; 
    public static final int HEART_RATE_ZONE_3 = 180; 
    public static final double HEART_RATE_ZONE_0_DASHBOARD = 80; 
    public static final double HEART_RATE_ZONE_1_DASHBOARD = 95; 
    public static final double HEART_RATE_ZONE_2_DASHBOARD = 133; 
    public static final double HEART_RATE_ZONE_3_DASHBOARD = 152; 
    
    // ========== 船速分区配置 ========== 
    public static final double BOAT_SPEED_ZONE_0 = 1.0; 
    public static final double BOAT_SPEED_ZONE_1 = 2.0; 
    public static final double BOAT_SPEED_ZONE_2 = 3.0; 
    public static final double BOAT_SPEED_ZONE_3 = 4.0; 
    
    // ========== 桨频分区配置 ========== 
    public static final double STROKE_RATE_ZONE_0 = 17; 
    public static final double STROKE_RATE_ZONE_1 = 26; 
    public static final double STROKE_RATE_ZONE_2 = 34; 
    public static final double STROKE_RATE_ZONE_3 = 43; 
    
    // ========== 动画配置 ========== 
    public static final int ANIMATION_DURATION_MS = 200; 
    public static final int ANIMATION_DURATION_FAST_MS = 100; 
    public static final int ANIMATION_DURATION_SLOW_MS = 500; 
    public static final int COUNTDOWN_DURATION_MS = 5000; 
    public static final int COUNTDOWN_DELAY_MS = 5500; 
    public static final int UI_REFRESH_INTERVAL_MS = 50; 
    
    // ========== 弹窗配置 ========== 
    public static final float POPUP_BACKGROUND_ALPHA = 0.2f; 
    public static final float POPUP_BACKGROUND_ALPHA_DARK = 0.3f; 
    public static final float NORMAL_BACKGROUND_ALPHA = 1.0f; 
    public static final float ALPHA_HALF = 0.5f; 
    public static final float ALPHA_SLIGHT = 0.7f; 
    public static final float ALPHA_VERY_SLIGHT = 0.9f; 
    
    // ========== 屏幕判断配置 ========== 
    public static final int TABLET_DPI_THRESHOLD = 250; 
    
    // ========== 数据更新配置 ========== 
    public static final int DATA_UPDATE_INTERVAL_MS = 2000; 
    public static final int DATA_UPDATE_INITIAL_DELAY_MS = 5000; 
    public static final int SECONDARY_DATA_UPDATE_INTERVAL_MS = 20; 
    
    // ========== 文本格式配置 ========== 
    public static final String FORMAT_STROKE_RATE = "0.0"; 
    public static final String FORMAT_SPEED = "0.0"; 
    public static final String FORMAT_DISTANCE = "0"; 
    public static final String FORMAT_ANGLE = "0"; 
    public static final String FORMAT_TIME_LOG = "yyyy-MM-dd-HH: mm:ss"; 
    public static final String FORMAT_TIME_RESULT = "yyyy年MM月dd日 HH: mm:ss"; 
    
    // ========== 默认显示值 ========== 
    public static final String DEFAULT_DISTANCE = "0"; 
    public static final String DEFAULT_SPEED = "0.0"; 
    public static final String DEFAULT_STROKE_RATE = "0.0"; 
    public static final String DEFAULT_TIME = "0:00"; 
    
    private UIConfig() { 
        throw new AssertionError("Cannot instantiate UIConfig"); 
    } 
}