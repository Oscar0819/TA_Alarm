package com.eunwoo.ta_alarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eunwoo.ta_alarm.room.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private final int SINGLE_PERMISSION = 1004;
    private int permissionCheck;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("permission", String.valueOf(permissionCheck));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한이 없을 때...
            Log.d("permission", "2");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    SINGLE_PERMISSION);
            //권한 요청 거부 유무를 확인하는 코드 거부 했을 시 true반환
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("permission", "3");
                //권한 요청 거부시
                Toast.makeText(getApplicationContext(),
                        "위치 정보 권한이 필요합니다",
                        Toast.LENGTH_SHORT).show();
            } else {
                //권한 요청 거부한 이력이 없을 때
                Log.d("permission", "4");

            }
        } else {
            //권한이 있을 때
            Log.d("permission", "5");
            startMainActivity();
        }


    }

    private void startMainActivity() {
        insertDatabase();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void insertDatabase() {
        SharedPreferences sharedPreferences = new SharedPreferences();
        // 첫 실행인지 확인..
        sharedPreferences.checkFirstTime(getApplicationContext());
    }

    // 앱 권한 다이얼로그에서 권한을 선택하면 작동하는 콜백 함수..
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startMainActivity();
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청을 거부하거나 거부를 2번하면 동작
            Toast.makeText(getApplicationContext(),
                    "앱을 사용하기 위해서는 위치 권한이 필요합니다." +
                            "설정에서 권한을 허용해주세요.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
