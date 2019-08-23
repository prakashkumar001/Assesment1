package com.altimetrik.assesment1.realm;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel {


    private int id;

    public LocationModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private String lastUpdatedTime;
    private String latitude;
    private String longitude;

    public LocationModel(){

    }

    private LocationModel(Parcel in) {
        lastUpdatedTime = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
