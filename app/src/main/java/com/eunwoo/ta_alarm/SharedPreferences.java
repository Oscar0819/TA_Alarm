package com.eunwoo.ta_alarm;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.eunwoo.ta_alarm.accidentdata.ChildAccidentData;
import com.eunwoo.ta_alarm.accidentdata.Item;
import com.eunwoo.ta_alarm.accidentdata.Items;
import com.eunwoo.ta_alarm.accidentdata.RetrofitClient;
import com.eunwoo.ta_alarm.accidentdata.RetrofitInterface;
import com.eunwoo.ta_alarm.room.AppDatabase;
import com.eunwoo.ta_alarm.room.Todo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedPreferences {
     String TAG = "SharedPreferences";

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    private int failureCount = 0;
    //값 불러오기
    public void getPreferences(Context context, String KeyName) {
        //SharedPreferences선언 파라미터는 SharedPreferences의 이름과 모드
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        //로그의 내용 pref의 값을 가져오는데 Key에 대한 값이 없을경우 두 번째
        //파라미터의 String을 반환함..
        Log.d(TAG, pref.getString(KeyName, "Data is not exists..."));
        //pref.getString(KeyName, "Data is not exists...");
    }

    //값 저장
    public void savePreferences(Context context, String KeyName, String KeyValue) {
        Log.d(TAG, "KeyName : " + KeyName + ", " + "KeyValue : " + KeyValue);
        //선언
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        //SharedPreferences를 제어할 Editor 선언
        android.content.SharedPreferences.Editor editor = pref.edit();
        //Key, Value 형식으로 저장
        editor.putString(KeyName, KeyValue);
        //최종 커밋 커밋을 해야 저장이 됨.
        editor.commit();
    }

    //동일한 키가 있는지 확인
    public Boolean containsPreferences(Context context, String KeyName) {
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        Log.d(TAG, "containsPreferences : " + pref.contains(KeyName));
        return pref.contains(KeyName);
    }

    //값 삭제하기
    public void removePreferences(Context context, String KeyName) {
        Log.d(TAG, "KeyName : " + KeyName + " Remove...");
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref.edit();
        editor.remove(KeyName);
        editor.commit();
    }

    //모든 값을 반환
    public void getAllPreferences(Context context, ArrayList<String> ListName) {
        android.content.SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        Map<String, ?> keys = pref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
            ListName.add(entry.getValue().toString());
        }
    }

    public void checkFirstTime(Context context) {
        //최초 실행 여부 판단하는 구문
        android.content.SharedPreferences pref = context.getSharedPreferences("isFirst", context.MODE_PRIVATE);

        // key:isFirst가 처음 생성되면 기본값을 false로
        boolean first = pref.getBoolean("isFirst", false);
        if(first==false){
            Log.d("Is first Time?", "first");
            android.content.SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",true);
            editor.commit();
            //앱 최초 실행시 하고 싶은 작업
            insertDatabase(context, pref);

        }else{
            Log.d("Is first Time?", "not first");
        }
    }
    private void insertDatabase(Context context, android.content.SharedPreferences pref) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("thread", "start");
                AssetManager am = context.getResources().getAssets();
                InputStream is1;

                ArrayList<String> spot_name2 = new ArrayList<>(); // 위험지역이름
                ArrayList<Double> longitude2 = new ArrayList<>(); // 경도
                ArrayList<Double> latitude2 = new ArrayList<>(); // 위도
                ArrayList<Integer> caslt_cnt2 = new ArrayList<>(); // 사상자 수
                ArrayList<Integer> dth_dnv_cnt2 = new ArrayList<>(); //사망자 수
                ArrayList<Integer> se_dnv_cnt2 = new ArrayList<>(); // 중상자 수
                ArrayList<Integer> sl_dnv_cnt2 = new ArrayList<>(); // 경상자 수
