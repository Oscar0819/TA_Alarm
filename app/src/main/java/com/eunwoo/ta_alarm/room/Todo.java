package com.eunwoo.ta_alarm.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Todo {
    @PrimaryKey(autoGenerate = true) //autoGenerate는 알아서 id를 1씩 증가시켜줌.
    private int id;

    // 입력에 쓰이는 세팅
    @ColumnInfo(name = "spot_name")
    private String spot_name; // 위험지역이름
    @ColumnInfo(name = "caslt_cnt")
    private int caslt_cnt; // 사상자 수
    @ColumnInfo(name = "dth_dnv_cnt")
    private int dth_dnv_cnt; //사망자 수
    @ColumnInfo(name = "se_dnv_cnt")
    private int se_dnv_cnt; // 중상자 수
    @ColumnInfo(name = "sl_dnv_cnt")
    private int sl_dnv_cnt; // 경상자 수
    @ColumnInfo(name = "longitude")
    private double longitude; // 경도
    @ColumnInfo(name = "latitude")
    private double latitude; // 위도

    // 초기 데이터베이스 세팅 생성자
    public Todo(String spot_name, int caslt_cnt, int dth_dnv_cnt,
                int se_dnv_cnt, int sl_dnv_cnt, double longitude, double latitude) {
        this.spot_name = spot_name;
        this.caslt_cnt = caslt_cnt;
        this.dth_dnv_cnt = dth_dnv_cnt;
        this.se_dnv_cnt = se_dnv_cnt;
        this.sl_dnv_cnt = sl_dnv_cnt;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpot_name() {
        return spot_name;
    }

    public void setSpot_name(String spot_name) {
        this.spot_name = spot_name;
    }

    public int getCaslt_cnt() {
        return caslt_cnt;
    }

    public void setCaslt_cnt(int caslt_cnt) {
        this.caslt_cnt = caslt_cnt;
    }

    public int getDth_dnv_cnt() {
        return dth_dnv_cnt;
    }

    public void setDth_dnv_cnt(int dth_dnv_cnt) {
        this.dth_dnv_cnt = dth_dnv_cnt;
    }

    public int getSe_dnv_cnt() {
        return se_dnv_cnt;
    }

    public void setSe_dnv_cnt(int se_dnv_cnt) {
        this.se_dnv_cnt = se_dnv_cnt;
    }

    public int getSl_dnv_cnt() {
        return sl_dnv_cnt;
    }

    public void setSl_dnv_cnt(int sl_dnv_cnt) {
        this.sl_dnv_cnt = sl_dnv_cnt;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {

        return "\n id=> " + this.id + ", 위험지역 이름 => " + this.spot_name
                + " , 사상자 수 => " + this.caslt_cnt
                + " , 사망자 수 => " + this.dth_dnv_cnt
                + " , 중상자 수 => " + this.se_dnv_cnt
                + " , 경상자 수 => " + this.sl_dnv_cnt
                + " , 경도 => " + this.longitude
                + " , 위도 => " + this.latitude ;

    }
}
