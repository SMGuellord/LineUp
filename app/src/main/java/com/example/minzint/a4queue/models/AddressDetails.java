package com.example.minzint.a4queue.models;

import com.google.android.gms.maps.model.LatLng;

public class AddressDetails {

    private String address;
    private LatLng mLatLng;

    public AddressDetails(String address, LatLng latLng) {
        this.address = address;
        mLatLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }
}
