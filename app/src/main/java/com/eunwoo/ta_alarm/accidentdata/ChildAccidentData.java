package com.eunwoo.ta_alarm.accidentdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildAccidentData {
    // @SerialzedName : JSON으로 serialize 될 때 매칭되는 이름을 명시하는 목적으로 사용
    // @Expose : object 중 해당 값이 null일 경우, json으로 만들 필드를 자동 생략
    @SerializedName("resultCode")
    @Expose
    private String resultCode;
    @SerializedName("resultMsg")
    @Expose
    private String resultMsg;
    @SerializedName("items")
    @Expose
    private Items items;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

}