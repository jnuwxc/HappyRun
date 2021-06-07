package com.example.happyrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.DistanceRequest;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.TransportMode;
import com.example.happyrun.databinding.ActivityRunningBinding;
import com.example.happyrun.util.MyToast;
import com.example.happyrun.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

public class RunningActivity extends AppCompatActivity {

    private static final String TAG = "RunningActivity";
    private ActivityRunningBinding binding;
    private boolean isStartTrace;
    private String entityName;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mTraceListener;
    private Trace mTrace;
    private long startTime;
    private Handler handler = new Handler();
    private LatLng nowLatlng;
    private TimeTrackRunnable timeTrackRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRunningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        // TODO: 2021/6/6 初始化界面数据
        Intent intent = getIntent();
        int stopPoint = intent.getIntExtra("stopPoint", -1);
        int startPoint = intent.getIntExtra("startPoint", -1);
        LatLng startLatlng = new LatLng(intent.getFloatExtra("lat", 0),
                intent.getFloatExtra("lng", 0));
        nowLatlng = startLatlng;
        RunPoint runPoint = RunPoint.getRunPoint();
        binding.tvDistance.setText("0.0");
        binding.tvSpeed.setText("0.0");
        binding.tvTime.setText("0.0");
        binding.tvStopPoint.setText(runPoint.getPointInfo()[stopPoint]);
        binding.ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSos = new Intent(RunningActivity.this, SosActivity.class);
                intentSos.putExtra("lat", (float) nowLatlng.latitude);
                intentSos.putExtra("lng", (float) nowLatlng.longitude);
                startActivity(intentSos);
            }
        });

        // TODO: 2021/6/6 结束跑步
        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng stopLatlng = runPoint.getPointLatlng(stopPoint);
                double distanceToStop = DistanceUtil.getDistance(nowLatlng, stopLatlng);
                Intent intentEnd = new Intent(RunningActivity.this, RunResultActivity.class);
                intentEnd.putExtra("startTime", startTime);
                startActivity(intentEnd);
//                if (distanceToStop < 50) {
//                    Intent intentEnd = new Intent(RunningActivity.this, RunResultActivity.class);
//                    intentEnd.putExtra("startTime", startTime);
//                    startActivity(intentEnd);
//                }else {
//                    AlertDialog alertDialog = new AlertDialog.Builder(RunningActivity.this)
//                            .setTitle("警告")
//                            .setMessage("你距离目标打卡点的距离为"
//                                    + StringUtil.formatDistance(distanceToStop / 1000) +
//                                    "km\n不符合打卡规则\n放弃将不计入成绩，返回可继续跑步")
//                            .setPositiveButton("继续跑步", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .setNegativeButton("结束", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    finish();
//                                }
//                            })
//                            .show();
//                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTrace();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTrace();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startTrace() {
        // TODO: 2021/6/6 鹰眼轨迹追踪开启
        // 主要包括 Trace、TraceClient、TraceLister
        // 设备标识，这里用用户账号作为标识
        entityName = getSharedPreferences("login", Context.MODE_PRIVATE)
                .getString("account", "error");
        // 初始化轨迹服务，服务ID、设备标识、是否需要图像存储功能
        mTrace = new Trace(MyApplication.SERVICE_ID, entityName, false);

        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());
        // 定位周期(单位:秒)
        int gatherInterval = 3;
        // 打包回传周期(单位:秒)
        int packInterval = 6;
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        // 初始化轨迹服务监听器
        mTraceListener = new OnTraceListener(){
            // 绑定服务回调
            @Override
            public void onBindServiceCallback(int i, String s) {
            }
            // 开启服务回调
            @Override
            public void onStartTraceCallback(int status, String message) {
                if (status == 0) {
                    Log.d(TAG, "onStartTraceCallback: 服务已开启");
                    isStartTrace = true;
                    startGather();
                }
            }
            // 停止服务回调
            @Override
            public void onStopTraceCallback(int status, String message) {}

            // 开启采集回调
            @Override
            public void onStartGatherCallback(int status, String message) {
                if (status == 0) {
                    Log.d(TAG, "onStartGatherCallback: 采集已开启");
                    timeTrackRunnable = new TimeTrackRunnable();
                    handler.postDelayed(timeTrackRunnable, 1000);
                }
            }
            // 停止采集回调
            @Override
            public void onStopGatherCallback(int status, String message) {}
            // 推送回调
            @Override
            public void onPushCallback(byte messageNo, PushMessage message) {}
            // 初始化bos功能回调
            @Override
            public void onInitBOSCallback(int i, String s) {
            }
        };
        // 开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
    }

    private void startGather() {
        if (isStartTrace) {
            mTraceClient.startGather(mTraceListener);
            // 开始时间(单位：秒)
            startTime = System.currentTimeMillis() / 1000;
        }else {
            MyToast.toast("服务尚未开启");
        }
    }

    private void stopTrace() {
        // 停止服务
        mTraceClient.stopTrace(mTrace, mTraceListener);
        handler.removeCallbacks(timeTrackRunnable);
        Log.d(TAG, "stopTrace: 服务已关闭");
    }

    private void queryTrace() {
        // 请求标识
        int tag = 1;
        // 创建历史轨迹请求实例
        DistanceRequest distanceRequest = new DistanceRequest(tag,
                MyApplication.SERVICE_ID, entityName);

        //设置轨迹查询起止时间
        // 结束时间(单位：秒)
        long endTime = System.currentTimeMillis() / 1000;
        // 设置开始时间
        distanceRequest.setStartTime(startTime);
        // 设置结束时间
        distanceRequest.setEndTime(endTime);

        // 初始化轨迹监听器
        OnTrackListener mTrackListener = new OnTrackListener() {
            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                Log.d(TAG, "" + distanceResponse);
                binding.tvDistance.setText(StringUtil.formatDistance
                        (distanceResponse.getDistance() / 1000));
            }
        };
        mTraceClient.queryDistance(distanceRequest, mTrackListener);
