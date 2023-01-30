package com.xy.wechatkeyboarddemo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘工具类
 */
public class SoftKeyBoardUtil {
    /**
     * 获取软键盘高度
     * @param context
     * @return
     */
    public static int getSoftKeyBoardHeight(Context context) {
        View decorView = ((Activity) context).getWindow().getDecorView();
        int screenHeight = decorView.getHeight();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom - ScreenUtils.INSTANCE.getNavigateBarHeight(context);
    }

    /**
     * 隐藏软键盘
     * @param context
     * @param token
     */
    public static void hideSoftInput(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && isSoftInputShown(context)) {
            imm.hideSoftInputFromWindow(token, 0);
        }
    }

    public static void showSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!isSoftInputShown(context)) {
            imm.toggleSoftInput(0, 0);
        }
    }

    /**
     * 是否打开软键盘
     * @param context
     * @return
     */
    public static boolean isSoftInputShown(Context context) {
        View decorView = ((Activity) context).getWindow().getDecorView();
        int screenHeight = decorView.getHeight();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom - ScreenUtils.INSTANCE.getNavigateBarHeight(context) > 0;
    }
}

