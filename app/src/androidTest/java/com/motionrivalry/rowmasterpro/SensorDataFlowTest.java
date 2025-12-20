package com.motionrivalry.rowmasterpro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Environment;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import static org.junit.Assert.*;

/**
 * 传感器数据流功能测试类
 * 测试完整的传感器数据处理、CSV记录和UI更新流程
 */
@RunWith(AndroidJUnit4.class)
public class SensorDataFlowTest {

    private Context context;
    private ActivityScenario<Speedometer> activityScenario;
    private SensorManager sensorManager;

    /**
     * 测试前初始化
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

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

        // 清理测试文件
        cleanupTestFiles();
    }

    /**
     * 测试完整的传感器数据流
     */
    @Test
    public void testCompleteSensorDataFlow() {
        activityScenario.onActivity(activity -> {
            // 1. 验证传感器注册
            assertTrue("传感器应该被注册", activity.getSensorProcessor().areSensorsRegistered());

            // 2. 启动数据记录
            activity.getStartButton().performClick();
            assertEquals("开始状态应该为1", 1, activity.getStartStatus());
            assertEquals("日志创建状态应该为1", 1, activity.getLogCreated());

            // 3. 模拟传感器数据输入
            simulateSensorData(activity);

            // 4. 验证数据处理结果
            verifyDataProcessing(activity);

            // 5. 验证CSV数据记录
            verifyCSVDataRecording(activity);

            // 6. 验证UI更新
            verifyUIUpdates(activity);

            // 7. 停止记录（通过设置状态变量模拟）
            activity.setStartStatus(0);
            assertEquals("开始状态应该为0", 0, activity.getStartStatus());
        });
    }

    /**
     * 测试传感器数据准确性
     */
    @Test
    public void testSensorDataAccuracy() {
        activityScenario.onActivity(activity -> {
            // 启动记录
            activity.getStartButton().performClick();

            // 模拟精确的传感器数据
            float[] accelerometerData = { 0.0f, 0.0f, 9.81f }; // 标准重力加速度
            float[] magneticData = { 50.0f, 0.0f, 0.0f }; // 标准磁力数据
            float[] linearAcceleration = { 1.0f, 0.0f, 0.0f }; // X轴线性加速度

            // 处理传感器数据
            processSensorData(activity, Sensor.TYPE_ACCELEROMETER, accelerometerData);
            processSensorData(activity, Sensor.TYPE_MAGNETIC_FIELD, magneticData);
            processSensorData(activity, Sensor.TYPE_LINEAR_ACCELERATION, linearAcceleration);

            // 验证数据准确性
            float[] storedAccel = activity.getSensorProcessor().getAccelerometerValues();
            assertArrayEquals("加速度数据应该准确存储", accelerometerData, storedAccel, 0.01f);

            float[] storedMagnetic = activity.getSensorProcessor().getMagneticFieldValues();
            assertArrayEquals("磁力数据应该准确存储", magneticData, storedMagnetic, 0.01f);

            float[] storedLinear = activity.getSensorProcessor().getAccelerometerLinearValues();
            assertArrayEquals("线性加速度数据应该准确存储", linearAcceleration, storedLinear, 0.01f);
        });
    }

    /**
     * 测试船只姿态计算准确性
     */
    @Test
    public void testBoatAttitudeCalculationAccuracy() {
        activityScenario.onActivity(activity -> {
            // 设置测试数据：水平放置的设备
            float[] accelerometerData = { 0.0f, 0.0f, 9.81f }; // 设备水平，重力在Z轴
            float[] magneticData = { 50.0f, 0.0f, 0.0f }; // 磁力指向X轴

            // 处理传感器数据
            processSensorData(activity, Sensor.TYPE_ACCELEROMETER, accelerometerData);
            processSensorData(activity, Sensor.TYPE_MAGNETIC_FIELD, magneticData);

            // 计算船只姿态
            activity.getSensorProcessor().triggerCalculation();

            // 验证姿态计算结果
            // 水平放置的设备应该接近0度横滚和俯仰
            SensorProcessor.SensorData data = getLatestSensorData(activity);
            assertNotNull("应该生成传感器数据", data);

            // 允许一定的误差范围
            assertEquals("水平设备的横滚角应该接近0度", 0.0f, data.roll, 5.0f);
            assertEquals("水平设备的偏航角应该合理", 0.0f, data.yaw, 10.0f);
        });
    }

