package com.motionrivalry.rowmasterpro.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 数据记录器
 * 负责训练数据的CSV文件记录、管理和存储
 * 提供统一的接口处理数据记录功能
 */
public class DataRecorder {
    
    private static final String TAG = "DataRecorder";
    
    // 文件相关
    private FileWriter fileWriter;
    private CSVWriter csvWriter;
    private String currentFilePath;
    private String baseDirectory;
    
    // 格式化器
    private SimpleDateFormat dateTimeFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormat_1;
    
    // 状态
    private boolean isRecording;
    private long recordingStartTime;
    private int recordCount;
    
    /**
     * 构造函数
     * 
     * @param baseDirectory 基础目录路径
     */
    public DataRecorder(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.isRecording = false;
        this.recordCount = 0;
        
        // 初始化格式化器
        this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        this.decimalFormat = new DecimalFormat("0.0");
        this.decimalFormat_1 = new DecimalFormat("0");
    }
    
    /**
     * 开始数据记录
     * 
     * @param fileName 文件名（不包含扩展名）
     * @param headers CSV表头数组
     * @return true表示成功，false表示失败
     */
    public boolean startRecording(String fileName, String[] headers) {
        if (isRecording) {
            stopRecording();
        }
        
        try {
            // 创建基础目录
            createDirectory(baseDirectory);
            
            // 生成文件名
            String timestamp = dateTimeFormat.format(new Date()).replace(" ", "_").replace(":", "-");
            String fullFileName = fileName + "_" + timestamp + ".csv";
            currentFilePath = baseDirectory + File.separator + fullFileName;
            
            // 创建CSV写入器
            fileWriter = new FileWriter(currentFilePath);
            csvWriter = new CSVWriter(fileWriter,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.RFC4180_LINE_END);
            
            // 写入表头
            if (headers != null && headers.length > 0) {
                csvWriter.writeNext(headers);
            }
            
            // 更新状态
            isRecording = true;
            recordingStartTime = System.currentTimeMillis();
            recordCount = 0;
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            cleanup();
            return false;
        }
    }
    
    /**
     * 记录数据行
     * 
     * @param data 数据数组
     * @return true表示成功，false表示失败
     */
    public boolean recordData(String[] data) {
        if (!isRecording || csvWriter == null) {
            return false;
        }
        
        try {
            csvWriter.writeNext(data);
            recordCount++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 记录数据行（自动刷新）
     * 
     * @param data 数据数组
     * @return true表示成功，false表示失败
     */
    public boolean recordDataAndFlush(String[] data) {
        boolean result = recordData(data);
        if (result) {
            flush();
        }
        return result;
    }
    
    /**
     * 停止数据记录
     * 
     * @return 文件路径，失败时返回null
     */
    public String stopRecording() {
        if (!isRecording) {
            return null;
        }
        
        String filePath = currentFilePath;
        cleanup();
        return filePath;
    }
    
    /**
     * 读取CSV数据
     * 
     * @param filePath 文件路径
     * @return 数据列表，失败时返回null
     */
    public List<String[]> readCSVData(String filePath) {
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            List<String[]> data = reader.readAll();
            reader.close();
            
            // 移除表头
            if (!data.isEmpty()) {
                data.remove(0);
            }
            
            return data;
            
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 删除数据文件
     * 
     * @param filePath 文件路径
     * @return true表示成功，false表示失败
     */
    public boolean deleteDataFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 显示保存确认对话框
     * 
     * @param activity 当前Activity
     * @param currentFilePath 当前文件路径
     * @param onSaveListener 保存监听器
     * @param onDiscardListener 丢弃监听器
     */
    public void showSaveDialog(Activity activity, String currentFilePath, 
                              Runnable onSaveListener, Runnable onDiscardListener) {
        new AlertDialog.Builder(activity)
                .setTitle("你希望保存这次训练的数据吗?")
                .setMessage("未被保存的数据将被删除。")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onSaveListener != null) {
                            onSaveListener.run();
                        }
                    }
                })
                .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDataFile(currentFilePath);
                        if (onDiscardListener != null) {
                            onDiscardListener.run();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * 保存截图到相册
     * 
     * @param activity 当前Activity
     * @param bitmap 位图
     * @param fileName 文件名
     * @return true表示成功，false表示失败
     */
    public boolean saveScreenshotToGallery(Activity activity, Bitmap bitmap, String fileName) {
        try {
            // 获取DCIM目录
            File dcimDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "RowMasterPro");
            
            if (!dcimDir.exists()) {
                dcimDir.mkdirs();
            }
            
            // 创建文件
            if (fileName == null || fileName.isEmpty()) {
                fileName = System.currentTimeMillis() + ".jpg";
            }
            
            File file = new File(dcimDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            
            // 通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            activity.sendBroadcast(intent);
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 格式化时间戳
     * 
     * @param timestamp 时间戳
     * @return 格式化的时间字符串
     */
    public String formatDateTime(long timestamp) {
        return dateTimeFormat.format(new Date(timestamp));
    }
    
    /**
     * 格式化日期
     * 
     * @param timestamp 时间戳
     * @return 格式化的日期字符串
     */
    public String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
    
    /**
     * 格式化时间
     * 
     * @param timestamp 时间戳
     * @return 格式化的时间字符串
     */
    public String formatTime(long timestamp) {
        return timeFormat.format(new Date(timestamp));
    }
    
    /**
     * 格式化小数
     * 
     * @param value 数值
     * @param precision 精度（小数位数）
     * @return 格式化后的字符串
     */
    public String formatDecimal(double value, int precision) {
        switch (precision) {
            case 0:
                return decimalFormat_1.format(value);
            case 1:
                return decimalFormat.format(value);
            default:
                return String.format(Locale.getDefault(), "%." + precision + "f", value);
        }
    }
    
    /**
     * 获取记录统计信息
     * 
     * @return 统计信息字符串
     */
    public String getRecordingStats() {
        if (!isRecording) {
            return "未在记录状态";
        }
        
        long elapsedTime = System.currentTimeMillis() - recordingStartTime;
        return String.format("记录中 - 文件: %s, 时长: %d秒, 记录数: %d", 
                getFileName(), elapsedTime / 1000, recordCount);
    }
    
    /**
     * 获取当前文件名
     * 
     * @return 文件名
     */
    public String getFileName() {
        if (currentFilePath == null) {
            return "无";
        }
        return new File(currentFilePath).getName();
    }
    
    /**
     * 获取记录状态
     * 
     * @return true表示正在记录
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * 获取记录数量
     * 
     * @return 记录的数据行数
     */
    public int getRecordCount() {
        return recordCount;
    }
    
    /**
     * 获取记录开始时间
     * 
     * @return 开始时间戳
     */
    public long getRecordingStartTime() {
        return recordingStartTime;
    }
    
    /**
     * 刷新缓冲区
     */
    public void flush() {
        if (csvWriter != null) {
            try {
                csvWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 创建目录
     * 
     * @param path 目录路径
     * @return true表示成功，false表示失败
     */
    private boolean createDirectory(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                return dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 清理资源
     */
    private void cleanup() {
        try {
            if (csvWriter != null) {
                csvWriter.close();
                csvWriter = null;
            }
            if (fileWriter != null) {
                fileWriter.close();
                fileWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        isRecording = false;
        currentFilePath = null;
        recordCount = 0;
    }
}