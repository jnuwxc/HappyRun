package com.example.happyrun.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 字符串静态工具类，封装了字符串常用方法
 * @author wxc
 * @date 2021.5.18
 */
public class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str 要判断的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 格式化float为小数点后四位
     * @param aFloat
     * @return
     */
    public static String formatNum(float aFloat, int length) {
        if (length == 4) {
            DecimalFormat df = new DecimalFormat("#.####");
            return df.format(aFloat);
        } else if(length == 2){
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(aFloat);
        }else {
            DecimalFormat df = new DecimalFormat("#.#");
            return df.format(aFloat);
        }
    }

    /**
     * 格式化时间
     * @param time 单位s
     * @return
     */
    public static String formatTime(long time) {
        if (time <= 60) {
            return time + "s";
        } else if (time <= 3600) {
            return (time / 60) + "." + (time % 60);
        }else {
            return "errorTime";
        }
    }

    public static String formatDistance(double distance) {
        return formatNum((float) distance, 2);
    }
    /**
     * 罗马数字转换为整型数字
     * 注意罗马数字的书写规则以及特殊情况
     * @param s 要转换的罗马数字字符串
     * @return 转换完成的整型数字
     */
    public static int romanToInt(String s) {
        // 存放对应关系
        Map<Character, Integer> symbolValues = new HashMap<Character, Integer>() {{
            put('I', 1);
            put('V', 5);
            put('X', 10);
            put('L', 50);
            put('C', 100);
            put('D', 500);
            put('M', 1000);
        }};
        int ans = 0;
        int n = s.length();
        for (int i = 0; i < n; ++i) {
            int value = symbolValues.get(s.charAt(i));
            if (i < n - 1 && value < symbolValues.get(s.charAt(i + 1))) {
                ans -= value;
            } else {
                ans += value;
            }
        }
        return ans;
    }

    /**
     * 二进制字符串求和
     * 注意需要输入参数的长度均小于33位
     * @param a 二进制字符串a
     * @param b 二进制字符串b
     * @return 返回a和b的和，二进制形式
     */
    public static String addBinary(String a, String b) {
        if (a.length() > 32 || b.length() > 32) {
            return "Error, string's length > 32";
        }
        return Integer.toBinaryString(
                Integer.parseInt(a, 2) + Integer.parseInt(b, 2)
        );
    }

    /**
     * 将给定的字符串前面补0，使字符串的长度为num位。
     * @param str 要补全的字符串
     * @param num 要全部的位数
     * @return 补全后的字符串
     */
    public static String fillNum(int num,String str) {
        if(str.length() == num) {
            return str;
        } else {
            int fulNum = num-str.length();
            String tmpStr  =  "";
            for(int i = 0; i < fulNum ; i++){
                tmpStr += "0";
            }
            return tmpStr + str;
        }
    }

    /**
     * 验证字符串是否为全汉字组成
     * @param str 要验证的字符串
     * @return 是否为全汉字字符串
     */
    public static boolean isChineseString(String str){
        if(StringUtil.isEmpty(str)){
            return false;
        }else {
            return str.matches("^[\\u4e00-\\u9fa5]{0,}");
        }
    }

    /**
     * 验证一个字符串是否完全由纯数字组成的字符串，当字符串为空时也返回false.
     * @param str 要判断的字符串 .
     * @return true or false .
     */
    public static boolean isNumeric(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        } else {
            return str.matches("\\d*");
        }
    }
}
