package com.yongjili.ambienttemperature;

/**
 * Created by YongjiLi on 10/5/16.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * background service for ambient temperature reporting
 * if ambient temperature sensor exists
 * it will report ambient temperature in every five seconds
 * if not it will report not existing and stop itself
 */

public class SensorService extends Service {

    private final String logId = "SensorService";

    private SensorManager mSensorManager;
    private Sensor mAmbientTemperature;
    private SensorEventListener sensorEventListener;

    private Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(logId, "sensor service started");

        // setup for broadcast sensor info
        intent = new Intent();
        intent.setAction("android.intent.action.SENSOR_RECEIVER");

        // get sensor manager and ambient temperature sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        // first check if the ambient temperature sensor exists
        if (mAmbientTemperature == null) {

            // if not , broadcast and stop sensor service

            Log.w(logId, "ambient temperature sensor not found");
            intent.putExtra("hasAmbientTemperature", false);
            sendBroadcast(intent);
            this.stopSelf();
        } else {
            intent.putExtra("hasAmbientTemperature", true);
        }

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.i(logId, "read new ambient temperature");

                // the first number of ambient temperature reading is the current temperature in cel
                float ambientTemperature = event.values[0];
                intent.putExtra("ambientTemperature", ambientTemperature);
                sendBroadcast(intent);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // register sensorEventListener for ambient temperature sensor, report for every 5 seconds
        mSensorManager.registerListener(sensorEventListener, mAmbientTemperature, 5000000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
