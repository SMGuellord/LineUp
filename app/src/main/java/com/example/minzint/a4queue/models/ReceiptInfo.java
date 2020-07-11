package com.example.minzint.a4queue.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ReceiptInfo implements Parcelable{

    @SerializedName("user_id")
    private String user_id;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("matched_user_id")
    private String matched_user_id;
    @SerializedName("matched_user_name")
    private String matched_user_name;
    @SerializedName("address")
    private String address;
    @SerializedName("time_queued")
    private String time_queued;
    @SerializedName("fee")
    private double fee;
    @SerializedName("total_paid")
    private double total_paid;
    @SerializedName("date")
    private String date;

    /*Constructor*/
    public ReceiptInfo(Parcel in) {
        readFromParcel(in);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMatched_user_id() {
        return matched_user_id;
    }

    public void setMatched_user_id(String matched_user_id) {
        this.matched_user_id = matched_user_id;
    }

    public String getMatched_user_name() {
        return matched_user_name;
    }

    public void setMatched_user_name(String matched_user_name) {
        this.matched_user_name = matched_user_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime_queued() {
        return time_queued;
    }

    public void setTime_queued(String time_queued) {
        this.time_queued = time_queued;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getTotal_paid() {
        return total_paid;
    }

    public void setTotal_paid(double total_paid) {
        this.total_paid = total_paid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(user_name);
        dest.writeString(matched_user_id);
        dest.writeString(matched_user_name);
        dest.writeString(address);
        dest.writeString(time_queued);
        dest.writeDouble(fee);
        dest.writeDouble(total_paid);
        dest.writeString(date);

    }

    private void readFromParcel(Parcel in){
            this.user_id = in.readString();
            this.user_name = in.readString();
            this.matched_user_id = in.readString();
            this.matched_user_name = in.readString();
            this.address = in.readString();
            this.time_queued = in.readString();
            this.fee = in.readDouble();
            this.total_paid = in.readDouble();
            this.date = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ReceiptInfo createFromParcel(Parcel in ) {
            return new ReceiptInfo(in);
        }

        public ReceiptInfo[] newArray(int size) {
            return new ReceiptInfo[size];
        }
    };
}
