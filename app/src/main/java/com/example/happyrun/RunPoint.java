package com.example.happyrun;

import com.baidu.mapapi.model.LatLng;

/**
 * @author wxc
 * 单例存储打卡点数据
 * 以下坐标分别对应：二食堂、体育馆、西北操场； 三食堂、四食堂、溪苑
 * double[][] points = {{31.495751, 120.272446}, {31.491771, 120.281973}, {31.492933, 120.27188},
 * {31.48368, 120.278721}, {31.482163, 120.281312}, {31.483334, 120.276821}};
 * 一些测试点：李园23号楼{120.276504, 31.496147}, 物联网学院{120.276884,31.48995}
 */
public class RunPoint {
    private static RunPoint runPoint;
//    private final double[][] points = {{31.183389, 121.567045}, {31.185335, 121.5610007}};
//    private final String[] pointInfo = {"测试打卡点1", "测试打卡点2"};

    private final double[][] points;
    private final String[] pointInfo;

    private RunPoint() {
        points = new double[][]{{31.495751, 120.272446}, {31.491771, 120.281973}, {31.496147,120.276504}
        ,{31.48368, 120.278721}, {31.482163, 120.281312}, {31.483334, 120.276821}};
        pointInfo = new String[]{"二食堂", "体育馆", "李园", "三食堂", "四食堂", "溪苑"};
    }
    public static synchronized RunPoint getRunPoint() {
        if (runPoint == null) {
            synchronized (RunPoint.class) {
                if (runPoint == null) {
                    return new RunPoint();
                }
            }
        }
        return runPoint;
    }

    protected double[][] getPoints() {
        return points;
    }

    protected String[] getPointInfo() {
        return pointInfo;
    }

    protected LatLng getPointLatlng(int i) {
        return new LatLng(points[i][0], points[i][1]);
    }
}
