package com.altimetrik.assesment1.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationDBObject extends RealmObject {

    @PrimaryKey
    public int id;
    public String lastUpdatedTime;
    public String latitude;
    public String longitude;

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
