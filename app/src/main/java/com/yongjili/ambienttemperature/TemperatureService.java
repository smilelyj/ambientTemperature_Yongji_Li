package com.yongjili.ambienttemperature;

/**
 * Created by YongjiLi on 10/5/16.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * background service for temperature data

 */

public class TemperatureService extends Service {

    private final String logId = "TemperatureService";

    private Intent intent;

    public void onCreate() {
        super.onCreate();

        Log.i(logId, "temperature service started");

        intent = new Intent();
        intent.setAction("android.intent.action.TEMPERATURE_RECEIVER");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
