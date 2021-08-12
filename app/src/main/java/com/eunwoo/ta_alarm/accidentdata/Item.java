package com.eunwoo.ta_alarm.accidentdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("afos_fid")
    @Expose
    private String afosFid;
    @SerializedName("afos_id")
    @Expose
    private String afosId;
    @SerializedName("bjd_cd")
    @Expose
    private String bjdCd;
    @SerializedName("spot_cd")
    @Expose
    private String spotCd;
    @SerializedName("sido_sgg_nm")
    @Expose
    private String sidoSggNm;
    @SerializedName("spot_nm")
    @Expose
    private String spotNm;
    @SerializedName("occrrnc_cnt")
    @Expose
    private Integer occrrncCnt;
    @SerializedName("caslt_cnt")
    @Expose
    private Integer casltCnt;
    @SerializedName("dth_dnv_cnt")
    @Expose
    private Integer dthDnvCnt;
    @SerializedName("se_dnv_cnt")
    @Expose
    private Integer seDnvCnt;
    @SerializedName("sl_dnv_cnt")
    @Expose
    private Integer slDnvCnt;
    @SerializedName("wnd_dnv_cnt")
    @Expose
    private Integer wndDnvCnt;
    @SerializedName("lo_crd")
    @Expose
    private String loCrd;
    @SerializedName("la_crd")
    @Expose
    private String laCrd;

    public String getAfosFid() {
    return afosFid;
    }

    public void setAfosFid(String afosFid) {
    this.afosFid = afosFid;
    }

    public String getAfosId() {
    return afosId;
    }

    public void setAfosId(String afosId) {
    this.afosId = afosId;
    }

    public String getBjdCd() {
    return bjdCd;
    }

    public void setBjdCd(String bjdCd) {
    this.bjdCd = bjdCd;
    }

    public String getSpotCd() {
    return spotCd;
    }

    public void setSpotCd(String spotCd) {
    this.spotCd = spotCd;
    }

    public String getSidoSggNm() {
    return sidoSggNm;
    }

    public void setSidoSggNm(String sidoSggNm) {
    this.sidoSggNm = sidoSggNm;
    }

    public String getSpotNm() {
    return spotNm;
    }

    public void setSpotNm(String spotNm) {
    this.spotNm = spotNm;
    }

    public Integer getOccrrncCnt() {
    return occrrncCnt;
    }

    public void setOccrrncCnt(Integer occrrncCnt) {
    this.occrrncCnt = occrrncCnt;
    }

    public Integer getCasltCnt() {
    return casltCnt;
    }

    public void setCasltCnt(Integer casltCnt) {
    this.casltCnt = casltCnt;
    }

    public Integer getDthDnvCnt() {
    return dthDnvCnt;
    }

    public void setDthDnvCnt(Integer dthDnvCnt) {
    this.dthDnvCnt = dthDnvCnt;
    }

    public Integer getSeDnvCnt() {
    return seDnvCnt;
    }

    public void setSeDnvCnt(Integer seDnvCnt) {
    this.seDnvCnt = seDnvCnt;
    }

    public Integer getSlDnvCnt() {
    return slDnvCnt;
    }

    public void setSlDnvCnt(Integer slDnvCnt) {
    this.slDnvCnt = slDnvCnt;
    }

    public Integer getWndDnvCnt() {
    return wndDnvCnt;
    }

    public void setWndDnvCnt(Integer wndDnvCnt) {
    this.wndDnvCnt = wndDnvCnt;
    }

    public String getLoCrd() {
    return loCrd;
    }

    public void setLoCrd(String loCrd) {
    this.loCrd = loCrd;
    }

    public String getLaCrd() {
    return laCrd;
    }

    public void setLaCrd(String laCrd) {
    this.laCrd = laCrd;
    }
}