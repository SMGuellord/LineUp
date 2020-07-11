package com.example.minzint.a4queue.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
public class QueueInfo implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("length")
    private int length;
    @SerializedName("eta")
    private int eta;
    @SerializedName("speed")
    private int speed;
    @SerializedName("traffic")
    private String traffic;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("address")
    private String address;
    @SerializedName("date_created")
    private String date_created;


    protected QueueInfo(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<QueueInfo> CREATOR = new Creator<QueueInfo>() {
        @Override
        public QueueInfo createFromParcel(Parcel in) {
            return new QueueInfo(in);
        }

        @Override
        public QueueInfo[] newArray(int size) {
            return new QueueInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(length);
        dest.writeInt(eta);
        dest.writeInt(speed);
        dest.writeString(traffic);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeString(date_created);
    }


        private void readFromParcel(Parcel in){
            id = in.readInt();
            length = in.readInt();
            eta = in.readInt();
            speed = in.readInt();
            traffic = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            address = in.readString();
            date_created = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return  " Queue stats: \n" +
                "   Length: " + length +"m\n"+
                "   ETA: " + eta +"min\n"+
                "   Speed: " + speed +"m/min\n"+
                "   Traffic: '" + traffic + '\'';
    }
}
