package com.eunwoo.ta_alarm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.CircleOverlay;

import java.util.ArrayList;
import java.util.List;

public class ForegroundService extends Service {
    public static final String TAG = "ForegroundService";

    LocationManager locationManager;

//    private LocationRequest locationRequest;
//    private Location mLocation;
//    private FusedLocationProviderClient mFusedLocationClient;

    public static final String GPS_PROVIDER = "gps";
    //public static final String NETWORK_PROVIDER = "network";
    //public static final String PASSIVE_PROVIDER = "passive";

    MainActivity mainActivity;

    //CData==========================================================================
    private static ArrayList<CircleOverlay> circleOverlays = new ArrayList<>(),
    //CustomData
            circleOverlaysCustom = new ArrayList<>();
    //===============================================================================

    ;
    NotificationManager manager2;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    private static int cCircleOverlaysSize, customCircleOverlaysSize;

    Intent notificationIntent;

    final String notiChannelId = "WAlarm";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreateService");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mainActivity = new MainActivity();

        //노티를 누르면 이전에 사용하던 액티비티를 사용...
        notificationIntent = new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "MyApp::MyWakelockTag");

        setCircleOverlaySize();

        forCircleOverlay();
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroyService");

        manager2.cancel(2);
        stopForeground(true);
//        stopSelf();
        super.onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        processCommand(intent);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //requestLocationUpdates()minTime는 ms 10000 = 10s, minDistance는 1 = 1m
            locationManager.requestLocationUpdates(GPS_PROVIDER, 10000, 50, locationListener);
            // TEST START
//            locationRequest = new LocationRequest()
//                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                    .setInterval(10000)
//                    .setFastestInterval(30000);
//
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            // END
        }

        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.d(TAG, "onTaskRemoved");
    }

    private void processCommand(Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ForegroundService");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("위험지역알림");
        builder.setContentText("실행 중");

        // builder의 노티에 대한 pendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);




        if (Build.VERSION.SDK_INT >= 26){
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("ForegroundService", "정보", NotificationManager
                    .IMPORTANCE_LOW));

            manager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager2.createNotificationChannel(new NotificationChannel(notiChannelId, "위험지역", NotificationManager
                    .IMPORTANCE_HIGH));
        }



        startForeground(1, builder.build());
    }

//    LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            mLocation = locationResult.getLastLocation();
//            if (mLocation != null) {
//                Log.d(TAG, "latitude : " + mLocation.getLatitude() + " longitude : " + mLocation.getLongitude());
//
//                searchingCircleOverlay(mLocation);
//            }
//        }
//    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());

            //Log.d(TAG, String.valueOf(CircleOverlaySizeC));

            searchingCircleOverlay(location);

            //Log.d(TAG, "Size : " + (circleOverlays.size()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void forCircleOverlay() {
        //Cdata
        for (int i = 0; i < cCircleOverlaysSize; i++){
            circleOverlays.add(mainActivity.circleOverlays.get(i));
        }

        //Custom
        for (int i = 0; i < customCircleOverlaysSize; i++) {
            circleOverlaysCustom.add(mainActivity.customCircleOverlays.get(i));
        }
    }

    private void setCircleOverlaySize() {
        customCircleOverlaysSize = mainActivity.customCircleOverlays.size();

        cCircleOverlaysSize = mainActivity.circleOverlays.size();
    }

    private void searchingCircleOverlay(Location location){
        //Cdata
        for (int i = 0; i < cCircleOverlaysSize; i++) {
            if (circleOverlays.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                Alarm();
            }
        }
        //Custom data
        for (int i = 0; i < customCircleOverlaysSize; i++) {
            if (circleOverlaysCustom.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                Alarm();
            }
        }
    }
    private void Alarm(){
        // Log.d(TAG, "In circle");
        if (powerManager.isScreenOn() == false){
            wakeLock.acquire();
            wakeLock.release();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder2;

            builder2 = new NotificationCompat.Builder(this, "WAlarm");

            builder2.setSmallIcon(R.mipmap.ic_launcher);
            builder2.setContentTitle("위험지역알림");
            builder2.setContentText("조심하세요. 현재 교통사고 다발지역에 위치합니다.");
            builder2.setTicker("조심하세요. 현재 교통사고 다발지역에 위치합니다.");
            builder2.setShowWhen(true);
            builder2.setWhen(System.currentTimeMillis());
            builder2.setAutoCancel(true);

            // builder2의 노티에 대한 pendingIntent
            PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder2.setContentIntent(pendingIntent2);

            manager2.notify(2, builder2.build());
        }
    }
    public void setCircleOverlaysCustom(List<CircleOverlay> circleOverlays) {
        circleOverlaysCustom.clear();

        customCircleOverlaysSize = circleOverlays.size();
        for (int i = 0; i < circleOverlays.size(); i++) {
            circleOverlaysCustom.add(circleOverlays.get(i));
        }
    }

    public void setCircleOverlays(List<CircleOverlay> circleOverlays) {
        ForegroundService.circleOverlays.clear();

        cCircleOverlaysSize = circleOverlays.size();
        for (int i = 0; i < circleOverlays.size(); i++) {
            ForegroundService.circleOverlays.add(circleOverlays.get(i));
        }
    }
}

