package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class SeriouslyCnt {

    @ColumnInfo(name = "se_dnv_cnt")
    private int se_dnv_cnt; // 중상자 수

    public int getSe_dnv_cnt() {
        return se_dnv_cnt;
    }

    public void setSe_dnv_cnt(int se_dnv_cnt) {
        this.se_dnv_cnt = se_dnv_cnt;
    }
}
