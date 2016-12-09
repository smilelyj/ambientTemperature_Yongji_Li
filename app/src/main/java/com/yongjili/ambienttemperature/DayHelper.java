package com.yongjili.ambienttemperature;

/**
 * Created by YongjiLi on 10/4/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * class that stores the weather information for each day
 * Parcelable interface was implement for passing this class among the application
 * member variable
 * string date
 * string dayOfTheWeek: Mon, Tue ...
 * float temperature: in Cel
 */

/**
 * class that stores the weather  for each day
 * Parcelable interface was implement for passing this class among the application
 * member variable
 * string date 09-01-2016 etc...
 * string dayOfTheWeek: Mon, Tue ...
 * float temperature: in Cel (default)
 */

public class DayHelper implements Parcelable {

    DayHelper(String date, String dayOfTheWeek, float temperature) {
        this.date = date;
        this.dayOfTheWeek = dayOfTheWeek;
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    private float temperature;

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    private String dayOfTheWeek;

    public String getDate() {
        return date;
    }

    private String date;

    public static final Parcelable.Creator<DayHelper> CREATOR = new Creator<DayHelper>() {
        public DayHelper createFromParcel(Parcel source) {
            return new DayHelper(source.readString(), source.readString(), source.readFloat());
        }

        public DayHelper[] newArray(int size) {
            return new DayHelper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(dayOfTheWeek);
        dest.writeFloat(temperature);
    }
}
