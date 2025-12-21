package com.motionrivalry.rowmasterpro;

import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;

/**
 * UIManager类用于统一管理UI更新操作
 * 封装了所有UI组件的显示更新和格式化逻辑
 */
public class UIManager {
  private final DecimalFormat speedFormat = new DecimalFormat("0.0");
  private final DecimalFormat distanceFormat = new DecimalFormat("0");

  private final TextView speedView, speedAvgView, speedMaxView, distanceView;
  private final TextView strokeRateView, strokeCountView, strokeRateAvgView, splitTimeView;
  private final ImageView boatRollView, boatYawView;

  /**
   * UIManager构造函数
   * 
   * @param speedView         速度显示视图
   * @param speedAvgView      平均速度显示视图
   * @param speedMaxView      最大速度显示视图
   * @param distanceView      距离显示视图
   * @param strokeRateView    划桨频率显示视图
   * @param strokeCountView   划桨计数显示视图
   * @param strokeRateAvgView 平均划桨频率显示视图
   * @param splitTimeView     分段时间显示视图
   * @param boatRollView      船只滚动显示视图
   * @param boatYawView       船只偏航显示视图
   */
  public UIManager(TextView speedView, TextView speedAvgView, TextView speedMaxView,
      TextView distanceView, TextView strokeRateView, TextView strokeCountView,
      TextView strokeRateAvgView, TextView splitTimeView,
      ImageView boatRollView, ImageView boatYawView) {
    this.speedView = speedView;
    this.speedAvgView = speedAvgView;
    this.speedMaxView = speedMaxView;
    this.distanceView = distanceView;
    this.strokeRateView = strokeRateView;
    this.strokeCountView = strokeCountView;
    this.strokeRateAvgView = strokeRateAvgView;
    this.splitTimeView = splitTimeView;
    this.boatRollView = boatRollView;
    this.boatYawView = boatYawView;
  }

  /**
   * 更新速度显示
   * 
   * @param speed 当前速度
   * @return 格式化后的速度文本
   */
  public String updateSpeed(double speed) {
    String text = speedFormat.format(speed);
    speedView.setText(text);
    return text;
  }

  /**
   * 更新平均速度显示
   * 
   * @param avgSpeed 平均速度
   */
  public void updateAvgSpeed(double avgSpeed) {
    speedAvgView.setText(speedFormat.format(avgSpeed));
  }

  /**
   * 更新最大速度显示
   * 
   * @param maxSpeed 最大速度
   */
  public void updateMaxSpeed(double maxSpeed) {
    speedMaxView.setText(speedFormat.format(maxSpeed));
  }

  /**
   * 更新距离显示
   * 
   * @param distance 行驶距离
   */
  public void updateDistance(double distance) {
    distanceView.setText(distanceFormat.format(distance));
  }

  /**
   * 更新分段时间显示
   * 
   * @param splitTime 分段时间（秒）
   */
  public void updateSplitTime(double splitTime) {
    int sec = (int) splitTime % 60;
    int min = ((int) splitTime - sec) / 60;
    String secText = sec < 10 ? "0" + sec : String.valueOf(sec);
    splitTimeView.setText(min + ":" + secText);
  }

  /**
   * 更新划桨频率显示
   * 
   * @param strokeRate 划桨频率
   * @return 格式化后的划桨频率文本
   */
  public String updateStrokeRate(double strokeRate) {
    String text = speedFormat.format(strokeRate);
    strokeRateView.setText(text);
    return text;
  }

  /**
   * 更新划桨计数显示
   * 
   * @param count 划桨计数
   */
  public void updateStrokeCount(int count) {
    strokeCountView.setText(distanceFormat.format(count));
  }

  /**
   * 更新平均划桨频率显示
   * 
   * @param avgStrokeRate 平均划桨频率
   */
  public void updateAvgStrokeRate(double avgStrokeRate) {
    strokeRateAvgView.setText(speedFormat.format(avgStrokeRate));
  }

  /**
   * 更新船只滚动显示
   * 
   * @param roll 滚动角度
   */
  public void updateBoatRoll(float roll) {
    boatRollView.setRotation(-roll);
  }

  /**
   * 更新船只偏航显示
   * 
   * @param yaw 偏航角度
   */
  public void updateBoatYaw(float yaw) {
    boatYawView.setRotation(-yaw);
  }

  /**
   * 重置所有显示为默认值
   */
  public void resetAllDisplays() {
    speedView.setText("0.0");
    speedAvgView.setText("0.0");
    speedMaxView.setText("0.0");
    distanceView.setText("0");
    strokeRateView.setText("0.0");
    strokeCountView.setText("0");
    strokeRateAvgView.setText("0.0");
    splitTimeView.setText("00:00");
    boatRollView.setRotation(0);
    boatYawView.setRotation(0);
  }
}