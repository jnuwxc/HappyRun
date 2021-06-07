package com.example.happyrun;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.happyrun.databinding.ActivityLoginBinding;
import com.example.happyrun.model.User;
import com.example.happyrun.network.OkHttpEngine;
import com.example.happyrun.network.ResultCallback;
import com.example.happyrun.util.MyToast;
import com.example.happyrun.util.StringUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 登录
 * @author wxc
 * @date 2021.6.5
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private OkHttpEngine okHttpEngine;
    private boolean accountExists;
    private boolean passwordRight;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 隐藏actionBar
        getSupportActionBar().hide();

        // 启动activity时的动画，将人物图从左侧移动到屏幕中央
        ObjectAnimator.ofFloat(binding.runManImg, "translationX", getImgTranX())
                .setDuration(1000).start();
        binding.loginBtn.setOnClickListener(v -> {
            okHttpEngine = OkHttpEngine.getInstance(this);
            String account = binding.accountEdit.getText().toString().trim();
            String password = binding.passwordEdit.getText().toString().trim();
            Log.d(TAG, "onCreate: " + account + " " + password);
            if(passwordIsRight(account, password)){
                requestPermission();
                getSharedPreferences("login", Context.MODE_PRIVATE)
                        .edit()
                        .putString("account", account)
                        .putBoolean("isLogin", true)
                        .apply();
                // 存储用户数据
                getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                        .putString("userId", user.getUserId()+"")
                        .putString("userName", user.getUserName())
                        .putString("userGender", user.getUserGender())
                        .putString("userMajor", user.getUserMajor())
                        .putString("userPassword", user.getUserPassword())
                        .putString("userAvatar", user.getUserAvatar())
                        .putFloat("userHeight", user.getUserHeight())
                        .putFloat("userWeight", user.getUserWeight())
                        .putString("userSchool", user.getUserSchool())
                        .putString("semester", user.getSemester())
                        .apply();
            }
        });

    }

    /**
     * 获取图片位移的距离，单位为像素，效果为将图片从左/右侧未出现移动到屏幕中央。
     * @return 返回要移动的距离
     */
    private float getImgTranX(){
        //图片的宽度，单位dp。
        final int imgWidthDp = 250;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        //这两个就是手机屏幕的屏幕分辨率，物理宽高值如1080*1920（ToolBar或ActionBar会占据一定空间，得到的heightPiexls会小一点）
        // 表示屏幕的像素宽度，单位是px（像素）
        int screenWidth = metrics.widthPixels;
        // 表示屏幕的像素高度，单位是px（像素）
        //int screenHeight = metrics.heightPixels;
        // 获取dp与px的换算值
        float screenScale = metrics.density;
        return screenWidth / 2 + imgWidthDp * screenScale / 2 ;
    }

    /**
     * 向服务器请求，验证账号密码是否正确
     * @param account 账号
     * @param password 密码
     * @return 是否正确
     */
    private boolean passwordIsRight(String account, String password){
        if(accountInspect(account)){
            if(accountExistence(account)){
                String url = MyApplication.BASEURL + "userPassword.php?userId=" + account + "&userPassword=" + password;
                Log.d(TAG, "passwordIsRight: " + url);
                okHttpEngine.getAsyncHttp(url, new ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.toast("密码————其它错误");
                    }

                    @Override
                    public void onResponse(String str) throws IOException {
                        if ("error".equals(str)) {
                            MyToast.toast("密码错误");
                        }else {
                            Gson gson = new Gson();
                            user = gson.fromJson(str, User.class);
                            passwordRight = true;
                        }
                    }
                });
            }
        }
        return passwordRight;
    }

    /**
     * 判断账号是否合规
     * 这里主要检查学号是否为10位数字
     * @param account 账号
     * @return 是否合规
     */
    private boolean accountInspect(String account){
        return account.length() == 10 && StringUtil.isNumeric(account);
    }

    /**
     * 检查账号是否存在
     * @param account 账号
     * @return 是否存在
     */
    private boolean accountExistence(String account){
        String url = MyApplication.BASEURL + "userExists.php?userId=" + account;
        okHttpEngine.getAsyncHttp(url, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.toast("账号————其它错误");
            }

            @Override
            public void onResponse(String str) throws IOException {
                //返回值为该账号存在的记录个数
                switch (str) {
                    case "1":
                        accountExists= true;
                        break;
                    case "0":
                        MyToast.toast("账号————不存在");
                        break;
                    default:
                        MyToast.toast("账号————其它错误");
                        break;
                }
            }
        });
        return accountExists;
    }

    //申请危险权限
    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permission, 2);
        } else {
            // 申请成功
            Log.d(TAG, "requestPermission: 申请成功" );
            loginSuccess();
        }
    }

    //重写权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            MyToast.toast("同意权限才能运行");
                            finish();
                            return;
                        }
                    }
                    //申请成功
                    loginSuccess();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，无法运行", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loginSuccess() {
        // 播放结束动画
        ObjectAnimator endAnimator = ObjectAnimator.ofFloat(binding.runManImg, "translationX", getImgTranX() * 2);
        endAnimator.setDuration(1000).start();
        // 动画结束跳转
        endAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}