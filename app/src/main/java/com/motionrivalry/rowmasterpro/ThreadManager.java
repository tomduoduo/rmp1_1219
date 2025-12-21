package com.motionrivalry.rowmasterpro;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程管理器 - 统一管理后台任务和UI更新
 */
public class ThreadManager {
    private final Handler mainHandler;
    private final ExecutorService backgroundExecutor;
    private final Map<String, Runnable> repeatTasks = new HashMap<>();

    public ThreadManager() {
        mainHandler = new Handler(Looper.getMainLooper());
        backgroundExecutor = Executors.newSingleThreadExecutor();
    }

    /** 在主线程执行任务 */
    public void runOnUiThread(Runnable task) {
        mainHandler.post(task);
    }

    /** 在主线程延迟执行任务 */
    public void runOnUiThreadDelayed(Runnable task, long delayMillis) {
        mainHandler.postDelayed(task, delayMillis);
    }

    /** 在后台线程执行任务 */
    public void runInBackground(Runnable task) {
        backgroundExecutor.execute(task);
    }

    /** 定期在主线程执行任务（替代Timer），返回任务ID用于取消 */
    public String scheduleRepeating(Runnable task, long intervalMillis) {
        String taskId = "task_" + System.currentTimeMillis();
        Runnable repeatingTask = new Runnable() {
            @Override
            public void run() {
                task.run();
                mainHandler.postDelayed(this, intervalMillis);
            }
        };
        repeatTasks.put(taskId, repeatingTask);
        mainHandler.post(repeatingTask);
        return taskId;
    }

    /** 取消指定任务 */
    public void cancelTask(String taskId) {
        Runnable task = repeatTasks.remove(taskId);
        if (task != null) {
            mainHandler.removeCallbacks(task);
        }
    }

    /** 取消所有任务 */
    public void cancelAll() {
        mainHandler.removeCallbacksAndMessages(null);
        repeatTasks.clear();
    }

    /** 关闭线程池 */
    public void shutdown() {
        cancelAll();
        backgroundExecutor.shutdown();
    }
}