//        //鹰眼服务ID
//        long serviceId = MyApplication.SERVICE_ID;
//         //是否返回精简的结果（0 : 将只返回经纬度，1 : 将返回经纬度及其他属性信息）
//        int simpleReturn = 1;
//        //开始时间（Unix时间戳）
//        //结束时间（Unix时间戳）
//        int endTime = (int) (System.currentTimeMillis() / 1000);
//        //分页大小
//        int pageSize = 1000;
//        //分页索引
//        int pageIndex = 1;
//        //轨迹查询监听器
//        OnTrackListener trackListener = new OnTrackListener() {
//            //请求失败回调接口
//            @Override
//            public void onRequestFailedCallback(String arg0) {
//                System.out.println("track请求失败回调接口消息 : " + arg0);
//            }
//
//            // 查询历史轨迹回调接口
//            @Override
//            public void onQueryHistoryTrackCallback(String arg0) {
//                System.out.println("查询历史轨迹回调接口消息 : " + arg0);
//            }
//
//        };
//
//        //查询历史轨迹
//        queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime, pageSize,pageIndex,trackListener);
    }

    private void queryLocation() {
        //查询服务端纠偏后的最新轨迹点请求参数类
        LatestPointRequest request = new LatestPointRequest(1, MyApplication.SERVICE_ID, entityName);
        ProcessOption processOption = new ProcessOption();//纠偏选项
        processOption.setRadiusThreshold(50);//设置精度过滤，0为不需要；精度大于50米的位置点过滤掉
        processOption.setTransportMode(TransportMode.walking);
        processOption.setNeedDenoise(true);//去噪处理
        processOption.setNeedMapMatch(true);//绑路处理
        request.setProcessOption(processOption);//设置参数

        //请求纠偏后的最新点
        mTraceClient.queryLatestPoint(request, new OnTrackListener() {
            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                Log.d(TAG, "onLatestPointCallback: " + latestPointResponse);
                nowLatlng = new LatLng(latestPointResponse.getLatestPoint().getLocation().latitude
                        , latestPointResponse.getLatestPoint().getLocation().longitude);
                long time = System.currentTimeMillis() / 1000 - startTime;
                // 跑步时间大于20分钟
                if (time > 20 * 60) {
                    new AlertDialog.Builder(RunningActivity.this)
                            .setTitle("警告")
                            .setMessage("你本次跑步时间已经超过20分钟，成绩无效")
                            .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                binding.tvTime.setText(StringUtil.formatTime(System.currentTimeMillis() / 1000 - startTime));
                binding.tvSpeed.setText(StringUtil.formatNum(
                        (float) latestPointResponse.getLatestPoint().getSpeed(), 2));
            }
        });
    }



    // 每隔1秒查询一次位置
    class TimeTrackRunnable implements Runnable {
        @Override
        public void run() {
            queryLocation();
            queryTrace();
            handler.postDelayed(this, 1000);
        }
    }
}