    /**
     * 测试CSV文件格式和内容
     */
    @Test
    public void testCSVFileFormatAndContent() {
        activityScenario.onActivity(activity -> {
            // 启动记录
            activity.getStartButton().performClick();

            // 模拟传感器数据
            simulateSensorData(activity);

            // 等待数据写入
            try {
                Thread.sleep(100); // 给文件写入一些时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 验证CSV文件存在
            File csvFile = new File(activity.getCSVFilePath());
            assertTrue("CSV文件应该存在", csvFile.exists());
            assertTrue("CSV文件应该可读", csvFile.canRead());

            // 验证文件内容格式
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String header = reader.readLine();
                assertNotNull("CSV文件应该有表头", header);

                // 验证表头包含必要的字段
                assertTrue("表头应包含时间字段", header.contains("时间"));
                assertTrue("表头应包含距离字段", header.contains("距离"));
                assertTrue("表头应包含桨频字段", header.contains("桨频"));
                assertTrue("表头应包含速度字段", header.contains("速度"));
                assertTrue("表头应包含加速度字段", header.contains("加速度"));
                assertTrue("表头应包含横滚角字段", header.contains("横滚角"));
                assertTrue("表头应包含偏航角字段", header.contains("偏航角"));

                // 验证数据行
                String dataLine = reader.readLine();
                assertNotNull("CSV文件应该有数据行", dataLine);

                // 验证数据格式（逗号分隔）
                String[] dataFields = dataLine.split(",");
                assertTrue("数据行应该有足够的字段", dataFields.length >= 7);

            } catch (Exception e) {
                fail("读取CSV文件失败: " + e.getMessage());
            }
        });
    }

    /**
     * 测试UI更新性能
     */
    @Test
    public void testUIPerformance() {
        activityScenario.onActivity(activity -> {
            long startTime = System.currentTimeMillis();

            // 模拟大量传感器数据更新
            for (int i = 0; i < 100; i++) {
                float roll = (float) (Math.random() * 30 - 15); // -15到15度
                float yaw = (float) (Math.random() * 360); // 0到360度
                float pitch = 0.0f; // 默认俯仰角
                double boatAcceleration = Math.random() * 5; // 0到5 m/s²
                float boatYawAngle = 0.0f; // 默认偏航角
                SensorProcessor.SensorData testData = new SensorProcessor.SensorData(roll, yaw, pitch, boatAcceleration,
                        boatYawAngle);

                activity.getSensorProcessor().getListener().onSensorDataUpdated(testData);
            }

            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            // 验证处理时间在合理范围内（100次更新应该在几秒内完成）
            assertTrue("100次传感器更新应该在5秒内完成", processingTime < 5000);

            // 验证UI没有卡顿（通过检查UI组件是否正常响应）
            assertNotNull("桨频显示应该正常", activity.getStrokeRateTextView().getText());
            assertNotNull("速度显示应该正常", activity.getSpeedTextView().getText());
        });
    }

    /**
     * 模拟传感器数据输入
     */
    private void simulateSensorData(Speedometer activity) {
        // 模拟加速度计数据
        processSensorData(activity, Sensor.TYPE_ACCELEROMETER,
                new float[] { 0.1f, -0.2f, 9.8f });

        // 模拟磁力计数据
        processSensorData(activity, Sensor.TYPE_MAGNETIC_FIELD,
                new float[] { 25.0f, 5.0f, -10.0f });

        // 模拟线性加速度数据
        processSensorData(activity, Sensor.TYPE_LINEAR_ACCELERATION,
                new float[] { 0.5f, -0.3f, 0.1f });
    }

    /**
     * 处理传感器数据
     */
    private void processSensorData(Speedometer activity, int sensorType, float[] values) {
        SensorEvent event = createSensorEvent(sensorType, values);
        activity.getSensorProcessor().onSensorChanged(event);
        activity.getSensorProcessor().triggerCalculation();
    }

