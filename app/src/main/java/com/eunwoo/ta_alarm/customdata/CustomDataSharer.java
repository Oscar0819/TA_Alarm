package com.eunwoo.ta_alarm.customdata;

import android.content.Context;
import android.location.Location;

import java.util.Map;

public class CustomDataSharer {

    //생성자
    public CustomDataSharer(Context context) {
        //생성자에 addAllCustomData함수를 추가해서 클래스를 인스턴스화 하면 바로 실행.
        addAllCustomData(context);
    }
    //실행시 SharedPreferences에 저장 되어있는 모든 커스텀 데이터를 메모리에 저장
    public void addAllCustomData(Context context) {
        //위험지역 데이터를 메모리에 추가하기 위해 parsingDataList 클래스 인스턴스화
        StaticCustomDataList staticCustomDataList = new StaticCustomDataList();
        //pref파일의 데이터를 pref에 저장
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        //getAll()메소드를 이용해 pref의 key값을 map자료형인 keys에 전부 저장
        Map<String, ?> keys = pref.getAll();

        //Key-Value형식으로 저장되어 있는 keys에 entrySet메소드를 이용해서 원하는 값 저장
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
//            Log.d("map values", entry.getKey() + ": " +
//                    entry.getValue().toString());
            // pref.xml에 들어있는 위경도를 getValue메소드를 이용해 split으로 나눠서 sp에 저장.
            String[] sp = entry.getValue().toString().split(",");

            //CustomName에는 getKey메소드를 이용해 커스텀 위험지역 추가할 때 작성한 이름을 저장.
            staticCustomDataList.addCustomName(entry.getKey());
            //sp의 값을 Double로 캐스팅해서 Customlo에 경도 저장
            staticCustomDataList.addCustomlo(Double.valueOf(sp[0]));
            //sp의 값을 Double로 캐스팅해서 Customla에 위도 저장
            staticCustomDataList.addCustomla(Double.valueOf(sp[1]));
        }
    }

    //위험지역을 추가 했을 때 메모리에 추가.
    public void addCustomData(String name, Location location) {
        StaticCustomDataList staticCustomDataList = new StaticCustomDataList();

        staticCustomDataList.addCustomName(name);
        staticCustomDataList.addCustomlo(location.getLongitude());
        staticCustomDataList.addCustomla(location.getLatitude());
    }
}
