package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.happyrun.databinding.ActivityPointSelectBinding;
import com.example.happyrun.util.StringUtil;
import com.example.happyrun.util.UIUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * 打卡终点选择对话框
 * @author wxc
 */
public class PointSelectActivity extends Activity {

    private ActivityPointSelectBinding binding;
    private static final String TAG = "PointSelectActivity";
    private int stopPoint;
    private int startPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取起始打卡点数据
        Intent intent = getIntent();
        LatLng startLatlng = new LatLng(intent.getFloatExtra("lat", 0),
                intent.getFloatExtra("lng", 0));

        // TODO: 2021/6/6 初始化打卡点数据
        RunPoint runPoint = RunPoint.getRunPoint();
        double[][] points = runPoint.getPoints();
        String[] pointInfo = runPoint.getPointInfo();
        int pointNum = pointInfo.length;
        // 各个打卡点距离现在位置的距离distance
        float[] distance = new float[pointNum];
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < pointNum; i++) {
            LatLng latLng = new LatLng(points[i][0], points[i][1]);
            distance[i] = (float) DistanceUtil.getDistance(startLatlng, latLng);
        }

        // TODO: 2021/6/6 设置listview和单选框
        // 是否默认选中一个
        boolean isOneChecked = false;
        for (int i = 0; i < pointNum; i++) {
            String info = pointInfo[i] + ": " + StringUtil.formatNum(distance[i] / 1000, 4) + " km";
            RadioButton radioButton = new RadioButton(this);
            // 起始打卡点和距离过近的打卡点无法设置为终点
            if (distance[i] < 50) {
                startPoint = i;
                info = info + "（起始打卡点）";
                radioButton.setVisibility(View.INVISIBLE);
            } else if (distance[i] < 1000) {
                info = info + "（距离过近）";
                radioButton.setVisibility(View.INVISIBLE);
            }else {
                if (!isOneChecked) {
                    isOneChecked = true;
                    radioButton.setChecked(true);
                    stopPoint = i;
                }
            }
            data.add(info);
            radioButton.setId(i);
            radioButton.setHeight((int)UIUtil.dp2px(50));
            binding.rgSelectPoint.addView(radioButton);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PointSelectActivity.this,
                android.R.layout.simple_list_item_1, data);
        binding.lvPoints.setAdapter(adapter);

        // TODO: 2021/6/6 单选框处理和跳转
        binding.rgSelectPoint.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                stopPoint = checkedId;
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + pointInfo[stopPoint]);
                // 开始跑步打卡
                Intent intent = new Intent(PointSelectActivity.this, RunningActivity.class);
                intent.putExtra("startPoint", startPoint);
                intent.putExtra("stopPoint", stopPoint);
                intent.putExtra("startLat", (float) startLatlng.latitude);
                intent.putExtra("startLng", (float) startLatlng.longitude);
                startActivity(intent);
                finish();
            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}