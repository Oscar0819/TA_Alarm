package com.eunwoo.ta_alarm.accidentdata;


import com.eunwoo.ta_alarm.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    String startPoint = "http://apis.data.go.kr/B552061/frequentzoneChild/getRestFrequentzoneChild?serviceKey=";
    String c2015 = "&searchYearCd=2015&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String c2016 = "&searchYearCd=2016&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String c2017 = "&searchYearCd=2017&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String c2018 = "&searchYearCd=2018&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String c2019 = "&searchYearCd=2019&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String c2020 = "&searchYearCd=2020&siDo=&guGun=&type=json&numOfRows=10000&pageNo=1";
    String key = BuildConfig.open_api_key;

    @GET(startPoint + key + c2015)
    Call<ChildAccidentData> get2015ChildAccidentData();

    @GET(startPoint + key + c2016)
    Call<ChildAccidentData> get2016ChildAccidentData();

    @GET(startPoint + key + c2017)
    Call<ChildAccidentData> get2017ChildAccidentData();

    @GET(startPoint + key + c2018)
    Call<ChildAccidentData> get2018ChildAccidentData();

    @GET(startPoint + key + c2019)
    Call<ChildAccidentData> get2019ChildAccidentData();

    @GET(startPoint + key + c2020)
    Call<ChildAccidentData> get2020ChildAccidentData();

//    @Query("ServiceKey") String ServiceKey,
//    @Query("searchYearCd") String searchYearCd,
//    @Query("siDo") String siDo,
//    @Query("guGun") String guGun,
//    @Query("type") String type,
//    @Query("numOfRows") String numOfRows,
//    @Query("pageNo") String pageNo

}