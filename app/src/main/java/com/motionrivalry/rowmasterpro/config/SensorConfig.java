package com.motionrivalry.rowmasterpro.config;

/**
 * 传感器相关配置
 * 包含桨频检测、IMU传感器、GPS定位等参数
 */
public class SensorConfig {

  // ========== 桨频检测配置 ==========
  public static final int STROKE_MIN_GAP_MS = 1200;
  public static final double STROKE_MIN_ACCEL = 1.2;
  public static final double STROKE_MIN_ACCEL_STRICT = 1.3;
  public static final int STROKE_MIN_GAP_SPEEDOMETER_MS = 1400;
  public static final int STROKE_IDLE_TIMEOUT_MS = 10000;
  public static final int STROKE_IDLE_TIMEOUT_SPEEDOMETER_MS = 5000;
  public static final double STROKE_REFRESH_LOWER_THRESH = -1.2;
  public static final double MAX_STROKE_RATE_SPM = 50;
  public static final double MIN_BOAT_SPEED_FOR_STROKE = 1.2;

  // ========== Xsens DOT传感器配置 ==========
  public static final int XSENS_SAMPLING_RATE_HZ = 30;
  public static final int XSENS_SAMPLING_RATE_LOW_HZ = 20;
  public static final int XSENS_LOAD_CONTROL_THRESHOLD = 2;
  public static final int XSENS_CONNECTION_TIMEOUT_MS = 10000;
  public static final int XSENS_RECONNECT_INTERVAL_MS = 2000;
  public static final int XSENS_RECONNECT_LOOP_COUNT = 3;
  public static final int XSENS_WATCHDOG_INTERVAL_MS = 3000;
  public static final int XSENS_DATA_TIMEOUT_MS = 5000;
  public static final int XSENS_FILTER_PROFILE_DYNAMIC = 1;

  // ========== 手机传感器配置 ==========
  public static final int PHONE_SENSOR_SAMPLING_INTERVAL_US = 50000;
  public static final double LOW_PASS_FILTER_ALPHA = 0.95;

  // ========== GPS定位配置 ==========
  public static final int GPS_UPDATE_INTERVAL_MS = 500;
  public static final int GPS_MIN_DISTANCE_M = 3;
  public static final float GPS_ABNORMAL_DISTANCE_M = 100f;
  public static final int AMAP_LOCATION_INTERVAL_MS = 1000;
  public static final int AMAP_LOCATION_INTERVAL_DASHBOARD_MS = 2000;

  // ========== 数据处理配置 ==========
  public static final int ACCEL_CACHE_LENGTH = 10;
  public static final int STROKE_RATE_CACHE_LENGTH = 4;
  public static final int BOAT_SPEED_CACHE_LENGTH = 10;
  public static final int BOAT_YAW_CACHE_LENGTH = 20;
  public static final int BOAT_YAW_CACHE_LENGTH_DASHBOARD = 52;
  public static final int BOAT_ROLL_CACHE_LENGTH = 20;
  public static final float YAW_ADJUST_RATIO = 1.4f;

  // ========== 日志记录配置 ==========
  public static final long LOG_FILE_MAX_DURATION_MS = 480000;
  public static final int LOG_SAMPLING_RATE_HZ = 30;

  // ========== 角度转换配置 ==========
  public static final double CORRECTION_LEFT = 90.0;
  public static final double CORRECTION_RIGHT = 90.0;
  public static final double CORRECTION_PADDLE = 135.0;
  public static final double CORRECTION_PADDLE_REVERSE = 225.0;
  public static final double CORRECTION_RIGHT_PITCH = 0.0;

  // ========== 功率计算配置 ==========
  public static final double WATTAGE_SUPPRESSION_RATIO = 0.35;
  public static final double FWD_SPLIT_RATIO = 0.40;
  public static final double FWD_SPLIT_THRESH_SPEED = 1.35;
  public static final double FWD_SPLIT_DIVIDE_FACTOR = 50.0;
  public static final double FWD_SPLIT_RANDOM_SEED_DIVIDE_FACTOR = 50.0;

  private SensorConfig() {
    throw new AssertionError("Cannot instantiate SensorConfig");
  }
}