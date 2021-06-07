package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.example.happyrun.databinding.ActivityRunResultBinding;
import com.example.happyrun.util.StringUtil;

import java.util.List;

public class RunResultActivity extends AppCompatActivity {

    private ActivityRunResultBinding binding;
    private static final String TAG = "RunResultActivity";
    private String entityName;
    private long startTime;
    private LBSTraceClient mTraceClient;
    private String distance;

    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRunResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        entityName = getSharedPreferences("login", Context.MODE_PRIVATE)
                .getString("account", "error");
        Intent intent = getIntent();
        startTime = intent.getLongExtra("startTime", System.currentTimeMillis() / 1000);
        binding.totalTime.setText(StringUtil.formatTime(System.currentTimeMillis() / 1000 - startTime));

        mapView = findViewById(R.id.resultMapView);
        baiduMap = mapView.getMap();
        initMap();

        queryTrace();

        binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String shareString = "分享跑步结果：总距离" + distance;
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareString);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
        binding.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void queryTrace() {
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());
        // 定位周期(单位:秒)
        int gatherInterval = 3;
        // 打包回传周期(单位:秒)
        int packInterval = 6;
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        // 请求标识
        int tag = 1;
        // 创建历史轨迹请求实例
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag,
                MyApplication.SERVICE_ID, entityName);

        //设置轨迹查询起止时间
        // 结束时间(单位：秒)
        long endTime = System.currentTimeMillis() / 1000;
        // 设置开始时间
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);

        // 初始化轨迹监听器
        OnTrackListener mTrackListener = new OnTrackListener() {
            // 历史轨迹回调
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                distance = StringUtil.formatDistance(response.getDistance() / 1000) + "千米";
                binding.totalDistance.setText(distance);
            }
        };
        // 查询历史轨迹
        mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
    }

    // 初始化地图
    private void initMap() {
        Log.d(TAG, "initMap: 初始化地图");
        //开启地图的定位图层
        baiduMap.setMyLocationEnabled(true);
        //设置缩放比列，17代表100米，18——50米，19——20米
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        // 定位为当前坐标
        mapLocation();
    }

    //定位
    private void mapLocation() {
        Log.d(TAG, "mapLocation: 定位");
        //设置本机定位点
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,
                true, BitmapDescriptorFactory.fromResource(R.drawable.my_location));
        baiduMap.setMyLocationConfiguration(myLocationConfiguration);

        //通过LocationClientOption设置LocationClient相关参数
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        // 高精度定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        locationClient.setLocOption(option);

        //注册LocationListener监听器
        RunResultActivity.MyLocationListener myLocationListener = new RunResultActivity.MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        locationClient.start();
    }

    // 位置监听
    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "onReceiveLocation: " + location);
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
        }
    }
}