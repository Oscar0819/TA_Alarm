package com.eunwoo.ta_alarm.customdata;


public class CustomData {

    public String customName;
    public Double customLatitude;
    public Double customLongitude;

    public CustomData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CustomData(String customName, Double customLatitude, Double customLongitude) {
        this.customName = customName;
        this.customLatitude = customLatitude;
        this.customLongitude = customLongitude;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Double getCustomLatitude() {
        return customLatitude;
    }

    public void setCustomLatitude(Double customLatitude) {
        this.customLatitude = customLatitude;
    }

    public Double getCustomLongitude() {
        return customLongitude;
    }

    public void setCustomLongitude(Double customLongitude) {
        this.customLongitude = customLongitude;
    }
}
