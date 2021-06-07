package com.example.happyrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.example.happyrun.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 主页
 * @author wxc
 * @date 2021.5.1
 */
public class MainActivity extends AppCompatActivity {

    private BaiduMap baiduMap;
    private MapView mapView;
    private LocationClient locationClient;
    private static final String TAG = "MainActivity";
    private Button startRun;
    private LatLng myLatLng;
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private TextView userName;
    private TextView userId;
    private TextView userMajor;
    private TextView punchNum;
    private TextView targetNum;
    private TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: 2021/6/5 UI绑定及点击事件
        userName = findViewById(R.id.userName);
        userId = findViewById(R.id.userId);
        userMajor = findViewById(R.id.userMajor);
        punchNum = findViewById(R.id.punchNum);
        targetNum = findViewById(R.id.targetNum);
        distance = findViewById(R.id.totalDistance);
        mapView = findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        // 个人空间
        TextView tvUserSpace = findViewById(R.id.tvUserSpace);
        tvUserSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SpaceActivity.class);
                intent.putExtra("userId", userId.getText().toString());
                intent.putExtra("userName", userName.getText().toString());
                intent.putExtra("userMajor", userMajor.getText().toString());
                intent.putExtra("punchNum", punchNum.getText().toString());
                intent.putExtra("targetNum", targetNum.getText().toString());
                intent.putExtra("distance", distance.getText().toString());
                startActivity(intent);
            }
        });
//        binding.btnReLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mapLocation();
//            }
//        });

        // TODO: 2021/6/5 判断登录情况
        SharedPreferences loginSp = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean isLogin = loginSp.getBoolean("isLogin", false);
        if(isLogin){
            initMap();
            // 用户信息相关
            Toast.makeText(this, "欢迎用户: " + loginSp.getString("account", "error"), Toast.LENGTH_SHORT).show();
            SharedPreferences userSp = getSharedPreferences("user", Context.MODE_PRIVATE);
            mainViewModel = new ViewModelProvider(this, new MainViewModelFactory(
                    userSp.getString("userId","errorUserId"),
                    userSp.getString("userName", "error"),
                    userSp.getString("userMajor","error")))
                    .get(MainViewModel.class);
            mainViewModel.requestUserInfo();
            // 观察vm数据
            mainViewModel.userId.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String str) {
                    userId.setText(str);
                }
            });
            mainViewModel.userName.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    userName.setText(s);
                }
            });
            mainViewModel.userMajor.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    userMajor.setText(s);
                }
            });
            mainViewModel.punchNum.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String str) {
                    punchNum.setText(str);
                }
            });
            mainViewModel.targetNum.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String str) {
                    targetNum.setText(str);
                }
            });
            mainViewModel.distance.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String str) {
                    distance.setText(str);
                }
            });
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // TODO: 2021/6/5 开始跑步
        startRun = findViewById(R.id.startRun);
        startRun.setOnClickListener(it -> {
            if (myLatLng == null) {
                Toast.makeText(this, "未定位到你当前的位置", Toast.LENGTH_LONG).show();
            }else {
                // 判断是否在打卡点内
                RunPoint runPoint = RunPoint.getRunPoint();
                double[][] points = runPoint.getPoints();
                String[] pointInfo = runPoint.getPointInfo();
                int i;
                for (i = 0; i < pointInfo.length; i++) {
                    if (SpatialRelationUtil.isCircleContainsPoint(new LatLng(points[i][0], points[i][1]), 70, myLatLng)) {
                        Intent intent = new Intent(this, PointSelectActivity.class);
                        intent.putExtra("lat", (float) myLatLng.latitude);
                        intent.putExtra("lng", (float) myLatLng.longitude);
                        startActivity(intent);
                        break;
                    }
                }
                if (i >= pointInfo.length) {
                    Toast.makeText(this, "你未在任何一个打卡点的范围内！无法开始跑步", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
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
        setMapPoint();
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
        MyLocationListener myLocationListener = new MyLocationListener();
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
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            baiduMap.setMyLocationData(locData);
        }
    }

    //设置打卡点
    private void setMapPoint() {
        RunPoint runPoint = RunPoint.getRunPoint();
        double[][] points = runPoint.getPoints();
        for (int i = 0; i < points.length; i++) {
            //定义Maker坐标点
            LatLng point = new LatLng(points[i][0], points[i][1]);
//            //构建Marker图标
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.my_location);
//            //构建MarkerOption，用于在地图上添加Marker
//            OverlayOptions option = new MarkerOptions()
//                    .position(point)
//                    .icon(bitmap);
//            //在地图上添加Marker，并显示

            //绘制每个点的打卡范围
            //构造CircleOptions对象
            CircleOptions mCircleOptions = new CircleOptions().center(point)
                    .radius(50)
                    .fillColor(0x55B3F7C0)
                    .stroke(new Stroke(1, 0xAA00ff00)); //边框宽和边框颜色

            //在地图上显示圆
            Overlay mCircle = baiduMap.addOverlay(mCircleOptions);
//            baiduMap.addOverlay(option);
        }


    }

    /**
     * VM工厂类，用于向VM中传递数据
     */
    class MainViewModelFactory implements ViewModelProvider.Factory {

        private String userId;
        private String userName;
        private String userMajor;

        public MainViewModelFactory(String userId, String userName, String userMajor) {
            this.userId = userId;
            this.userName = userName;
            this.userMajor = userMajor;
        }

        @NonNull
        @NotNull
        @Override
        public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
            return (T) new MainViewModel(userId,userName, userMajor);
        }
    }
}