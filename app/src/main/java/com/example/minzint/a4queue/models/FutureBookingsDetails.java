package com.example.minzint.a4queue.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class FutureBookingsDetails implements Parcelable{

    @SerializedName("id")
    private String booking_id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("username")
    private String username;
    @SerializedName("profile")
    private String profile;
    @SerializedName("matched_id")
    private String matched_id;
    @SerializedName("matched_username")
    private String matched_username;
    @SerializedName("lat")
    private float lat;
    @SerializedName("lng")
    private float lng;
    @SerializedName("address")
    private String address;
    @SerializedName("dates")
    private String date;
    @SerializedName("times")
    private String times;
    @SerializedName("details")
    private String details;


    @SerializedName("date_created")
    private String date_created;
    @SerializedName("fee_per_hr")
    private double fee_per_hour;

    //Constructor
    public FutureBookingsDetails(Parcel in) {
        readFromParcel(in);
    }

    /**
     *
     * @return booking_id
     */
    public String getBooking_id() {
        return booking_id;
    }

    /**
     *
     * set booking_id
     */
    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    /**
     *
     * @return user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     *
     * set userr_id
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * set username booking_id
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     *
     * set Profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     *
     * @return matched_id
     */
    public String getMatched_id() {
        return matched_id;
    }

    /**
     *
     * set matched id
     */
    public void setMatched_id(String matched_id) {
        this.matched_id = matched_id;
    }

    /**
     *
     * @return matched username
     */
    public String getMatched_username() {
        return matched_username;
    }

    /**
     *
     * set matched username
     */
    public void setMatched_username(String matched_username) {
        this.matched_username = matched_username;
    }

    /**
     *
     * @return latitude
     */
    public float getLat() {
        return lat;
    }

    /**
     *
     * set latitude
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     *
     * @return longitude
     */
    public float getLng() {
        return lng;
    }

    /**
     *
     * set longitude
     */
    public void setLng(float lng) {
        this.lng = lng;
    }

    /**
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * set Address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * return date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * set date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return time
     */
    public String getTimes() {
        return times;
    }

    /**
     *
     * set time
     */
    public void setTimes(String times) {
        this.times = times;
    }

    /**
     *
     * @return details
     */
    public String getDetails() {
        return details;
    }

    /**
     *
     * @return setDetails
     */
    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    /**
     *
     * @return fee_per_hour
     */
    public double getFee_per_hour() {
        return fee_per_hour;
    }

    /**
     *
     * @return set fee_per_hour
     */
    public void setFee_per_hour(double fee_per_hour) {
        this.fee_per_hour = fee_per_hour;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.booking_id);
        dest.writeString(this.user_id);
        dest.writeString(this.username);
        dest.writeString(this.matched_id);
        dest.writeString(this.matched_username);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lng);
        dest.writeString(this.address);
        dest.writeString(this.date);
        dest.writeString(this.times);
        dest.writeString(this.details);
        dest.writeString(this.date_created);
        dest.writeDouble(this.fee_per_hour);

    }

    private void readFromParcel(Parcel in){
        this.booking_id = in.readString();
        this.user_id = in.readString();
        this.username = in.readString();
        this.matched_id = in.readString();
        this.matched_username = in.readString();
        this.lat = in.readFloat();
        this.lng = in.readFloat();
        this.address = in.readString();
        this.date = in.readString();
        this.times = in.readString();
        this.details = in.readString();
        this.date_created = in.readString();
        this.fee_per_hour = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FutureBookingsDetails createFromParcel(Parcel in ) {
            return new FutureBookingsDetails( in );
        }

        public FutureBookingsDetails[] newArray(int size) {
            return new FutureBookingsDetails[size];
        }
    };
}
