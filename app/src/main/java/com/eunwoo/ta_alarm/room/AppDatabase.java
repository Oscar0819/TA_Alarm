package com.eunwoo.ta_alarm.room;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {com.eunwoo.ta_alarm.room.Todo.class}, version = 1)
// 추상 클래스
public abstract class AppDatabase extends RoomDatabase {
    //데이터베이스를 매번 생성하는건 리소스를 많이 사용하므로 싱글톤이 권장됨.
    private static AppDatabase INSTANCE;

    public abstract TodoDao todoDao();

    //디비객체생성 가져오기
    public static AppDatabase getAppDatabase(Context context) {
        if(INSTANCE == null) {
            Log.d("AppDatabase", "getAppDatabase");

            INSTANCE = Room.databaseBuilder(context, com.eunwoo.ta_alarm.room.AppDatabase.class, "todo-db").build();

        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
