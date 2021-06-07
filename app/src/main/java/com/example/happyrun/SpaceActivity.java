package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.happyrun.databinding.ActivitySosBinding;
import com.example.happyrun.databinding.ActivitySpaceBinding;
import com.example.happyrun.network.OkHttpEngine;
import com.example.happyrun.network.ResultCallback;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Request;

public class SpaceActivity extends AppCompatActivity {

    private LineChart lineChart;
    private BarChart barChart;
    private ActivitySpaceBinding binding;
    private static final String TAG = "SpaceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置
        TextView tvSetting = findViewById(R.id.tvUserSpace);
        tvSetting.setText("设置");
        tvSetting.setTextSize(20);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpaceActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // 个人数据相关
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userMajor = intent.getStringExtra("userMajor");
        String userName = intent.getStringExtra("userName");
        String punchNum = intent.getStringExtra("punchNum");
        String targetNum = intent.getStringExtra("targetNum");
        String distance = intent.getStringExtra("distance");

        TextView tvUserName = findViewById(R.id.userName);
        TextView tvUserId = findViewById(R.id.userId);
        TextView tvUserMajor = findViewById(R.id.userMajor);
        TextView tvPunchNum = findViewById(R.id.punchNum);
        TextView tvTargetNum = findViewById(R.id.targetNum);
        TextView tvDistance = findViewById(R.id.totalDistance);

        tvUserName.setText(userName);
        tvUserId.setText(userId);
        tvUserMajor.setText(userMajor);
        tvPunchNum.setText(punchNum);
        tvTargetNum.setText(targetNum);
        tvDistance.setText(distance);


        // 体测数据相关
        String semester = "20212";
        OkHttpEngine okHttpEngine = OkHttpEngine.getInstance(this);
        String url = MyApplication.BASEURL + "getPhysical.php?userId=" + userId + "&semester=" + semester;
        Log.d(TAG, "onCreate: " + url);
        okHttpEngine.getAsyncHttp(url, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d(TAG, "onError: error");
            }

            @Override
            public void onResponse(String str) throws IOException {
                Log.d(TAG, "onResponse: " + str);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(str);
                    binding.tvScore.setText(jsonObject.getString("score") + "分");
                    binding.tvRunLong.setText(jsonObject.getString("scoreRunLong") + "秒");
                    binding.tvRun50.setText(jsonObject.getString("scoreRun50") + "秒");
                    binding.tvProject1.setText(jsonObject.getString("scoreProject1") + "个");
                    binding.tvProject2.setText(jsonObject.getString("scoreProject2") + "米");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        setBarChart();
        setLineChart();
    }

    private void setBarChart(){
        int[] dataX = {1, 2, 3, 4, 5, 6, 7};
        float[] dataY = {3.1f, 3.5f, 0, 2.9f, 4.5f, 3.4f, 0};
        List<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < dataX.length; i++){
            entries.add(new BarEntry(dataX[i], dataY[i]));
        }
        BarDataSet dataSet = new BarDataSet(entries, "跑步距离");
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.invalidate();
        barChart.getAxisRight().setEnabled(false);
    }

    private void setLineChart(){
        int[]  dataX = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
        int[] dataY2 = {2, 1, 0, 1, 1, 0, 1, 0, 2, 1, 1, 1, 1, 1, 2, 1, 0,
                1, 1, 0, 1, 0, 2, 1, 1, 1, 1, 1, 2, 0};
        int[] dataY1 = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1,
                1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0};

        List<Entry> entries1 = new ArrayList<>();
        for(int i = 0; i < dataX.length; i++){
            entries1.add(new BarEntry(dataX[i], dataY1[i]));
        }
        LineDataSet lineDataSet1 = new LineDataSet(entries1, "早卡次数");
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setCircleColor(Color.GREEN);
        // 设置数据显示为整型
        lineDataSet1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });

        List<Entry> entries2 = new ArrayList<>();
        for(int i = 0; i < dataX.length; i++){
            entries2.add(new BarEntry(dataX[i], dataY2[i]));
        }
        LineDataSet lineDataSet2 = new LineDataSet(entries2, "打卡次数");
        // 设置数据显示为整型
        lineDataSet2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });
        LineData lineData = new LineData(lineDataSet1, lineDataSet2);
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.getAxisRight().setEnabled(false);
        //去掉纵向网格线和顶部边线：
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        // 缩放，显示15天
        lineChart.invalidate();
        Matrix mMatrix=new Matrix();
        mMatrix.postScale(2f, 1f);
        lineChart.getViewPortHandler().refresh(mMatrix,lineChart , false);
        lineChart.animateY(800);
    }
    private int getDaysOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}