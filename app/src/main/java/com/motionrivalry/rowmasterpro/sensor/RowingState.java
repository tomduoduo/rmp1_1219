package com.motionrivalry.rowmasterpro.sensor;

/**
 * 划船状态枚举
 * 定义划船过程中的各种状态
 */
public enum RowingState {
    /**
     * 空闲状态 - 未开始训练
     */
    IDLE,
    
    /**
     * 准备状态 - 准备开始训练
     */
    READY,
    
    /**
     * 划船状态 - 正在划船
     */
    ROWING,
    
    /**
     * 暂停状态 - 训练暂停
     */
    PAUSED,
    
    /**
     * 完成状态 - 训练完成
     */
    COMPLETED,
    
    /**
     * 错误状态 - 发生错误
     */
    ERROR
}