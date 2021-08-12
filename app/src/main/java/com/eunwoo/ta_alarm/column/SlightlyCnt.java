package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class SlightlyCnt {

    @ColumnInfo(name = "sl_dnv_cnt")
    private int sl_dnv_cnt; // 경상자 수

    public int getSl_dnv_cnt() {
        return sl_dnv_cnt;
    }

    public void setSl_dnv_cnt(int sl_dnv_cnt) {
        this.sl_dnv_cnt = sl_dnv_cnt;
    }
}
