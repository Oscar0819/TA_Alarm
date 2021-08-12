
package com.eunwoo.ta_alarm.customdata;

import java.util.ArrayList;

public class CustomDataList {
    ArrayList<String> name = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();

    public CustomDataList() {
    }

    public CustomDataList(ArrayList<String> name, ArrayList<Double> latitude, ArrayList<Double> longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public ArrayList<Double> getLatitude() {
        return latitude;
    }

    public void setLatitude(ArrayList<Double> latitude) {
        this.latitude = latitude;
    }

    public ArrayList<Double> getLongitude() {
        return longitude;
    }

    public void setLongitude(ArrayList<Double> longitude) {
        this.longitude = longitude;
    }
}
