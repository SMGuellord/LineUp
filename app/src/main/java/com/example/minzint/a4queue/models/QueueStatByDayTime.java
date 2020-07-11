package com.example.minzint.a4queue.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class QueueStatByDayTime implements Parcelable{
    @SerializedName("dates")
    private String dates;
    @SerializedName("times")
    private String times;


    protected QueueStatByDayTime(Parcel in) {
        dates = in.readString();
        times = in.readString();
    }

    public static final Creator<QueueStatByDayTime> CREATOR = new Creator<QueueStatByDayTime>() {
        @Override
        public QueueStatByDayTime createFromParcel(Parcel in) {
            return new QueueStatByDayTime(in);
        }

        @Override
        public QueueStatByDayTime[] newArray(int size) {
            return new QueueStatByDayTime[size];
        }
    };

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dates);
        dest.writeString(times);
    }
}
