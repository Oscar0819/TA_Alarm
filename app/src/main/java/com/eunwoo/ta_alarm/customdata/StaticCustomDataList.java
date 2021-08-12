package com.eunwoo.ta_alarm.customdata;

import java.util.ArrayList;

public class StaticCustomDataList {

    public static ArrayList<Double> customLo = new ArrayList<>();
    public static ArrayList<Double> customLa = new ArrayList<>();
    public static ArrayList<String> customName = new ArrayList<>();


    public StaticCustomDataList() {
        super();
    }

    public void addCustomlo(Double longitude) { this.customLo.add(longitude); }
    public void addCustomla(Double latitude) { this.customLa.add(latitude); }
    public void addCustomName(String Name) {this.customName.add(Name); }

    public static ArrayList<Double> getCustomLo() {
        return customLo;
    }

    public static void setCustomLo(ArrayList<Double> customLo) {
        StaticCustomDataList.customLo = customLo;
    }

    public static ArrayList<Double> getCustomLa() {
        return customLa;
    }

    public static void setCustomLa(ArrayList<Double> customLa) {
        StaticCustomDataList.customLa = customLa;
    }

    public static ArrayList<String> getCustomName() {
        return customName;
    }

    public static void setCustomName(ArrayList<String> customName) {
        StaticCustomDataList.customName = customName;
    }

    public static void clearCustomData() {
        customName.clear();
        customLa.clear();
        customLo.clear();
    }
}
