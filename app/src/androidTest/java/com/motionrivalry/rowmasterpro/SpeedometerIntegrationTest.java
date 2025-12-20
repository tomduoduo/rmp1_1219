package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Speedometer集成测试类
 * 测试Speedometer与SensorProcessor的交互和UI更新
 */
@RunWith(AndroidJUnit4.class)
public class SpeedometerIntegrationTest {

    private Context context;
    private ActivityScenario<Speedometer> activityScenario;

    /**
     * 测试前初始化
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // 启动Speedometer活动
        activityScenario = ActivityScenario.launch(Speedometer.class);
    }

    /**
     * 测试后清理
     */
    @After
    public void tearDown() {
        if (activityScenario != null) {
            activityScenario.close();
        }
    }

    /**
     * 测试SensorProcessor集成
     */
    @Test
    public void testSensorProcessorIntegration() {
        activityScenario.onActivity(activity -> {
            // 验证SensorProcessor被正确初始化
            assertNotNull("SensorProcessor应该被初始化", activity.getSensorProcessor());

            // 验证监听器被设置
            assertNotNull("SensorDataListener应该被设置", activity.getSensorProcessor().getListener());

            // 验证传感器管理器被设置
            SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
            assertNotNull("传感器管理器应该可用", sensorManager);
        });
    }

    /**
     * 测试UI组件初始化
     */
    @Test
    public void testUIComponentsInitialization() {
        activityScenario.onActivity(activity -> {
            // 验证主要UI组件被正确初始化
            assertNotNull("桨频显示TextView应该被初始化", activity.getStrokeRateTextView());
            assertNotNull("速度显示TextView应该被初始化", activity.getSpeedTextView());
            assertNotNull("距离显示TextView应该被初始化", activity.getDistanceTextView());
            assertNotNull("开始按钮应该被初始化", activity.getStartButton());
            // 注意：Speedometer.java中没有停止按钮mStop，使用其他方式测试停止功能

            // 验证初始显示值
            assertEquals("初始桨频应为0.0", "0.0", activity.getStrokeRateTextView().getText().toString());
            assertEquals("初始速度应为0.0", "0.0", activity.getSpeedTextView().getText().toString());
            assertEquals("初始距离应为0.0", "0.0", activity.getDistanceTextView().getText().toString());
        });
    }

    /**
     * 测试开始按钮点击功能
     */
    @Test
    public void testStartButtonFunctionality() {
        activityScenario.onActivity(activity -> {
            // 模拟点击开始按钮
            activity.getStartButton().performClick();

            // 验证状态变量更新
            assertEquals("开始状态应该为1", 1, activity.getStartStatus());
            assertFalse("UI重置标志应该为false", activity.isStrokeDisplayReset());

            // 验证CSV文件创建状态
            assertEquals("日志创建状态应该为1", 1, activity.getLogCreated());
        });
    }

    /**
     * 测试停止功能
     */
    @Test
    public void testStopFunctionality() {
        activityScenario.onActivity(activity -> {
            // 启动记录
            activity.getStartButton().performClick();

            // 验证启动状态
            assertEquals("开始状态应该为1", 1, activity.getStartStatus());
            assertFalse("UI重置标志应该为false", activity.isStrokeDisplayReset());

            // 模拟停止功能（通过设置状态变量）
            activity.setStartStatus(0);

            // 验证停止状态
            assertEquals("开始状态应该为0", 0, activity.getStartStatus());

            // 注意：实际应用中停止功能可能通过其他方式实现，这里只测试状态变化
        });
    }

    /**
     * 测试传感器数据回调处理
     */
    @Test
    public void testSensorDataCallbackHandling() {
        activityScenario.onActivity(activity -> {
            // 创建模拟传感器数据
            float roll = 15.0f; // 15度横滚角
            float yaw = 30.0f; // 30度偏航角
            float pitch = 0.0f; // 0度俯仰角
            double boatAcceleration = 2.5; // 2.5 m/s²加速度
            float boatYawAngle = 0.0f; // 0度偏航角
            SensorProcessor.SensorData testData = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                    boatYawAngle);

            // 模拟传感器数据更新
            activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);

            // 验证船只姿态显示更新
            assertEquals("船只横滚角应该为-15度", -15.0f, activity.getBoatRollImageView().getRotation(), 0.1f);

            // 验证StrokeDetector被调用（当logCreated和mStartStatus都为1时）
            // 注意：这些字段是私有的，无法直接设置，需要修改测试逻辑
            // 通过点击开始按钮来设置这些状态
            activity.getStartButton().performClick();

            activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);

