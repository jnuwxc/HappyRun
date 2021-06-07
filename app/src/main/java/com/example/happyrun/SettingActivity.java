package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import com.example.happyrun.databinding.ActivitySettingBinding;
import com.example.happyrun.databinding.ActivitySpaceBinding;

/**
 * 设置
 * @author wxc
 */
public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * 将状态栏字体颜色设置为黑色
         * 关于WindowInsetsController，查看下面的链接
         * https://developer.android.com/reference/android/view/WindowInsetsController?hl=en
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            insetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }else{
            // API30以下
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // 返回键监听
        binding.ivBackup.setOnClickListener(v -> {
            finish();
        });

        // 退出登录
        binding.clExitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                        .putBoolean("isLogin", false)
                        .remove("account")
                        .apply();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}