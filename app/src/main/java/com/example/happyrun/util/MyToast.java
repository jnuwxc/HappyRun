package com.example.happyrun.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.example.happyrun.MyApplication;


/**
 * 全局使用Toast
 * @author wxc
 * @date 2021.5.17
 */
public class MyToast {
    private static Toast myToast;

    /**
     * 短时间Toast
     * @param text Toast的内容
     */
    public static void toast(String text){
        if(myToast != null){
            myToast.cancel();
        }
        myToast =  Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT);
        myToast.show();
    }

    public static void toast(Context context, String text) {
        if(myToast != null){
            myToast.cancel();
        }
        myToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        myToast.show();
    }

    /**
     * 长时间Toast
     * @param text 要吐司的内容
     */
    public static void toastLong(String text) {
        if(myToast != null){
            myToast.cancel();
        }
        myToast = Toast.makeText(MyApplication.context, text, Toast.LENGTH_LONG);
        myToast.show();
    }

    /**
     * 短时间居中显示Toast
     * 注意，在Build.VERSION_CODES#R之后，该方法居中无效，和默认toast效果一样。
     * @param text 要吐司的内容
     */
    public static void toastCenter(String text){
        if(myToast != null){
            myToast.cancel();
        }
        myToast =  Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT);
        myToast.setGravity(Gravity.CENTER, 0, 0);
        myToast.show();
    }
}
