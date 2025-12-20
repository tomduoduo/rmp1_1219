package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

/**
 * GPS定位追踪器
 * 功能：GPS定位监听、速度计算、距离累计、Split Time计算
 */
public class LocationTracker {

  private static final String TAG = "LocationTracker";

  // 配置参数
  private final long minUpdateInterval;
  private final float minUpdateDistance;

  // 核心状态
  private double currentSpeed;
  private double maxSpeed;
  private double avgSpeed;
  private double totalDistance;
  private double distance500Cache;
  private Location lastLocation;
  private boolean isTracking;
  private long trackingStartTime; // 追踪开始时间戳

  // GPS定位相关
  private final Context context;
  private final LocationManager locationManager;
  private final LocationListener locationListener;

  // 计算参数
  private final double speedLowLimit = 0.5;
  private final double distanceTempHighLimit = 50;
  private final double distanceTempLowLimit = 1;
  private double speedLastUpdate;
  private int updateCount;
  private final int initiateLock = 5;

  // 位置坐标缓存
  private double latitude_0;
  private double longitude_0;

  // 监听器接口
  public interface LocationUpdateListener {
    void onLocationUpdate(LocationUpdate update);
  }

  private LocationUpdateListener updateListener;

  /**
   * 位置更新数据类
   */
  public static class LocationUpdate {
    public final double speed;
    public final double maxSpeed;
    public final double avgSpeed;
    public final double totalDistance;
    public final double splitTime;
    public final Location location;

    public LocationUpdate(double speed, double maxSpeed, double avgSpeed,
        double totalDistance, double splitTime, Location location) {
      this.speed = speed;
      this.maxSpeed = maxSpeed;
      this.avgSpeed = avgSpeed;
      this.totalDistance = totalDistance;
      this.splitTime = splitTime;
      this.location = location;
    }
  }

  /**
   * 构造函数
   * 
   * @param context     上下文
   * @param minInterval 最小更新间隔（毫秒）
   * @param minDistance 最小更新距离（米）
   */
  public LocationTracker(Context context, long minInterval, float minDistance) {
    this.context = context;
    this.minUpdateInterval = minInterval;
    this.minUpdateDistance = minDistance;
    this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    this.locationListener = createLocationListener();
    reset();
  }

  /**
   * 设置位置更新监听器
   * 
   * @param listener 监听器
   */
  public void setLocationUpdateListener(LocationUpdateListener listener) {
    this.updateListener = listener;
  }

  /**
   * 开始GPS追踪
   */
  public void startTracking() {
    if (isTracking) {
      Log.w(TAG, "GPS追踪已在运行中");
      return;
    }

    // 检查位置权限
    if (ActivityCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Log.w(TAG, "位置权限未授予，无法开始GPS追踪");
      return;
    }

    // 检查GPS是否可用
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      Log.w(TAG, "GPS服务未启用，无法开始追踪");
      return;
    }

    try {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
          minUpdateInterval, minUpdateDistance, locationListener);