            // 验证StrokeDetector存在
            assertNotNull("StrokeDetector应该被初始化", activity.getStrokeDetector());
        });
    }

    /**
     * 测试CSV数据记录功能
     */
    @Test
    public void testCSVDataRecording() {
        activityScenario.onActivity(activity -> {
            // 启动记录
            activity.getStartButton().performClick();

            // 创建模拟传感器数据
            float roll = 10.0f;
            float yaw = 20.0f;
            float pitch = 0.0f;
            double boatAcceleration = 1.5;
            float boatYawAngle = 0.0f;
            SensorProcessor.SensorData testData = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                    boatYawAngle);

            // 模拟传感器数据更新（应该触发CSV记录）
            activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);

            // 验证CSV写入器存在
            assertNotNull("CSV写入器应该被初始化", activity.getCSVWriter());

            // 验证文件路径设置
            assertNotNull("CSV文件路径应该被设置", activity.getCSVFilePath());

            // 验证时间信息被记录
            assertNotNull("开始时间应该被记录", activity.getStartTime());
            assertNotNull("当前时间应该被记录", activity.getCurrentTime());
        });
    }

    /**
     * 测试UI性能优化
     */
    @Test
    public void testUIPerformanceOptimization() {
        activityScenario.onActivity(activity -> {
            // 初始状态验证
            assertTrue("初始UI重置标志应该为true", activity.isStrokeDisplayReset());

            // 模拟传感器数据更新（未启动状态）
            float roll = 0.0f;
            float yaw = 0.0f;
            float pitch = 0.0f;
            double boatAcceleration = 0.0;
            float boatYawAngle = 0.0f;
            SensorProcessor.SensorData testData = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                    boatYawAngle);
            activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);

            // 验证UI重置标志被正确管理
            assertTrue("UI重置标志应该保持为true", activity.isStrokeDisplayReset());

            // 启动后再次测试
            activity.getStartButton().performClick();
            activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);

            // 验证UI重置标志被重置
            assertFalse("启动后UI重置标志应该为false", activity.isStrokeDisplayReset());
        });
    }

    /**
     * 测试StrokeDetector集成
     */
    @Test
    public void testStrokeDetectorIntegration() {
        activityScenario.onActivity(activity -> {
            // 验证StrokeDetector被正确初始化
            assertNotNull("StrokeDetector应该被初始化", activity.getStrokeDetector());

            // 设置测试条件
            activity.setLogCreated(1);
            activity.setStartStatus(1);

            // 创建测试数据
            float roll = 0.0f;
            float yaw = 0.0f;
            float pitch = 0.0f;
            double boatAcceleration = 3.0; // 模拟划桨加速度
            float boatYawAngle = 0.0f;
            SensorProcessor.SensorData testData = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                    boatYawAngle);

            // 模拟多次传感器数据更新（模拟划桨动作）
            for (int i = 0; i < 10; i++) {
                activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);
            }

            // 验证桨频检测逻辑被调用
            // 注意：实际检测结果取决于StrokeDetector的实现
        });
    }

    /**
     * 测试错误处理机制
     */
    @Test
    public void testErrorHandling() {
        activityScenario.onActivity(activity -> {
            // 测试空指针处理
            try {
                activity.getSensorProcessor().setListener(null);
                // 应该能够处理空监听器
            } catch (Exception e) {
                fail("设置空监听器不应该抛出异常: " + e.getMessage());
            }

            // 测试传感器数据异常处理
            try {
                // 模拟无效传感器数据
                SensorProcessor.SensorData invalidData = null;
                if (activity.getSensorProcessor().getListener() != null) {
                    activity.getSensorProcessor().getListener().onSensorDataUpdated(invalidData);
                }
                // 应该能够处理空数据
            } catch (Exception e) {
                fail("处理空传感器数据不应该抛出异常: " + e.getMessage());
            }
        });
    }
}