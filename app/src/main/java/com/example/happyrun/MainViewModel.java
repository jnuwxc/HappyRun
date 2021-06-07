package com.example.happyrun;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.happyrun.model.User;
import com.example.happyrun.network.OkHttpEngine;
import com.example.happyrun.network.ResultCallback;
import com.example.happyrun.util.MyToast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

/**
 * @author wxc
 */
public class MainViewModel extends ViewModel {

    MutableLiveData<String> userId = new MutableLiveData<>();
    MutableLiveData<String> userName = new MutableLiveData<>();
    MutableLiveData<String> userMajor = new MutableLiveData<>();
    MutableLiveData<String> punchNum = new MutableLiveData<>();
    MutableLiveData<String> targetNum = new MutableLiveData<>();
    MutableLiveData<String> distance = new MutableLiveData<>();

    private static final String TAG = "MainViewModel";

    public MainViewModel(String userId, String userName, String userMajor) {
        this.userName.setValue(userName);
        this.userId.setValue(userId);
        this.userMajor.setValue(userMajor);
    }

    public void requestUserInfo() {
        OkHttpEngine okHttpEngine = OkHttpEngine.getInstance(MyApplication.context);
        okHttpEngine.getAsyncHttp(
                MyApplication.BASEURL + "getMainOther.php?userId="+userId.getValue(),
                new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d(TAG, "onError: 主页——查询用户打卡数据失败——网络请求出错");
            }

            @Override
            public void onResponse(String str) throws IOException {
                Log.d(TAG, "onResponse: " + str);
                if ("error".equals(str)) {
                    Log.d(TAG, "onResponse:主页——查询用户打卡数据——数据库失败");
                }
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(str);
                    punchNum.postValue(jsonObject.getString("punchNum"));
                    targetNum.postValue(jsonObject.getString("targetNum"));
                    distance.postValue(jsonObject.getString("totalDistance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
