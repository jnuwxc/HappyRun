package com.example.happyrun.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpEngine {
    private static volatile OkHttpEngine instance;
    private OkHttpClient client;
    private Handler handler;
    private static final String TAG = "OkHttpEngine";

    public static OkHttpEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (OkHttpEngine.class) {
                if (instance == null) {
                    instance = new OkHttpEngine(context);
                }
            }
        }
        return instance;
    }

    private OkHttpEngine(Context context) {
        File sdCache = context.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(sdCache.getAbsoluteFile(), cacheSize));
        client = builder.build();
        handler = new Handler();
    }

    /**
     * 异步GET请求
     * @param url
     * @param callback
     */
    public void getAsyncHttp(String url, ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        dealResult(call, callback);
    }

    /**
     * 同步GET请求
     * @param url
     * @return
     */
    public String getSyncHttp(String url) {
        Request request= new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "getSyncHttp: " + response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    private void dealResult(Call call, final ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendFailedCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                sendSuccessCallback(response.body().string(), callback);
            }

            private void sendSuccessCallback(final String str, final ResultCallback callbackSuccess) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callbackSuccess != null) {
                            try {
                                callbackSuccess.onResponse(str);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            private void sendFailedCallback(final Request request, final Exception e, final ResultCallback callbackFailed){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callbackFailed != null) {
                            callbackFailed.onError(request, e);
                        }
                    }
                });
            }
        });
    }
}
