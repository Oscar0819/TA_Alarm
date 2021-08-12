package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class Latitude {

    @ColumnInfo(name = "latitude")
    private double latitude; // 위도

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
