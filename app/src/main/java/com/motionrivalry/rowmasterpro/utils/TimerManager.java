package com.motionrivalry.rowmasterpro.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统一定时任务管理器
 * 用于替代分散的Timer创建，提供生命周期管理和内存泄漏防护
 */
public class TimerManager {

    private static final String TAG = "TimerManager";
    private static TimerManager instance;

    // 使用ScheduledExecutorService替代Timer，性能更好
    private final ScheduledExecutorService scheduler;
    private final Handler mainHandler;

    // 跟踪所有正在运行的任务
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final Map<String, Timer> timerTasks;

    /**
     * 获取单例实例
     */
    public static synchronized TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    /**
     * 私有构造函数
     */
    private TimerManager() {
        // 创建带有自定义线程工厂的调度器
        this.scheduler = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "TimerManager-" + threadNumber.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });

        this.mainHandler = new Handler(Looper.getMainLooper());
        this.scheduledTasks = new HashMap<>();
        this.timerTasks = new HashMap<>();
    }

    /**
     * 调度周期性任务（使用ScheduledExecutorService）
     * 
     * @param taskId       任务唯一标识
     * @param runnable     要执行的任务
     * @param initialDelay 初始延迟（毫秒）
     * @param period       周期（毫秒）
     */
    public void scheduleAtFixedRate(String taskId, Runnable runnable, long initialDelay, long period) {
        cancelTask(taskId); // 取消已存在的任务

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                runnable,
                initialDelay,
                period,
                TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
    }

    /**
     * 调度一次性任务
     * 
     * @param taskId   任务唯一标识
     * @param runnable 要执行的任务
     * @param delay    延迟（毫秒）
     */
    public void schedule(String taskId, Runnable runnable, long delay) {
        cancelTask(taskId); // 取消已存在的任务

        ScheduledFuture<?> future = scheduler.schedule(
                runnable,
                delay,
                TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
    }

    /**
     * 在主线程中调度任务
     * 
     * @param taskId   任务唯一标识
     * @param runnable 要执行的任务
     * @param delay    延迟（毫秒）
     */
    public void scheduleOnMainThread(String taskId, Runnable runnable, long delay) {
        cancelTask(taskId); // 取消已存在的任务

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            mainHandler.post(runnable);
        }, delay, TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
    }

    /**
     * 调度周期性任务（兼容原有Timer接口）
     * 
     * @param taskId    任务唯一标识
     * @param timerTask TimerTask
     * @param delay     延迟（毫秒）
     * @param period    周期（毫秒）
     */
    public void scheduleTimerTask(String taskId, TimerTask timerTask, long delay, long period) {
        cancelTimerTask(taskId); // 取消已存在的任务

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, period);
        timerTasks.put(taskId, timer);
    }

    /**
     * 调度一次性TimerTask
     * 
     * @param taskId    任务唯一标识
     * @param timerTask TimerTask
     * @param delay     延迟（毫秒）
     */
    public void scheduleTimerTask(String taskId, TimerTask timerTask, long delay) {
        cancelTimerTask(taskId); // 取消已存在的任务

        Timer timer = new Timer();
        timer.schedule(timerTask, delay);
        timerTasks.put(taskId, timer);
    }

    /**
     * 取消指定任务
     * 
     * @param taskId 任务唯一标识
     */
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }

        // 同时检查Timer任务
        cancelTimerTask(taskId);
    }

    /**
     * 取消Timer任务
     * 
     * @param taskId 任务唯一标识
     */
    private void cancelTimerTask(String taskId) {
        Timer timer = timerTasks.remove(taskId);
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 检查任务是否存在
     * 
     * @param taskId 任务唯一标识
     * @return 任务是否存在
     */
    public boolean hasTask(String taskId) {
        return scheduledTasks.containsKey(taskId) || timerTasks.containsKey(taskId);
    }

    /**
     * 取消所有任务
     */
    public void cancelAllTasks() {
        // 取消所有ScheduledExecutorService任务
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            future.cancel(false);
        }
        scheduledTasks.clear();

        // 取消所有Timer任务
        for (Timer timer : timerTasks.values()) {
            timer.cancel();
        }
        timerTasks.clear();
    }

    /**
     * 销毁管理器，释放所有资源
     */
    public void shutdown() {
        cancelAllTasks();
        scheduler.shutdown();

        if (instance != null) {
            instance = null;
        }
    }

    /**
     * 获取当前任务数量
     * 
     * @return 任务数量
     */
    public int getTaskCount() {
        return scheduledTasks.size() + timerTasks.size();
    }
}