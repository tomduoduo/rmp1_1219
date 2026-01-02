package com.motionrivalry.rowmasterpro.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 蓝牙设备管理器
 * 负责管理所有蓝牙设备的连接、数据接收和状态管理
 */
public class BluetoothDeviceManager {

    private static final String TAG = "BluetoothDeviceManager";

    // 蓝牙服务和特征值UUID（Polar H10示例）
    private static final UUID HEART_RATE_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_MEASUREMENT_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    // 设备类型定义
    public enum DeviceType {
        HEART_RATE_MONITOR("心率监测器"),
        CADENCE_SENSOR("踏频传感器"),
        SPEED_SENSOR("速度传感器"),
        POWER_METER("功率计"),
        ROWING_MACHINE("划船机"),
        UNKNOWN("未知设备");

        private final String description;

        DeviceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 连接状态
    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    // 设备信息类
    public static class DeviceInfo {
        public String deviceId;
        public String deviceName;
        public String deviceAddress;
        public DeviceType deviceType;
        public ConnectionState connectionState;
        public int batteryLevel;
        public Map<String, Object> sensorData;
        public long lastDataTime;

        public DeviceInfo(String deviceId, String deviceName, String deviceAddress) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.deviceAddress = deviceAddress;
            this.deviceType = DeviceType.UNKNOWN;
            this.connectionState = ConnectionState.DISCONNECTED;
            this.batteryLevel = -1;
            this.sensorData = new HashMap<>();
            this.lastDataTime = 0;
        }
    }

    // 回调接口
    public interface BluetoothDeviceCallback {
        void onDeviceDiscovered(DeviceInfo device);

        void onDeviceConnected(DeviceInfo device);

        void onDeviceDisconnected(DeviceInfo device);

        void onDeviceConnectionFailed(DeviceInfo device, String error);

        void onHeartRateDataReceived(String deviceId, int heartRate);

        void onBatteryLevelReceived(String deviceId, int batteryLevel);

        void onSensorDataReceived(String deviceId, String sensorType, Object data);

        void onScanStarted();

        void onScanStopped();

        void onScanFailed(String error);

        void onBluetoothStateChanged(boolean enabled);
    }

    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private Handler mainHandler;

    // 设备管理
    private Map<String, DeviceInfo> connectedDevices;
    private Map<String, BluetoothGatt> deviceGatts;
    private Map<String, BluetoothGattCallback> deviceCallbacks;

    // 扫描相关
    private boolean isScanning;
    private Handler scanHandler;
    private Runnable scanStopRunnable;
    private int scanDuration = 10000; // 默认扫描时间10秒

    // 回调监听
    private List<BluetoothDeviceCallback> callbacks;

    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public BluetoothDeviceManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.connectedDevices = new HashMap<>();
        this.deviceGatts = new HashMap<>();
        this.deviceCallbacks = new HashMap<>();
        this.callbacks = new ArrayList<>();
        this.scanHandler = new Handler(Looper.getMainLooper());