      trackingStartTime = System.currentTimeMillis(); // 记录开始时间
      isTracking = true;
      Log.i(TAG, "GPS追踪已开始 - 间隔:" + minUpdateInterval + "ms, 距离:" + minUpdateDistance + "m");
    } catch (SecurityException e) {
      Log.e(TAG, "位置权限异常: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "参数错误: " + e.getMessage());
    } catch (Exception e) {
      Log.e(TAG, "开始GPS追踪失败: " + e.getMessage());
    }
  }

  /**
   * 停止GPS追踪
   */
  public void stopTracking() {
    if (!isTracking) {
      return;
    }

    try {
      locationManager.removeUpdates(locationListener);
      isTracking = false;
      Log.i(TAG, "GPS追踪已停止");
    } catch (Exception e) {
      Log.e(TAG, "停止GPS追踪失败: " + e.getMessage());
    }
  }

  /**
   * 重置所有状态
   */
  public void reset() {
    currentSpeed = 0;
    maxSpeed = 0;
    avgSpeed = 0;
    totalDistance = 0;
    distance500Cache = 0;
    lastLocation = null;
    updateCount = 0;
    latitude_0 = 0;
    longitude_0 = 0;
    speedLastUpdate = 0;
    trackingStartTime = 0; // 重置开始时间

    if (isTracking) {
      stopTracking();
    }

    Log.i(TAG, "位置追踪器已重置");
  }

  /**
   * 创建位置监听器
   */
  private LocationListener createLocationListener() {
    return new LocationListener() {
      public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
          case LocationProvider.AVAILABLE:
            Log.i(TAG, "当前GPS状态为可见状态");
            break;
          case LocationProvider.OUT_OF_SERVICE:
            Log.i(TAG, "当前GPS状态为服务区外状态");
            break;
          case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.i(TAG, "当前GPS状态为暂停服务状态");
            break;
        }
      }

      public void onProviderEnabled(String provider) {
        Log.i(TAG, "GPS服务已启用");
      }

      public void onProviderDisabled(String provider) {
        Log.i(TAG, "GPS服务已禁用");
        lastLocation = null;
      }

      public void onLocationChanged(Location location) {
        updateToNewLocation(location);
      }
    };
  }

  /**
   * 处理新的位置更新
   * 
   * @param location 新的位置信息
   */
  private void updateToNewLocation(Location location) {
    if (location != null) {
      double latitude = location.getLatitude();
      double longitude = location.getLongitude();

      double distanceTemp = 0;
      currentSpeed = 0;
      updateCount = updateCount + 1;

      if (location.hasSpeed() && location.getSpeed() > speedLowLimit && updateCount > initiateLock) {
        try {
          distanceTemp = getDistance(latitude_0, longitude_0, latitude, longitude);

          // 距离过滤
          if (distanceTemp > distanceTempHighLimit || distanceTemp < distanceTempLowLimit) {
            distanceTemp = 0;
          }

          totalDistance = totalDistance + distanceTemp;
          latitude_0 = latitude;
          longitude_0 = longitude;

        } catch (Exception e) {
          latitude_0 = latitude;
          longitude_0 = longitude;
          currentSpeed = 0;
          distanceTemp = 0;
        }

        if (distanceTemp != 0 && location.getSpeed() > speedLowLimit) {
          currentSpeed = location.getSpeed();

          // 计算Split Time（500米配速）
          double splitTime = calculateSplitTime(currentSpeed);

          // 计算平均速度（基于总距离和总时间）
          avgSpeed = calculateAverageSpeed();

          // 更新最大速度
          if (currentSpeed > maxSpeed) {
            maxSpeed = currentSpeed;
          }

          // 更新最后位置
          lastLocation = location;

          // 通知监听器
          if (updateListener != null) {
            LocationUpdate update = new LocationUpdate(
                currentSpeed, maxSpeed, avgSpeed, totalDistance, splitTime, location);
            updateListener.onLocationUpdate(update);
          }
        }
      } else {
        currentSpeed = 0;
        latitude_0 = latitude;
        longitude_0 = longitude;
      }
    }
  }

  /**
   * 计算两点间距离
   */
  public double getDistance(double lat1, double lon1, double lat2, double lon2) {
    float[] results = new float[1];
    try {
      Location.distanceBetween(lat1, lon1, lat2, lon2, results);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return results[0];
  }

  /**
   * 计算500米配速（Split Time）
   */
  private double calculateSplitTime(double speed) {
    if (speed <= 0)
      return 0;
    return 500.0 / speed;
  }

  /**
   * 计算平均速度（基于总时间）
   */
  private double calculateAverageSpeed() {
    if (trackingStartTime == 0 || totalDistance == 0)
      return 0;

    long totalTime = System.currentTimeMillis() - trackingStartTime;
    if (totalTime <= 0)
      return 0;

    return totalDistance / (totalTime / 1000.0); // 转换为米/秒
  }

  // ========== 查询方法 ==========

  public double getCurrentSpeed() {
    return currentSpeed;
  }

  public double getMaxSpeed() {
    return maxSpeed;
  }

  public double getAvgSpeed() {
    return avgSpeed;
  }

  public double getTotalDistance() {
    return totalDistance;
  }

  public double getSplitTime() {
    return calculateSplitTime(currentSpeed);
  }

  public boolean isTracking() {
    return isTracking;
  }

  public Location getLastLocation() {
    return lastLocation;
  }

  public int getUpdateCount() {
    return updateCount;
  }
}