package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class Longitude {

    @ColumnInfo(name = "longitude")
    private double longitude; // 경도

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