        initializeBluetooth();
    }

    /**
     * 初始化蓝牙
     */
    private void initializeBluetooth() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter == null) {
            Log.e(TAG, "设备不支持蓝牙");
            notifyScanFailed("设备不支持蓝牙");
        }
    }

    /**
     * 检查蓝牙是否可用
     * 
     * @return true表示蓝牙可用
     */
    public boolean isBluetoothAvailable() {
        return bluetoothAdapter != null;
    }

    /**
     * 检查蓝牙是否已启用
     * 
     * @return true表示蓝牙已启用
     */
    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * 请求启用蓝牙
     * 
     * @return 启用蓝牙的Intent
     */
    public Intent getEnableBluetoothIntent() {
        return new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }

    /**
     * 添加回调监听
     * 
     * @param callback 回调接口
     */
    public void addCallback(BluetoothDeviceCallback callback) {
        if (callback != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    /**
     * 移除回调监听
     * 
     * @param callback 回调接口
     */
    public void removeCallback(BluetoothDeviceCallback callback) {
        if (callback != null) {
            callbacks.remove(callback);
        }
    }

    /**
     * 开始扫描蓝牙设备
     * 
     * @param duration 扫描持续时间（毫秒）
     */
    public void startScan(int duration) {
        if (!isBluetoothEnabled()) {
            notifyScanFailed("蓝牙未启用");
            return;
        }

        if (isScanning) {
            Log.w(TAG, "扫描已在进行中");
            return;
        }

        this.scanDuration = duration;
        isScanning = true;

        // 停止之前的扫描
        stopScan();

        // 开始新扫描
        bluetoothAdapter.startLeScan(leScanCallback);

        // 设置自动停止
        scanStopRunnable = new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        };
        scanHandler.postDelayed(scanStopRunnable, duration);

        notifyScanStarted();
        Log.i(TAG, "开始扫描蓝牙设备，持续时间：" + duration + "毫秒");
    }

    /**
     * 开始扫描（使用默认持续时间）
     */
    public void startScan() {
        startScan(scanDuration);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (!isScanning) {
            return;
        }

        isScanning = false;

        // 移除自动停止任务
        if (scanStopRunnable != null) {
            scanHandler.removeCallbacks(scanStopRunnable);
            scanStopRunnable = null;
        }

        // 停止扫描
        bluetoothAdapter.stopLeScan(leScanCallback);

        notifyScanStopped();
        Log.i(TAG, "停止扫描蓝牙设备");
    }

    /**
     * 检查是否正在扫描
     * 
     * @return true表示正在扫描
     */
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * 连接到设备
     * 
     * @param deviceAddress 设备地址
     * @return true表示连接请求已发送
     */
    public boolean connectToDevice(String deviceAddress) {
        if (!isBluetoothEnabled()) {
            Log.e(TAG, "蓝牙未启用");
            return false;
        }

        if (connectedDevices.containsKey(deviceAddress)) {
            DeviceInfo device = connectedDevices.get(deviceAddress);
            if (device.connectionState == ConnectionState.CONNECTED) {
                Log.w(TAG, "设备已连接：" + deviceAddress);
                return true;
            } else if (device.connectionState == ConnectionState.CONNECTING) {
                Log.w(TAG, "设备正在连接中：" + deviceAddress);
                return true;
            }
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Log.e(TAG, "找不到设备：" + deviceAddress);
            return false;
        }

        // 创建设备信息
        DeviceInfo deviceInfo = new DeviceInfo(
                device.getAddress(),
                device.getName(),
                device.getAddress());
        deviceInfo.connectionState = ConnectionState.CONNECTING;
        connectedDevices.put(deviceAddress, deviceInfo);

        // 创建GATT回调
        BluetoothGattCallback gattCallback = createGattCallback(deviceAddress);
        deviceCallbacks.put(deviceAddress, gattCallback);

        // 连接设备
        BluetoothGatt gatt = device.connectGatt(context, false, gattCallback);
        deviceGatts.put(deviceAddress, gatt);

        Log.i(TAG, "开始连接设备：" + deviceAddress);
        return true;
    }

    /**
     * 断开设备连接
     * 
     * @param deviceAddress 设备地址
     */
    public void disconnectDevice(String deviceAddress) {
        if (!connectedDevices.containsKey(deviceAddress)) {
            return;
        }

        DeviceInfo device = connectedDevices.get(deviceAddress);
        device.connectionState = ConnectionState.DISCONNECTING;

        BluetoothGatt gatt = deviceGatts.get(deviceAddress);
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
        }

        // 清理资源
        connectedDevices.remove(deviceAddress);
        deviceGatts.remove(deviceAddress);
        deviceCallbacks.remove(deviceAddress);

        notifyDeviceDisconnected(device);
        Log.i(TAG, "断开设备连接：" + deviceAddress);
    }

    /**
     * 断开所有设备连接
     */
    public void disconnectAllDevices() {
        List<String> deviceAddresses = new ArrayList<>(connectedDevices.keySet());
        for (String address : deviceAddresses) {
            disconnectDevice(address);
        }
    }

    /**
     * 获取已连接的设备列表
     * 
     * @return 设备列表
     */
    public List<DeviceInfo> getConnectedDevices() {
        return new ArrayList<>(connectedDevices.values());
    }

    /**
     * 获取特定设备信息
     * 
     * @param deviceAddress 设备地址
     * @return 设备信息，如果未找到返回null
     */
    public DeviceInfo getDeviceInfo(String deviceAddress) {
        return connectedDevices.get(deviceAddress);
    }

    /**
     * 检查设备是否已连接
     * 
     * @param deviceAddress 设备地址
     * @return true表示已连接
     */
    public boolean isDeviceConnected(String deviceAddress) {
        DeviceInfo device = connectedDevices.get(deviceAddress);
        return device != null && device.connectionState == ConnectionState.CONNECTED;
    }

    /**
     * 创建设备GATT回调
     * 
     * @param deviceAddress 设备地址
     * @return GATT回调
     */
    private BluetoothGattCallback createGattCallback(final String deviceAddress) {
        return new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            Log.i(TAG, "设备已连接：" + deviceAddress);
                            DeviceInfo device = connectedDevices.get(deviceAddress);
                            if (device != null) {
                                device.connectionState = ConnectionState.CONNECTED;
                                notifyDeviceConnected(device);
                            }

                            // 开始发现服务
                            gatt.discoverServices();
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            Log.i(TAG, "设备已断开：" + deviceAddress);
                            DeviceInfo device = connectedDevices.get(deviceAddress);
                            if (device != null) {
                                device.connectionState = ConnectionState.DISCONNECTED;
                                notifyDeviceDisconnected(device);
                            }

                            // 清理资源
                            gatt.close();
                            connectedDevices.remove(deviceAddress);
                            deviceGatts.remove(deviceAddress);
                            deviceCallbacks.remove(deviceAddress);
                        }
                    }
                });
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "发现服务成功：" + deviceAddress);
                    discoverDeviceCharacteristics(gatt, deviceAddress);
                } else {
                    Log.e(TAG, "发现服务失败：" + deviceAddress + ", 状态：" + status);
                    notifyDeviceConnectionFailed(connectedDevices.get(deviceAddress), "发现服务失败");
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                    int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    processCharacteristicData(deviceAddress, characteristic);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                processCharacteristicData(deviceAddress, characteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                    int status) {
                // 特征值写入完成
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    DeviceInfo device = connectedDevices.get(deviceAddress);
                    if (device != null) {
                        device.sensorData.put("rssi", rssi);
                        device.lastDataTime = System.currentTimeMillis();
                    }
                }
            }
        };
    }

    /**
     * 发现设备特征值
     * 
     * @param gatt          GATT客户端
     * @param deviceAddress 设备地址
     */
    private void discoverDeviceCharacteristics(BluetoothGatt gatt, String deviceAddress) {
        DeviceInfo device = connectedDevices.get(deviceAddress);
        if (device == null)
            return;

        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services) {
            UUID serviceUuid = service.getUuid();

            // 识别设备类型
            if (serviceUuid.equals(HEART_RATE_SERVICE_UUID)) {
                device.deviceType = DeviceType.HEART_RATE_MONITOR;
            }

            // 处理特征值
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                UUID charUuid = characteristic.getUuid();

                // 心率测量
                if (charUuid.equals(HEART_RATE_MEASUREMENT_UUID)) {
                    enableNotification(gatt, characteristic);
                }
                // 电池电量
                else if (charUuid.equals(BATTERY_LEVEL_UUID)) {
                    gatt.readCharacteristic(characteristic);
                }
            }
        }

        // 定期读取RSSI
        startRssiReading(deviceAddress);
    }

    /**
     * 启用特征值通知
     * 
     * @param gatt           GATT客户端
     * @param characteristic 特征值
     */
    private void enableNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        gatt.setCharacteristicNotification(characteristic, true);

        // 获取描述符并启用通知
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    /**
     * 处理特征值数据
     * 
     * @param deviceAddress  设备地址
     * @param characteristic 特征值
     */
    private void processCharacteristicData(String deviceAddress, BluetoothGattCharacteristic characteristic) {
        DeviceInfo device = connectedDevices.get(deviceAddress);
        if (device == null)
            return;

        UUID charUuid = characteristic.getUuid();

        // 心率数据
        if (charUuid.equals(HEART_RATE_MEASUREMENT_UUID)) {
            int heartRate = parseHeartRateData(characteristic);
            if (heartRate > 0) {
                device.sensorData.put("heartRate", heartRate);
                device.lastDataTime = System.currentTimeMillis();
                notifyHeartRateDataReceived(deviceAddress, heartRate);
            }
        }
        // 电池电量
        else if (charUuid.equals(BATTERY_LEVEL_UUID)) {
            int batteryLevel = characteristic.getValue()[0];
            device.batteryLevel = batteryLevel;
            device.sensorData.put("batteryLevel", batteryLevel);
            notifyBatteryLevelReceived(deviceAddress, batteryLevel);
        }
    }

    /**
     * 解析心率数据
     * 
     * @param characteristic 特征值
     * @return 心率值
     */
    private int parseHeartRateData(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        if (data == null || data.length == 0)
            return 0;

        int flag = data[0] & 0xFF;
        int format = (flag & 0x01) != 0 ? 2 : 1; // 1 = UINT8, 2 = UINT16

        if (data.length >= format + 1) {
            if (format == 1) {
                return data[1] & 0xFF;
            } else {
                return ((data[2] & 0xFF) << 8) | (data[1] & 0xFF);
            }
        }

        return 0;
    }

    /**
     * 开始定期读取RSSI
     * 
     * @param deviceAddress 设备地址
     */
    private void startRssiReading(final String deviceAddress) {
        final BluetoothGatt gatt = deviceGatts.get(deviceAddress);
        if (gatt == null)
            return;

        final Runnable rssiRunnable = new Runnable() {
            @Override
            public void run() {
                if (isDeviceConnected(deviceAddress)) {
                    gatt.readRemoteRssi();
                    mainHandler.postDelayed(this, 1000); // 每秒读取一次
                }
            }
        };

        mainHandler.post(rssiRunnable);
    }

    // 通知方法

    private void notifyScanStarted() {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onScanStarted();
        }
    }

    private void notifyScanStopped() {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onScanStopped();
        }
    }

    private void notifyScanFailed(String error) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onScanFailed(error);
        }
    }

    private void notifyDeviceDiscovered(DeviceInfo device) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onDeviceDiscovered(device);
        }
    }

    private void notifyDeviceConnected(DeviceInfo device) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onDeviceConnected(device);
        }
    }

    private void notifyDeviceDisconnected(DeviceInfo device) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onDeviceDisconnected(device);
        }
    }

    private void notifyDeviceConnectionFailed(DeviceInfo device, String error) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onDeviceConnectionFailed(device, error);
        }
    }

    private void notifyHeartRateDataReceived(String deviceId, int heartRate) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onHeartRateDataReceived(deviceId, heartRate);
        }
    }

    private void notifyBatteryLevelReceived(String deviceId, int batteryLevel) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onBatteryLevelReceived(deviceId, batteryLevel);
        }
    }

    private void notifyBluetoothStateChanged(boolean enabled) {
        for (BluetoothDeviceCallback callback : callbacks) {
            callback.onBluetoothStateChanged(enabled);
        }
    }

    // 扫描回调
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    DeviceInfo deviceInfo = new DeviceInfo(
                            device.getAddress(),
                            device.getName() != null ? device.getName() : "未知设备",
                            device.getAddress());

                    // 根据设备名称判断类型
                    String deviceName = device.getName();
                    if (deviceName != null) {
                        if (deviceName.toLowerCase().contains("polar") || deviceName.toLowerCase().contains("hrm")) {
                            deviceInfo.deviceType = DeviceType.HEART_RATE_MONITOR;
                        } else if (deviceName.toLowerCase().contains("speed")) {
                            deviceInfo.deviceType = DeviceType.SPEED_SENSOR;
                        } else if (deviceName.toLowerCase().contains("cadence")) {
                            deviceInfo.deviceType = DeviceType.CADENCE_SENSOR;
                        } else if (deviceName.toLowerCase().contains("power")) {
                            deviceInfo.deviceType = DeviceType.POWER_METER;
                        } else if (deviceName.toLowerCase().contains("row")) {
                            deviceInfo.deviceType = DeviceType.ROWING_MACHINE;
                        }
                    }

                    deviceInfo.sensorData.put("rssi", rssi);
                    deviceInfo.lastDataTime = System.currentTimeMillis();

                    notifyDeviceDiscovered(deviceInfo);
                }
            });
        }
    };

    /**
     * 清理资源
     */
    public void cleanup() {
        stopScan();
        disconnectAllDevices();
        callbacks.clear();
    }
}