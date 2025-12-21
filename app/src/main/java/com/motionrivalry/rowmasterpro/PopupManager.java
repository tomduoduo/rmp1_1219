package com.motionrivalry.rowmasterpro;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.PopupWindow;

/**
 * PopupWindow管理器，统一管理应用中的所有PopupWindow
 * 提供一致的PopupWindow创建、显示和背景透明度控制功能
 */
public class PopupManager {
    private final Activity activity; // 上下文Activity
    private final int screenWidth; // 屏幕宽度
    private final int screenHeight; // 屏幕高度

    /**
     * 构造函数
     * 
     * @param activity 上下文Activity
     */
    public PopupManager(Activity activity) {
        this.activity = activity;
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
    }

    /**
     * 显示PopupWindow
     * 
     * @param contentView   PopupWindow的内容视图
     * @param widthPercent  宽度百分比（0表示MATCH_PARENT）
     * @param heightPercent 高度百分比（0表示MATCH_PARENT）
     * @param focusable     是否可获取焦点
     * @param listener      关闭监听器
     * @return 创建并显示的PopupWindow实例
     */
    public PopupWindow showPopup(View contentView, int widthPercent, int heightPercent, boolean focusable,
            PopupWindow.OnDismissListener listener) {
        // 根据百分比计算宽度，0表示MATCH_PARENT
        int w = (widthPercent > 0) ? screenWidth * widthPercent / 100 : WindowManager.LayoutParams.MATCH_PARENT;
        // 根据百分比计算高度，0表示MATCH_PARENT
        int h = (heightPercent > 0) ? screenHeight * heightPercent / 100 : WindowManager.LayoutParams.MATCH_PARENT;
        // 创建PopupWindow
        PopupWindow popup = new PopupWindow(contentView, w, h, focusable);
        // 获取父视图
        View parent = LayoutInflater.from(activity).inflate(R.layout.activity_speedometer, null);
        // 居中显示PopupWindow
        popup.showAtLocation(parent, Gravity.CENTER, 0, 0);
        // 设置关闭监听器
        if (listener != null)
            popup.setOnDismissListener(listener);
        return popup;
    }

    /**
     * 设置背景透明度
     * 
     * @param alpha 透明度值（0.0-1.0，0.0完全透明，1.0完全不透明）
     */
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        activity.getWindow().setAttributes(lp);
    }
}