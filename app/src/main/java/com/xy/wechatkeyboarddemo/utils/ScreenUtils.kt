package com.xy.wechatkeyboarddemo.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi

object ScreenUtils {
    /**
     * 返回包括虚拟键在内的总的屏幕高度
     * 即使虚拟按键显示着，也会加上虚拟按键的高度
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getTotalScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 返回屏幕的宽度
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 返回屏幕可用高度
     * 当显示了虚拟按键时，会自动减去虚拟按键高度
     */
    fun getAvailableScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 状态栏高度
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        return activity.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取虚拟按键的高度
     * 会根据当前是否有显示虚拟按键来返回相应的值
     * 即如果隐藏了虚拟按键，则返回零
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getVirtualBarHeightIfRoom(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val usableHeight = displayMetrics.heightPixels
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val realHeight = displayMetrics.heightPixels
        return realHeight - usableHeight
    }

    /**
     * 获取虚拟按键的高度，不论虚拟按键是否显示都会返回其固定高度
     */
    fun getVirtualBarHeight(activity: Activity): Int {
        val resourceId =
            activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return activity.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 标题栏高度，如果隐藏了标题栏则返回零
     */
    fun getTitleHeight(activity: Activity): Int {
        return activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    }

    /**
     * 获取除虚拟按键屏幕的尺寸
     */
    fun getDisplayMetrics(activity: Activity): DisplayMetrics {
        var mDisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        return mDisplayMetrics
    }

    /**
     * 获取原始的屏幕的尺寸
     */
    fun getReaDisplayMetrics(activity: Activity): DisplayMetrics {
        var mDisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(mDisplayMetrics)
        return mDisplayMetrics
    }
    /**
     * 隐藏状态栏
     */
    fun hideWindowStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    // 兼容有导航键的情况
     fun getNavigateBarHeight(context:Context): Int {
        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }
}