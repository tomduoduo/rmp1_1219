package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;

import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.models.XsensDotPayload;
import com.xsens.dot.android.sdk.utils.XsensDotScanner;
// import com.xsens.dot.android.sdk.utils.XsensDotLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Xsens DOT 传感器管理器
 * 统一管理多个 Xsens DOT 传感器的扫描、连接、数据接收和记录功能
 */
public class XsensDotManager implements XsensDotDeviceCallback, XsensDotScannerCallback {

    private final Context context;
    private XsensDotScanner xsDotScanner;
    private boolean isScanning = false;

    private final List<XsensDotDevice> connectedDevices;
    private final Map<String, XsensDotDevice> deviceMap;
    // private final Map<String, XsensDotLogger> loggerMap;

    private SensorDataListener dataListener;
    private ConnectionListener connectionListener;
    private ScanListener scanListener;

    // 传感器配置
    private int payloadMode = XsensDotPayload.PAYLOAD_TYPE_ORIENTATION_EULER;
    private int samplingRate = 20;
    private int loadControl = 2;
    private String fileLocation;

    /**
     * 传感器数据监听器
     */
    public interface SensorDataListener {
        void onDataReceived(String deviceAddress, XsensDotData data);

        void onDataError(String deviceAddress, Exception error);
    }

    /**
     * 连接状态监听器
     */
    public interface ConnectionListener {
        void onDeviceConnected(String deviceAddress);

        void onDeviceDisconnected(String deviceAddress);

        void onConnectionError(String deviceAddress, Exception error);
    }

    /**
     * 扫描监听器
     */
    public interface ScanListener {
        void onScanStarted();

        void onScanStopped();

        void onDeviceFound(BluetoothDevice device);

        void onScanError(Exception error);
    }

    /**
     * 构造函数
     */
    public XsensDotManager(Context context) {
        this.context = context;
        this.connectedDevices = new ArrayList<>();
        this.deviceMap = new HashMap<>();
        // this.loggerMap = new HashMap<>();

        // 初始化文件存储位置
        this.fileLocation = context.getFilesDir() + "/xsens/";
        createPath(fileLocation);

        // Xsens DOT SDK 初始化（根据 Dashboard.java 中的使用方式）
        // XsensDotSdk.setDebugEnabled(false);
        // XsensDotSdk.setReconnectEnabled(true);
    }

    /**
     * 设置数据监听器
     */
    public void setDataListener(SensorDataListener listener) {
        this.dataListener = listener;
    }

    /**
     * 设置连接监听器
     */
    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    /**
     * 设置扫描监听器
     */
    public void setScanListener(ScanListener listener) {
        this.scanListener = listener;
    }

    /**
     * 设置传感器配置
     */
    public void setSensorConfig(int payloadMode, int samplingRate, int loadControl) {
        this.payloadMode = payloadMode;
        this.samplingRate = samplingRate;
        this.loadControl = loadControl;
    }

    /**
     * 开始扫描传感器
     */
    public boolean startScanning() {
        if (xsDotScanner == null) {
            xsDotScanner = new XsensDotScanner(context, this);
            xsDotScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        }

        if (!isScanning) {
            isScanning = xsDotScanner.startScan();
            if (isScanning && scanListener != null) {
                scanListener.onScanStarted();
            }
        }

        return isScanning;
    }

    /**
     * 停止扫描
     */
    public boolean stopScanning() {
        if (isScanning && xsDotScanner != null) {
            isScanning = !xsDotScanner.stopScan();
            if (!isScanning && scanListener != null) {
                scanListener.onScanStopped();
            }
        }
        return !isScanning;
    }

    /**
     * 连接指定传感器
     */
    public void connectDevice(BluetoothDevice device) {
        try {
            XsensDotDevice xsensDevice = new XsensDotDevice(context, device, this);
            String address = device.getAddress();

            if (!deviceMap.containsKey(address)) {
                deviceMap.put(address, xsensDevice);
                xsensDevice.connect();
            }
        } catch (Exception e) {
            if (connectionListener != null) {
                connectionListener.onConnectionError(device.getAddress(), e);
            }
        }
    }

    /**
     * 断开指定传感器
     */
    public void disconnectDevice(String deviceAddress) {
        XsensDotDevice device = deviceMap.get(deviceAddress);
        if (device != null) {
            device.disconnect();
            deviceMap.remove(deviceAddress);
            connectedDevices.remove(device);

            // 停止数据记录
            stopDataLogging(deviceAddress);
        }
    }

