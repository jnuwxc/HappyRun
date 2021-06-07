
package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.happyrun.databinding.ActivitySosBinding;
import com.example.happyrun.network.OkHttpEngine;
import com.example.happyrun.network.ResultCallback;
import com.example.happyrun.util.MyToast;
import com.example.happyrun.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Request;

public class SosActivity extends Activity {

    private ArrayList<String> sosPhonesInfo;
    private ArrayList<String> sosPhones;
    private ActivitySosBinding binding;
    private static final String TAG = "SosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = getSharedPreferences("login", Context.MODE_PRIVATE)
                .getString("account", "error");
        String nowTime = (System.currentTimeMillis() / 1000) + "";
        Intent intent = getIntent();
        String lat = StringUtil.formatNum(intent.getFloatExtra("lat", 0), 4);
        String lng = StringUtil.formatNum(intent.getFloatExtra("lng", 0), 4);
        String nowLocation = "纬度: " + lat + " 经度：" + lng;
        OkHttpEngine okHttpEngine = OkHttpEngine.getInstance(this);
        okHttpEngine.getAsyncHttp(MyApplication.BASEURL + "/uploadSos.php?userId=" + userId +
                        "&time=" + nowTime + "&lat=" + lat + "&lng=" + lng, new ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                    }

                    @Override
                    public void onResponse(String str) throws IOException {
                    }
                });
        binding.tvUser.setText(userId);
        binding.tvLocation.setText(nowLocation);
        Date date = new Date();
        binding.tvTime.setText(date.getMonth()+"月" +date.getDay()+"日，" +
                date.getHours() + "时" + date.getMinutes() + "分" + date.getSeconds() + "秒");
        sosPhones = new ArrayList<>();
        sosPhonesInfo = new ArrayList<>();
        binding.btnCancelSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okHttpEngine.getAsyncHttp(MyApplication.BASEURL + "cancelSos.php?userId=" +
                        userId + "&time=" + nowTime, new ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                    }

                    @Override
                    public void onResponse(String str) throws IOException {
                        MyToast.toast("撤销成功");
                        finish();
                    }
                });
            }
        });
        initSosPhone();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sosPhonesInfo);
        binding.lvSosPhone.setAdapter(adapter);
        binding.lvSosPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 110、120这种紧急电话无法直接拨打，这里只能打卡联系人界面，手动拨打
                // 其余电话可以直接拨打
                if (sosPhones.get(position).equals("110") || sosPhones.get(position).equals("120")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Contacts.People.CONTENT_URI);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "onItemClick: " + sosPhones.get(position));
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + sosPhones.get(position)));
                    startActivity(intent);
                }

            }
        });
    }

    private void initSosPhone() {
        sosPhonesInfo.add("校保卫处：123");
        sosPhonesInfo.add("公安: 110");
        sosPhonesInfo.add("医院: 120");
        sosPhonesInfo.add("紧急联系人：15606160221");
        sosPhones.add("123");
        sosPhones.add("110");
        sosPhones.add("120");
        sosPhones.add("15606160221");
    }
}