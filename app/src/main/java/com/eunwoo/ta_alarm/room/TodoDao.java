package com.eunwoo.ta_alarm.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.eunwoo.ta_alarm.column.CasltCnt;
import com.eunwoo.ta_alarm.column.DeathCnt;
import com.eunwoo.ta_alarm.column.Latitude;
import com.eunwoo.ta_alarm.column.Longitude;
import com.eunwoo.ta_alarm.column.SeriouslyCnt;
import com.eunwoo.ta_alarm.column.SlightlyCnt;
import com.eunwoo.ta_alarm.column.SpotName;

import java.util.List;

// 모든 DB CRUD작업은 메인스레드가 아닌 백그라운드로 작업해야한다.
// (단, 라이브데이터는 반응시 자기가 알아서 백그라운드로 작업을 처리해준다.
@Dao
public interface TodoDao {
    @Query("SELECT * FROM Todo")
    LiveData<List<com.eunwoo.ta_alarm.room.Todo>> getAll(); //LiveData => Todo테이블에 있는 모든 객체를 계속 관찰하고있다가 변경이 일어나면 그것을 자동으로 업데이트하도록한다.
                                    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응하는)

    // 쿼리... 각 컬럼의 데이터를 읽음
    @Query("SELECT spot_name FROM Todo")
    // SpotName 타입을 선언해서 SpotName 메소드를 이용해 컬럼 값을 읽음
    public List<SpotName> SelectSpotName();

    @Query("SELECT caslt_cnt FROM Todo")
    public List<CasltCnt> SelectCasltCnt();

    @Query("SELECT dth_dnv_cnt FROM Todo")
    public List<DeathCnt> SelectDeathCnt();

    @Query("SELECT se_dnv_cnt FROM Todo")
    public List<SeriouslyCnt> SelectSeriouslyCnt();

    @Query("SELECT sl_dnv_cnt FROM Todo")
    public List<SlightlyCnt> SelectSlightlyCnt();

    @Query("SELECT longitude FROM Todo")
    public List<Longitude> SelectLongitude();

    @Query("SELECT latitude FROM Todo")
    public List<Latitude> SelectLatitude();

    // 입력 갱신 삭제
    @Insert
    void insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("DELETE FROM Todo")
    void deleteAll();

}