    /**
     * 断开所有传感器
     */
    public void disconnectAll() {
        for (XsensDotDevice device : connectedDevices) {
            device.disconnect();
        }
        connectedDevices.clear();
        deviceMap.clear();

        // 停止所有数据记录（由 Dashboard 处理）
        // for (String address : loggerMap.keySet()) {
        // stopDataLogging(address);
        // }
        // loggerMap.clear();
    }

    /**
     * 开始数据记录（由 Dashboard 处理具体的数据记录逻辑）
     */
    public void startDataLogging(String deviceAddress) {
        // 数据记录功能由 Dashboard 处理，这里只提供接口
        if (dataListener != null) {
            // 通知 Dashboard 开始记录数据
        }
    }

    /**
     * 停止数据记录（由 Dashboard 处理具体的数据记录逻辑）
     */
    public void stopDataLogging(String deviceAddress) {
        // 数据记录功能由 Dashboard 处理，这里只提供接口
        if (dataListener != null) {
            // 通知 Dashboard 停止记录数据
        }
    }

    /**
     * 获取已连接设备列表
     */
    public List<XsensDotDevice> getConnectedDevices() {
        return new ArrayList<>(connectedDevices);
    }

    /**
     * 获取设备数量
     */
    public int getConnectedDeviceCount() {
        return connectedDevices.size();
    }

    /**
     * 检查是否正在扫描
     */
    public boolean isScanning() {
        return isScanning;
    }

    // ========== XsensDotScannerCallback 实现 ==========

    @Override
    public void onXsensDotScanned(BluetoothDevice device, int rssi) {
        if (scanListener != null) {
            scanListener.onDeviceFound(device);
        }
    }

    // ========== XsensDotDeviceCallback 实现 ==========

    @Override
    public void onXsensDotDataChanged(String deviceAddress, XsensDotData data) {
        // 通知数据监听器
        if (dataListener != null) {
            dataListener.onDataReceived(deviceAddress, data);
        }
    }

    @Override
    public void onXsensDotConnectionChanged(String deviceAddress, int connectionState) {
        XsensDotDevice device = deviceMap.get(deviceAddress);

        // 连接状态处理（根据实际 SDK 常量定义）
        // 通常连接状态常量定义在 XsensDotDevice 或相关类中
        // 这里使用数值判断，具体值需要根据 SDK 文档确定
        if (connectionState == 2) { // 假设 2 表示已连接
            if (device != null && !connectedDevices.contains(device)) {
                connectedDevices.add(device);
                if (connectionListener != null) {
                    connectionListener.onDeviceConnected(deviceAddress);
                }
            }
        } else if (connectionState == 0) { // 假设 0 表示已断开
            if (device != null) {
                connectedDevices.remove(device);
                if (connectionListener != null) {
                    connectionListener.onDeviceDisconnected(deviceAddress);
                }
            }
        }
    }

    @Override
    public void onXsensDotServicesDiscovered(String deviceAddress, int status) {
        // 服务发现完成后的处理
    }

    @Override
    public void onXsensDotFirmwareVersionRead(String deviceAddress, String firmwareVersion) {
        // 固件版本读取完成
    }

    @Override
    public void onXsensDotTagChanged(String deviceAddress, String tag) {
        // 设备标签改变
    }

    @Override
    public void onXsensDotBatteryChanged(String deviceAddress, int batteryLevel, int chargingStatus) {
        // 电池状态改变
    }

    @Override
    public void onXsensDotInitDone(String deviceAddress) {
        // 设备初始化完成
    }

    @Override
    public void onXsensDotButtonClicked(String deviceAddress, long timestamp) {
        // 设备按钮点击
    }

    @Override
    public void onXsensDotPowerSavingTriggered(String deviceAddress) {
        // 省电模式触发
    }

    @Override
    public void onReadRemoteRssi(String deviceAddress, int rssi) {
        // RSSI 值读取
    }

    @Override
    public void onXsensDotOutputRateUpdate(String deviceAddress, int outputRate) {
        // 输出速率更新
    }

    @Override
    public void onXsensDotFilterProfileUpdate(String deviceAddress, int filterProfile) {
        // 滤波器配置更新
    }

    @Override
    public void onXsensDotGetFilterProfileInfo(String deviceAddress,
            ArrayList<com.xsens.dot.android.sdk.models.FilterProfileInfo> filterProfiles) {
        // 获取滤波器配置信息
    }

    @Override
    public void onSyncStatusUpdate(String deviceAddress, boolean isSynced) {
        // 同步状态更新
    }

    // ========== 辅助方法 ==========

    /**
     * 创建文件路径
     */
    private void createPath(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        stopScanning();
        disconnectAll();

        if (xsDotScanner != null) {
            xsDotScanner = null;
        }
    }
}