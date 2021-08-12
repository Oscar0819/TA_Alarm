package com.eunwoo.ta_alarm.column;

import androidx.room.ColumnInfo;

public class CasltCnt {

    @ColumnInfo(name = "caslt_cnt")
    private int caslt_cnt;

    public int getCaslt_cnt() {
        return caslt_cnt;
    }

    public void setCaslt_cnt(int caslt_cnt) {
        this.caslt_cnt = caslt_cnt;
    }

}
