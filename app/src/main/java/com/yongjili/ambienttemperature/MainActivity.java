package com.yongjili.ambienttemperature;

/**
 * Created by YongjiLi on 10/3/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String logId = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up widgets for temperature display
        ambientTemperatureText = (TextView) findViewById(R.id.ambientTemperatureText);

        assert ambientTemperatureText != null;
        ambientTemperatureText.setText(R.string.Loading);
        dateTemperatureListView = (ListView) findViewById(R.id.temperatureListView);
        temperatureUnitSwitch = (Switch) findViewById(R.id.temperatureUnitSwitch);

        // setup the switch widgets for unit conversion
        temperatureUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText(R.string.Fahrenheit);
                    switchDateTemperatureListUnit(true);
                } else {
                    buttonView.setText(R.string.Celsius);
                    switchDateTemperatureListUnit(false);
                }
                if (ambientTemperatureSensorExist)
                    updateAmbientTemperatureDisplay();
            }
        });

        // setup for sensor service
        SensorServiceIntent = new Intent(MainActivity.this, SensorService.class);
        sensorReceiver = new SensorReceiver();
        sensorFilter = new IntentFilter();
        sensorFilter.addAction("android.intent.action.SENSOR_RECEIVER");
        registerReceiver(sensorReceiver, sensorFilter);

        // setup for temperature service
        TemperatureServiceIntent = new Intent(MainActivity.this, TemperatureService.class);
        temperatureReceiver = new TemperatureReceiver();
        temperatureFilter = new IntentFilter();
        temperatureFilter.addAction("android.intent.action.TEMPERATURE_RECEIVER");
        registerReceiver(temperatureReceiver, temperatureFilter);

        // start the services
        startService(SensorServiceIntent);

        dayList.addAll(Arrays.asList(dayHelper));

        SystemClock.sleep(500);
        if (!dayList.isEmpty())
            updateDateTemperatureList(dayList);
    }

    ArrayList<HashMap<String, Object>> dateTemperatureListContent
            = new ArrayList<HashMap<String, Object>>();

    /**
     * switch the temperature unit
     *
     * @param toFahrenheit if true, switch the unit from cel to far
     *                     if false, far to cel
     */
    private void switchDateTemperatureListUnit(boolean toFahrenheit) {

        // no need to do conversion
        if (inCelsius == !toFahrenheit)
            return;

        inCelsius = !toFahrenheit;
        if (inCelsius)
            dayInfoListToCel(dayList);
        else
            dayInfoListToFar(dayList);

        if (!dayList.isEmpty())
            updateDateTemperatureList(dayList);
    }

    /**
     * update the weather display for the list view
     * @param dayList a list of DayHelper object to display
     */
    private void updateDateTemperatureList(List<DayHelper> dayList) {

        Log.i(logId, "update: weather info of " + dayList.size() + " days");

        // clear the current display
        dateTemperatureListContent.clear();

        // setup the mock-up data
        Iterator dayIterator = dayList.iterator();
        while (dayIterator.hasNext()) {

            DayHelper curDayInfo = (DayHelper) dayIterator.next();
            String date = curDayInfo.getDate();
            String day = curDayInfo.getDayOfTheWeek();
            Float temperature = curDayInfo.getTemperature();

            // keep one places of decimal
            temperature = (float) (Math.round(temperature * 10) / 10.0);

            String temperatureString = temperature.toString();

            HashMap<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("dateText", date);
            listItem.put("dayText", day);
            listItem.put("temperatureText", temperatureString);

            dateTemperatureListContent.add(listItem);
        }

        //use simple adapter to setup the display of list view
        SimpleAdapter listAdapter = new SimpleAdapter(
                MainActivity.this,
                dateTemperatureListContent,
                R.layout.listview_date_temperature,
                new String[]{"dateText", "dayText", "temperatureText"},
                new int[]{R.id.dateTextView, R.id.dayTextView, R.id.temperatureTextView}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = LinearLayout.inflate(
                        getBaseContext(), R.layout.listview_date_temperature, null);

                TextView dateText =
                        (TextView) convertView.findViewById(R.id.dateTextView);
                TextView dayText =
                        (TextView) convertView.findViewById(R.id.dayTextView);
                TextView temperatureText =
                        (TextView) convertView.findViewById(R.id.temperatureTextView);

                dateText.setText((String) ((Map<String, Object>)
                        getItem(position)).get("dateText"));
                dayText.setText((String) ((Map<String, Object>)
                        getItem(position)).get("dayText"));
                temperatureText.setText((String) ((Map<String, Object>)
                        getItem(position)).get("temperatureText"));

                return convertView;
            }
        };

        dateTemperatureListView.setAdapter(listAdapter);

    }

    // intent for service
    private Intent SensorServiceIntent;
    private Intent TemperatureServiceIntent;

    // widgets variables for UI
    private TextView ambientTemperatureText;
    private ListView dateTemperatureListView;
    private Switch temperatureUnitSwitch;

    // for temperature
    Float ambientTemperature = (float) 0.0;
    private boolean ambientTemperatureSensorExist = true;
    private boolean inCelsius = true;
    private List<DayHelper> dayList = new ArrayList<>();

    // generate a random list of temperate in Celsius for Mon-Fri

    private static DayHelper dayHelper[] = new DayHelper[5];

    static {
        Random random = new Random();
        dayHelper[0] = new DayHelper("09-01-2016", "Monday", random.nextFloat() * 30 + 10);
        dayHelper[1] = new DayHelper("09-02-2016", "Tuesday", random.nextFloat() * 30 + 10);
        dayHelper[2] = new DayHelper("09-03-2016", "Wednesday", random.nextFloat() * 30 + 10);
        dayHelper[3] = new DayHelper("09-04-2016", "Thursday", random.nextFloat() * 30 + 10);
        dayHelper[4] = new DayHelper("09-05-2016", "Friday", random.nextFloat() * 30 + 10);
    }

    // broadcast receiver
    private TemperatureReceiver temperatureReceiver;
    private SensorReceiver sensorReceiver;

    private IntentFilter temperatureFilter;
    private IntentFilter sensorFilter;

    // handling incoming temperature message
    private class TemperatureReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i(logId, "temperature info received");
            Parcelable dayInfoParcelable[] = intent.getParcelableArrayExtra("Weather");
            if (dayInfoParcelable == null || dayInfoParcelable.length == 0)
                return;
            dayList.clear();
            for (int i = 0; i < dayInfoParcelable.length; i++)
                dayList.add((DayHelper) dayInfoParcelable[i]);
            if (!dayList.isEmpty())
                updateDateTemperatureList(dayList);
        }
    }

    // handling incoming sensor message
    private class SensorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i(logId, "sensor info received");
            if (!intent.getBooleanExtra("hasAmbientTemperature", true)) {
                ambientTemperatureText.setTextSize(16);
                ambientTemperatureText.setText(R.string.sensor_not_found);
                ambientTemperatureSensorExist = false;
            } else {
                ambientTemperature = intent.getFloatExtra("ambientTemperature", (float) 0);
                updateAmbientTemperatureDisplay();
            }
        }
    }

    // update the display of ambient temperature based on the current unit
    void updateAmbientTemperatureDisplay() {
        Float ambientTemperatureDisplay = ambientTemperature;
        if (!inCelsius)
            ambientTemperatureDisplay = celToFar(ambientTemperatureDisplay);
        ambientTemperatureDisplay = (float) (Math.round(ambientTemperatureDisplay * 10) / 10);
        ambientTemperatureText.setText(ambientTemperatureDisplay.toString());
    }

    /*
     exit when back button pressed
     */
    @Override
    public void onBackPressed() {
        unregisterAllServices();
        stopService(SensorServiceIntent);
        stopService(TemperatureServiceIntent);
        System.exit(0);
    }

    protected void onResume() {
        super.onResume();

        if (ambientTemperatureSensorExist) {
            registerReceiver(sensorReceiver, sensorFilter);
        }
        registerReceiver(temperatureReceiver, temperatureFilter);

        if (!dayList.isEmpty())
            updateDateTemperatureList(dayList);
    }

    protected void unregisterAllServices() {
        try {
            this.unregisterReceiver(temperatureReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception.
            } else {
                throw e;
            }
        }

        try {
            this.unregisterReceiver(sensorReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception.
            } else {
                throw e;
            }
        }
    }

    @Override
    protected void onPause() {

        unregisterAllServices();
        super.onPause();
    }

    // local temp util module
    static {
        try {
            System.loadLibrary("tempUtil");
        } catch (UnsatisfiedLinkError ule) {
            Log.e("MainActivity", "Error: Could not load native library: " + ule.getMessage());
        }
    }

    // JNI methods for temperature conversion
    native float celToFar(float cel);

    native float farToCel(float far);

    // convert a list of temperature
    native List<DayHelper> dayInfoListToFar(List<DayHelper> dayList);

    native List<DayHelper> dayInfoListToCel(List<DayHelper> dayList);
}