    /**
     * 验证数据处理结果
     */
    private void verifyDataProcessing(Speedometer activity) {
        // 验证传感器数据被正确存储
        assertNotNull("加速度数据应该被存储", activity.getSensorProcessor().getAccelerometerValues());
        assertNotNull("磁力数据应该被存储", activity.getSensorProcessor().getMagneticFieldValues());
        assertNotNull("线性加速度数据应该被存储", activity.getSensorProcessor().getAccelerometerLinearValues());

        // 验证船只姿态计算
        SensorProcessor.SensorData data = getLatestSensorData(activity);
        assertNotNull("应该生成传感器数据对象", data);

        // 验证数据范围合理
        assertTrue("横滚角应该在合理范围内", Math.abs(data.roll) <= 180.0f);
        assertTrue("偏航角应该在合理范围内", Math.abs(data.yaw) <= 360.0f);
        assertTrue("加速度应该在合理范围内", Math.abs(data.boatAcceleration) < 20.0);
    }

    /**
     * 验证CSV数据记录
     */
    private void verifyCSVDataRecording(Speedometer activity) {
        // 验证CSV写入器存在
        assertNotNull("CSV写入器应该被初始化", activity.getCSVWriter());

        // 验证文件路径设置
        assertNotNull("CSV文件路径应该被设置", activity.getCSVFilePath());
        assertTrue("文件路径应该有效", activity.getCSVFilePath().endsWith(".csv"));

        // 验证时间信息被记录
        assertNotNull("开始时间应该被记录", activity.getStartTime());
        assertNotNull("当前时间应该被记录", activity.getCurrentTime());
    }

    /**
     * 验证UI更新
     */
    private void verifyUIUpdates(Speedometer activity) {
        // 验证UI组件响应
        assertNotNull("桨频显示应该更新", activity.getStrokeRateTextView().getText());
        assertNotNull("速度显示应该更新", activity.getSpeedTextView().getText());
        assertNotNull("距离显示应该更新", activity.getDistanceTextView().getText());

        // 验证船只姿态显示
        assertNotNull("船只横滚显示应该存在", activity.getBoatRollImageView());
        // 注意：Speedometer.java中没有boatYaw字段，移除相关验证
    }

    /**
     * 创建模拟传感器事件
     */
    private SensorEvent createSensorEvent(int sensorType, float[] values) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        // 使用反射创建SensorEvent（因为构造函数是包级私有的）
        try {
            SensorEvent event = SensorEvent.class.newInstance();

            java.lang.reflect.Field sensorField = SensorEvent.class.getDeclaredField("sensor");
            sensorField.setAccessible(true);
            sensorField.set(event, sensor);

            java.lang.reflect.Field valuesField = SensorEvent.class.getDeclaredField("values");
            valuesField.setAccessible(true);
            valuesField.set(event, values);

            java.lang.reflect.Field timestampField = SensorEvent.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(event, System.nanoTime());

            return event;

        } catch (Exception e) {
            throw new RuntimeException("创建SensorEvent失败", e);
        }
    }

    /**
     * 获取最新的传感器数据（通过模拟回调）
     */
    private SensorProcessor.SensorData getLatestSensorData(Speedometer activity) {
        final SensorProcessor.SensorData[] latestData = { null };

        // 设置临时监听器来捕获数据
        SensorProcessor.SensorDataListener tempListener = new SensorProcessor.SensorDataListener() {
            @Override
            public void onSensorDataUpdated(SensorProcessor.SensorData data) {
                latestData[0] = data;
            }
        };

        activity.getSensorProcessor().setListener(tempListener);
        activity.getSensorProcessor().triggerCalculation();

        // 恢复原始监听器
        activity.getSensorProcessor().setListener(activity.getSensorProcessor().getListener());

        return latestData[0];
    }

    /**
     * 清理测试文件
     */
    private void cleanupTestFiles() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] csvFiles = downloadsDir.listFiles((dir, name) -> name.startsWith("row_data_") && name.endsWith(".csv"));

        if (csvFiles != null) {
            for (File file : csvFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}