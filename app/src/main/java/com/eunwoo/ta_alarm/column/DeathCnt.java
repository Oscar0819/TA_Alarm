package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class DeathCnt {

    @ColumnInfo(name = "dth_dnv_cnt")
    private int dth_dnv_cnt; //사망자 수

    public int getDth_dnv_cnt() {
        return dth_dnv_cnt;
    }

    public void setDth_dnv_cnt(int dth_dnv_cnt) {
        this.dth_dnv_cnt = dth_dnv_cnt;
    }
}
