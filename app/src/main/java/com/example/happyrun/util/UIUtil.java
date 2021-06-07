package com.example.happyrun.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.happyrun.MyApplication;

/**
 * UI工具类，封装类UI操作中常用的一些方法
 * @author wxc
 * @date 2021.5.19
 */
public class UIUtil {

    /**
     * 获取资源对象
     */
    public static Resources getResources() {
        return MyApplication.context.getResources();
    }

    /**
     * dp to px
     * @param dp dp
     * @return px
     */
    public static int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * px to dp
     * @param px px
     * @return dp
     */
    public static float px2dp(int px) {
        float density = getResources().getDisplayMetrics().density;
        return px / density;
    }

    /**
     * 获取屏幕长宽，单位为px
     * @return int[] 屏幕长度，屏幕宽度
     */
    public static int[] getScreenWH(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        //这两个就是手机屏幕的屏幕分辨率，物理宽高值如1080*1920（ToolBar或ActionBar会占据一定空间，得到的heightPiexls会小一点）
        // 表示屏幕的像素宽度，单位是px（像素）
        int screenWidth = metrics.widthPixels;
        // 表示屏幕的像素高度，单位是px（像素）
        int screenHeight = metrics.heightPixels;
        return new int[]{screenWidth, screenHeight};
    }

    /**
     * 获取控件的尺寸
     * @param id 控件id
     * @return 尺寸
     */
    public static int getDimen(int id) {
        // 返回具体像素值
        return getResources().getDimensionPixelSize(id);
    }

    /**
     * 把自身从父View中移除
     * @param view 要移除的view
     */
    public static void removeSelfFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }
}
