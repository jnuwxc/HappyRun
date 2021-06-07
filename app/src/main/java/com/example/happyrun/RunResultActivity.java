package com.example.happyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.happyrun.databinding.ActivityRunResultBinding;

import java.util.List;

public class RunResultActivity extends AppCompatActivity {

    private ActivityRunResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRunResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.shareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"文字分享");
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

}