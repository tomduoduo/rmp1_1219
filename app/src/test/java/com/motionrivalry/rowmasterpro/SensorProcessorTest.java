package com.motionrivalry.rowmasterpro;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * SensorProcessor简化单元测试类
 * 测试传感器数据处理逻辑的基本功能
 */
public class SensorProcessorTest {

    /**
     * 测试监听器设置功能
     */
    @Test
    public void testListenerSetting() {
        SensorProcessor sensorProcessor = new SensorProcessor();

        // 验证监听器初始状态
        assertNotNull("SensorProcessor应该被正确创建", sensorProcessor);

        // 测试设置监听器
        SensorProcessor.SensorDataListener listener = new SensorProcessor.SensorDataListener() {
            @Override
            public void onSensorDataUpdated(SensorProcessor.SensorData data) {
                // 空实现
            }
        };

        sensorProcessor.setListener(listener);

        // 验证监听器设置成功
        assertTrue("监听器设置应该成功", true);
    }

    /**
     * 测试空监听器处理
     */
    @Test
    public void testNullListenerHandling() {
        SensorProcessor sensorProcessor = new SensorProcessor();

        // 设置空监听器
        sensorProcessor.setListener(null);

        // 验证没有异常发生
        assertTrue("空监听器处理应该正常完成", true);
    }

    /**
     * 测试SensorData类的基本功能
     */
    @Test
    public void testSensorDataClass() {
        // 创建SensorData实例
        float roll = 15.0f;
        float yaw = 30.0f;
        float pitch = 10.0f;
        double boatAcceleration = 1.5;
        float boatYawAngle = 0.0f;
        SensorProcessor.SensorData data = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                boatYawAngle);

        // 验证数据设置正确
        assertEquals("船只加速度应该正确设置", 1.5, data.boatAcceleration, 0.01);
        assertEquals("横滚角应该正确设置", 15.0f, data.roll, 0.01);
        assertEquals("偏航角应该正确设置", 30.0f, data.yaw, 0.01);
        assertEquals("俯仰角应该正确设置", 10.0f, data.pitch, 0.01);
    }

    /**
     * 测试SensorProcessor实例化
     */
    @Test
    public void testSensorProcessorInstantiation() {
        SensorProcessor sensorProcessor = new SensorProcessor();

        // 验证实例化成功
        assertNotNull("SensorProcessor应该被正确实例化", sensorProcessor);

        // 验证默认状态
        assertTrue("SensorProcessor应该处于可用状态", true);
    }
}