/*
                retrofitClient = RetrofitClient.getInstance();
                retrofitInterface = RetrofitClient.getRetrofitInterface();


                retrofitInterface.get2015ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();


                        Log.d("retrofit", "2015 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2015 Data fetch END");
                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });

                retrofitInterface.get2016ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();

                        Log.d("retrofit", "2016 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2016 Data fetch END");
                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });

                retrofitInterface.get2017ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();

                        Log.d("retrofit", "2017 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2017 Data fetch END");
                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });

                retrofitInterface.get2018ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();

                        Log.d("retrofit", "2018 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2018 Data fetch END");
                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });

                retrofitInterface.get2019ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();

                        Log.d("retrofit", "2019 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2019 Data fetch END");
                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });

                retrofitInterface.get2020ChildAccidentData().enqueue(new Callback<ChildAccidentData>() {
                    @Override
                    public void onResponse(Call<ChildAccidentData> call, Response<ChildAccidentData> response) {
                        ChildAccidentData childAccidentData = response.body();
                        Items items = childAccidentData.getItems();

                        Log.d("retrofit", "2020 Data fetch success");
                        for (Item item : items.getItem()) {
                            spot_name2.add(item.getSpotNm());
                            latitude2.add(Double.valueOf(item.getLaCrd()));
                            longitude2.add(Double.valueOf(item.getLoCrd()));
                            caslt_cnt2.add(item.getCasltCnt());
                            dth_dnv_cnt2.add(item.getDthDnvCnt());
                            se_dnv_cnt2.add(item.getSeDnvCnt());
                            sl_dnv_cnt2.add(item.getSlDnvCnt());
                        }
                        Log.d("retrofit", "2020 Data fetch END");

                        listener.onEvent(context, pref, spot_name2, latitude2, longitude2, caslt_cnt2, dth_dnv_cnt2, se_dnv_cnt2, sl_dnv_cnt2);

                    }

                    @Override
                    public void onFailure(Call<ChildAccidentData> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        failureCount++;
                    }
                });
                */

                try {
                    is1 = am.open("C2015Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;

                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    is1 = am.open("C2016Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;


                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    is1 = am.open("C2017Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;


                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    is1 = am.open("C2018Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;


                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    is1 = am.open("C2019Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;


                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    is1 = am.open("C2020Data.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is1, "UTF-8"));

                    String XmlTag;


                    xpp.next();
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:

                                //시작
                                break;

                            case XmlPullParser.START_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item")) ;

                                else if (XmlTag.equals("lo_crd")) {
                                    //경도
                                    xpp.next();
                                    longitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("la_crd")) {
                                    //위도
                                    xpp.next();
                                    latitude2.add(Double.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("spot_nm")) {
                                    // 위험지역 이름
                                    xpp.next();
                                    spot_name2.add(xpp.getText());

                                } else if (XmlTag.equals("caslt_cnt")) {
                                    // 사상자 수
                                    xpp.next();
                                    caslt_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("dth_dnv_cnt")) {
                                    // 사망자 수
                                    xpp.next();
                                    dth_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                } else if (XmlTag.equals("se_dnv_cnt")) {
                                    // 중상자 수
                                    xpp.next();
                                    se_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }  else if (XmlTag.equals("sl_dnv_cnt")) {
                                    // 경상자 수
                                    xpp.next();
                                    sl_dnv_cnt2.add(Integer.valueOf(xpp.getText()));

                                }

                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                XmlTag = xpp.getName();

                                if (XmlTag.equals("item"))

                                    break;
                        }
                        eventType = xpp.next();
                    }

                    //끝
                    is1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                int size = spot_name2.size();
                // 데이터베이스에 데이터 입력
                for (int i = 0; i < size; i++) {
                    // DB 생성
                    AppDatabase db = AppDatabase.getAppDatabase(context);
                    db.todoDao().insert(new Todo(
                            spot_name2.get(i), caslt_cnt2.get(i), dth_dnv_cnt2.get(i),
                            se_dnv_cnt2.get(i), sl_dnv_cnt2.get(i), longitude2.get(i), latitude2.get(i)
                    ));
                }

            }
        }).start();
    }
/*
    Listener listener = new Listener() {
        @Override
        public void onEvent(Context context, android.content.SharedPreferences pref, ArrayList<String> spot_name2, ArrayList<Double> latitude2, ArrayList<Double> longitude2, ArrayList<Integer> caslt_cnt2, ArrayList<Integer> dth_dnv_cnt2, ArrayList<Integer> se_dnv_cnt2, ArrayList<Integer> sl_dnv_cnt2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 데이터를 가져오는데 성공하면 failureCount가 0 이므로 데이터베이스에 입력
                    if (failureCount == 0) {
                        Log.d(TAG, "insertDatabase!");
                        int size = spot_name2.size();
                        // 데이터베이스에 데이터 입력
                        for (int i = 0; i < size; i++) {
                            // DB 생성
                            AppDatabase db = AppDatabase.getAppDatabase(context);
                            db.todoDao().insert(new Todo(
                                    spot_name2.get(i), caslt_cnt2.get(i), dth_dnv_cnt2.get(i),
                                    se_dnv_cnt2.get(i), sl_dnv_cnt2.get(i), longitude2.get(i), latitude2.get(i)
                            ));
                        }
                    } else {
                        Log.d(TAG, "failure Count = " + failureCount);
                        // 데이터를 받아오지 못하면 다시 받아와야함으로 isFirst의 값을 다시 false로
                        android.content.SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("isFirst", false);
                        editor.commit();
                        Toast.makeText(context, "데이터 입력 실패...", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    };

    public interface Listener {
        void onEvent(Context context,
                     android.content.SharedPreferences pref,
                     ArrayList<String> spot_name2,
                     ArrayList<Double> latitude2,
                     ArrayList<Double> longitude2,
                     ArrayList<Integer> caslt_cnt2,
                     ArrayList<Integer> dth_dnv_cnt2,
                     ArrayList<Integer> se_dnv_cnt2,
                     ArrayList<Integer> sl_dnv_cnt2);
    }
 */
}
