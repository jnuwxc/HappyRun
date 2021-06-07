package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SpaceActivity extends AppCompatActivity {

    private LineChart lineChart;
    private BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);

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