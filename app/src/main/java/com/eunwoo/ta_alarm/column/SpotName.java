package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class SpotName {
    @ColumnInfo(name = "spot_name")
    private String SpotName;

    public String getSpotName() {
        return SpotName;
    }

    public void setSpotName(String spotName) {
        SpotName = spotName;
    }
}
