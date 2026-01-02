package com.motionrivalry.rowmasterpro.sensor;

/**
 * 数据平滑器
 * 使用移动平均算法平滑数据，减少噪声
 */
public class DataSmoother {
    
    private static final String TAG = "DataSmoother";
    
    // 平滑窗口大小
    private final int windowSize;
    
    // 数据缓冲区（用于移动平均）
    private double[] dataBuffer;
    private int bufferIndex;
    private int bufferCount;
    
    // 多通道数据缓冲区
    private double[][] multiChannelBuffer;
    private int multiChannelBufferIndex;
    private int multiChannelBufferCount;
    
    /**
     * 构造函数（单通道）
     * 
     * @param windowSize 平滑窗口大小
     */
    public DataSmoother(int windowSize) {
        this.windowSize = windowSize;
        this.dataBuffer = new double[windowSize];
        this.bufferIndex = 0;
        this.bufferCount = 0;
    }
    
    /**
     * 构造函数（多通道）
     * 
     * @param windowSize 平滑窗口大小
     * @param channels 通道数量
     */
    public DataSmoother(int windowSize, int channels) {
        this.windowSize = windowSize;
        this.multiChannelBuffer = new double[channels][windowSize];
        this.multiChannelBufferIndex = 0;
        this.multiChannelBufferCount = 0;
    }
    
    /**
     * 平滑单通道数据（float）
     * 
     * @param input 输入数据
     * @return 平滑后的数据
     */
    public float smooth(float input) {
        return (float) smooth((double) input);
    }
    
    /**
     * 平滑单通道数据（double）
     * 
     * @param input 输入数据
     * @return 平滑后的数据
     */
    public double smooth(double input) {
        // 添加新数据到缓冲区
        dataBuffer[bufferIndex] = input;
        bufferIndex = (bufferIndex + 1) % windowSize;
        
        if (bufferCount < windowSize) {
            bufferCount++;
        }
        
        // 计算移动平均
        double sum = 0;
        for (int i = 0; i < bufferCount; i++) {
            sum += dataBuffer[i];
        }
        
        return sum / bufferCount;
    }
    
    /**
     * 平滑多通道数据（float数组）
     * 
     * @param input 输入数据数组
     * @return 平滑后的数据数组
     */
    public float[] smooth(float[] input) {
        if (multiChannelBuffer == null || input == null) {
            return input;
        }
        
        double[] inputDouble = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            inputDouble[i] = input[i];
        }
        
        double[] result = smooth(inputDouble);
        
        float[] output = new float[result.length];
        for (int i = 0; i < result.length; i++) {
            output[i] = (float) result[i];
        }
        
        return output;
    }
    
    /**
     * 平滑多通道数据（double数组）
     * 
     * @param input 输入数据数组
     * @return 平滑后的数据数组
     */
    public double[] smooth(double[] input) {
        if (multiChannelBuffer == null || input == null) {
            return input;
        }
        
        double[] output = new double[input.length];
        
        for (int channel = 0; channel < input.length && channel < multiChannelBuffer.length; channel++) {
            // 添加新数据到对应通道的缓冲区
            multiChannelBuffer[channel][multiChannelBufferIndex] = input[channel];
            
            // 计算该通道的移动平均
            double sum = 0;
            int count = Math.min(multiChannelBufferCount + 1, windowSize);
            for (int i = 0; i < count; i++) {
                sum += multiChannelBuffer[channel][i];
            }
            output[channel] = sum / count;
        }
        
        // 更新缓冲区索引
        multiChannelBufferIndex = (multiChannelBufferIndex + 1) % windowSize;
        if (multiChannelBufferCount < windowSize) {
            multiChannelBufferCount++;
        }
        
        return output;
    }
    
    /**
     * 获取平滑数据的方差
     * 
     * @return 方差
     */
    public double getVariance() {
        if (bufferCount == 0) {
            return 0;
        }
        
        // 计算平均值
        double sum = 0;
        for (int i = 0; i < bufferCount; i++) {
            sum += dataBuffer[i];
        }
        double mean = sum / bufferCount;
        
        // 计算方差
        double varianceSum = 0;
        for (int i = 0; i < bufferCount; i++) {
            double diff = dataBuffer[i] - mean;
            varianceSum += diff * diff;
        }
        
        return varianceSum / bufferCount;
    }
    
    /**
     * 获取平滑数据的标准差
     * 
     * @return 标准差
     */
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }
    
    /**
     * 获取当前平均值
     * 
     * @return 平均值
     */
    public double getCurrentAverage() {
        if (bufferCount == 0) {
            return 0;
        }
        
        double sum = 0;
        for (int i = 0; i < bufferCount; i++) {
            sum += dataBuffer[i];
        }
        return sum / bufferCount;
    }
    
    /**
     * 获取当前最大值
     * 
     * @return 最大值
     */
    public double getCurrentMax() {
        if (bufferCount == 0) {
            return Double.MIN_VALUE;
        }
        
        double max = dataBuffer[0];
        for (int i = 1; i < bufferCount; i++) {
            if (dataBuffer[i] > max) {
                max = dataBuffer[i];
            }
        }
        return max;
    }
    
    /**
     * 获取当前最小值
     * 
     * @return 最小值
     */
    public double getCurrentMin() {
        if (bufferCount == 0) {
            return Double.MAX_VALUE;
        }
        
        double min = dataBuffer[0];
        for (int i = 1; i < bufferCount; i++) {
            if (dataBuffer[i] < min) {
                min = dataBuffer[i];
            }
        }
        return min;
    }
    
    /**
     * 重置平滑器
     */
    public void reset() {
        bufferIndex = 0;
        bufferCount = 0;
        
        if (dataBuffer != null) {
            for (int i = 0; i < dataBuffer.length; i++) {
                dataBuffer[i] = 0;
            }
        }
        
        multiChannelBufferIndex = 0;
        multiChannelBufferCount = 0;
        
        if (multiChannelBuffer != null) {
            for (int i = 0; i < multiChannelBuffer.length; i++) {
                for (int j = 0; j < multiChannelBuffer[i].length; j++) {
                    multiChannelBuffer[i][j] = 0;
                }
            }
        }
    }
    
    /**
     * 获取窗口大小
     * 
     * @return 窗口大小
     */
    public int getWindowSize() {
        return windowSize;
    }
    
    /**
     * 获取当前缓冲区中的数据数量
     * 
     * @return 数据数量
     */
    public int getBufferCount() {
        return bufferCount;
    }
    
    /**
     * 检查是否有足够的数据进行平滑
     * 
     * @return true表示有足够数据
     */
    public boolean hasEnoughData() {
        return bufferCount >= windowSize / 2; // 至少一半窗口大小
    }
    
    @Override
    public String toString() {
        return String.format("DataSmoother{windowSize=%d, count=%d, avg=%.2f, std=%.2f}",
                windowSize, bufferCount, getCurrentAverage(), getStandardDeviation());
    }
}