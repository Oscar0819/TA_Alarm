package com.eunwoo.ta_alarm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.eunwoo.ta_alarm.column.CasltCnt;
import com.eunwoo.ta_alarm.column.DeathCnt;
import com.eunwoo.ta_alarm.column.Latitude;
import com.eunwoo.ta_alarm.column.Longitude;
import com.eunwoo.ta_alarm.column.SeriouslyCnt;
import com.eunwoo.ta_alarm.column.SlightlyCnt;
import com.eunwoo.ta_alarm.column.SpotName;
import com.eunwoo.ta_alarm.customdata.CustomData;
import com.eunwoo.ta_alarm.customdata.CustomDataSharer;
import com.eunwoo.ta_alarm.customdata.StaticCustomDataList;
import com.eunwoo.ta_alarm.dialog.AddLocationDialog;
import com.eunwoo.ta_alarm.dialog.DeveloperInfoDialog;
import com.eunwoo.ta_alarm.dialog.ProviderInfoDialog;
import com.eunwoo.ta_alarm.dialog.RemoveLocationDialog;
import com.eunwoo.ta_alarm.room.AppDatabase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;

import com.naver.maps.map.OnMapReadyCallback;

import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        Overlay.OnClickListener,
        NaverMap.OnMapClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    String TAG = "LARS";

    //UI ?????? ?????? ??????
    private DrawerLayout drawerLayout;
    private MenuItem navMenuItem1;
    private View header;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CircleImageView photoImageView;
    private TextView nameTextView;

    private final int SINGLE_PERMISSION = 1004; //?????? ??????

    private final int SIGN_IN_REQUEST_CODE = 3000;

    private DatabaseReference mFirebaseDatabaseReference;

    public static final String CUSTOMDATA_CHILD = "customdata";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    ArrayList<String> spot_name = new ArrayList<>(); // ??????????????????
    ArrayList<Double> longitude = new ArrayList<>(); // ??????
    ArrayList<Double> latitude = new ArrayList<>(); // ??????
    ArrayList<Integer> caslt_cnt = new ArrayList<>(); // ????????? ???
    ArrayList<Integer> dth_dnv_cnt = new ArrayList<>(); //????????? ???
    ArrayList<Integer> se_dnv_cnt = new ArrayList<>(); // ????????? ???
    ArrayList<Integer> sl_dnv_cnt = new ArrayList<>(); // ????????? ???

    AppDatabase db;

    StaticCustomDataList staticCustomDataList = new StaticCustomDataList();

    BackgroundExecutor executor  = new BackgroundExecutor();
    Handler handler  = new Handler();

    public static final String GPS_PROVIDER = "gps";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Marker> customMarkers = new ArrayList<>();

    public static ArrayList<CircleOverlay> circleOverlays = new ArrayList<>();

    public static ArrayList<CircleOverlay> customCircleOverlays = new ArrayList<>();

    int CDatasize, CustumSize;

    Intent intent;

    int mTALocationColor = Color.RED;

    int mStartPointCnt;
    public static CircleOverlay SearchingOverlay;

    CameraUpdate cameraUpdate;
    InfoWindow infoWindow;

    final Handler delayHandler = new Handler();

    //????????? SearchingOverlay??? ??????
    LatLngBounds lastSearchingOverlay;

    ForegroundService foregroundService;

    SharedPreferences sharedPreferences;

    CustomDataSharer customDataSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mStartPointCnt = 0;

        initLayout();
    }

    private void initLayout() {
        //setContentView()??? ???????????? ?????? setRequestedOrientation()??? ??????????????????

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        db = AppDatabase.getAppDatabase(this);

        sharedPreferences = new SharedPreferences();

        mapFragment.getMapAsync(this);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //?????? ????????? ??????
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        navMenuItem1 = navigationView.getMenu().findItem(R.id.nav_menu1);

        // ????????? ?????? ????????? ?????? ??? ?????? View
        nameTextView = header.findViewById(R.id.nameTextView);
        photoImageView = header.findViewById(R.id.photoImageView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser(); //???????????? ????????? ??? ????????? NULL

        if (mFirebaseUser == null) {
            //???????????? ?????? ????????? ??????
            navMenuItem1.setTitle("?????????");
            return;
        } else {
            //???????????? ?????? ??????
            setProfile();

            //???????????????????????? ???????????? ????????? ????????? DatabaseReference??? ??????????????? ?????????.
            setFirebaseDatabaseReference();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        quitApp();

        //?????? ?????? ??????????????? ???????????? ?????? ??? ???????????? ?????? ??????.
//        ActivityCompat.finishAffinity(MainActivity.this);
//        System.exit(0);

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");


        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        // ???????????? ?????? ?????? ?????? ???????????? ?????? ??????
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == 33) {
                // ???????????? ?????? ????????? ?????? ??? User??????
                mFirebaseUser = mFirebaseAuth.getCurrentUser(); //???????????? ????????? ??? ????????? NULL

                setProfile();

                //???????????????????????? ???????????? ????????? ????????? DatabaseReference??? ??????????????? ?????????.
                setFirebaseDatabaseReference();

                // ????????? ??? ????????? ????????????????????? ???????????? ??????
                staticCustomDataList.clearCustomData();
                setAllCustomData();

                // ???????????? ?????????
                addAllCustomOverlays();
                setAllCustomOverlays();

                refreshSearchingOverlay(locationSource.getLastLocation());
            }
        }
    }

    //?????? ?????? ?????? ??????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    //?????? ?????? ?????? ?????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searching_refresh :
                if (mStartPointCnt == 1) {
                    refreshSearchingOverlay(locationSource.getLastLocation());
                }
                return true;
            case R.id.add_location :
                // Toast.makeText(this, "add_location", Toast.LENGTH_SHORT).show();
                if (mStartPointCnt == 1) {
                    AddLocationDialog addLocationDialog = new AddLocationDialog(this,
                            new AddLocationDialog.addLocationDialogListener() {
                                @Override
                                public void onClick(EditText locationName) {

                                    if (mFirebaseUser == null) {
                                        //???????????? ?????? ????????? ??????
                                        onClickGuest(locationName);
                                    } else {
                                        //???????????? ?????? ??????
                                        onClickUser(locationName);
                                    }

                                }
                            });
                }
                return true;
            case R.id.remove_location : //????????? remove_location????????? ????????? ??????
                // Toast.makeText(this, "remove_location", Toast.LENGTH_SHORT).show();
                if (mStartPointCnt == 1) {
                    if (mFirebaseUser == null) {
                        // ????????? X
                        RemoveLocationDialog removeLocationDialog = new RemoveLocationDialog(MainActivity.this,
                                new RemoveLocationDialog.removeLocationDialogListener() {
                                    @Override
                                    public void onClick() {
                                        refreshSearchingOverlay(locationSource.getLastLocation());
                                    }
                                });
                    } else {
                        // ????????? O
                        // ????????? ?????? ??? ?????? ??????..
                        RemoveLocationDialog removeLocationDialog = new RemoveLocationDialog(MainActivity.this,
                                mFirebaseUser,
                                mFirebaseDatabaseReference,
                                new RemoveLocationDialog.removeLocationDialogListener() {
                                    @Override
                                    public void onClick() {
                                        refreshSearchingOverlay(locationSource.getLastLocation());
                                    }
                                });
                    }
                }

                return true;
            case android.R.id.home :
                // Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show();

                drawerLayout.openDrawer(Gravity.LEFT);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //?????? ??????
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_menu1:
                if (mFirebaseUser != null) {
                    signOutGoogle();
                } else {
                    Intent googleSignInIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
                    startActivityForResult(googleSignInIntent, SIGN_IN_REQUEST_CODE);
                }
                break;
            case R.id.nav_menu2:
                ProviderInfoDialog providerInfoDialog = new ProviderInfoDialog(this);
                break;
            case R.id.nav_menu3:
                DeveloperInfoDialog developerInfoDialog = new DeveloperInfoDialog(this);
                break;
        }

        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void onClickGuest(EditText locationName) {
        // ????????? ?????? ????????? ????????? ?????? ?????? ?????????..
        if (locationName.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????...", Toast.LENGTH_SHORT).show();
            //sharedPreferences??? containsPreferences??? ????????? ???????????? ????????? ?????? ?????? ???????????? ??????...
        } else if (sharedPreferences.containsPreferences(getApplicationContext(), locationName.getText().toString())) {

            Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????? ????????? ??????????????????...", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();

            //???????????? ??????????????? ???????????? ?????? ??????
//            Log.i(TAG, "?????? : " + locationName.getText() + ", Latitude : " +
//                    locationSource.getLastLocation().getLatitude() +
//                    ", Longitude : " + locationSource.getLastLocation().getLongitude());

            //SharedPreferences??? ????????? ????????? ??????
            sharedPreferences.savePreferences(getApplicationContext(), locationName.getText().toString(),
                    locationSource.getLastLocation().getLongitude() + "," +
                            locationSource.getLastLocation().getLatitude());

            //??????????????? sharedPreferneces??? ???????????? ??? ?????? ????????? ?????? ??? ?????? ???????????? ??????.
            customDataSharer.addCustomData(locationName.getText().toString(), locationSource.getLastLocation());

            //????????????
            refreshSearchingOverlay(locationSource.getLastLocation());
        }

    }

    private void onClickUser(EditText locationName) {
        // Toast.makeText(getApplicationContext(), "onClickUser", Toast.LENGTH_SHORT).show();

        if (locationName.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????...", Toast.LENGTH_SHORT).show();
            //sharedPreferences??? containsPreferences??? ????????? ???????????? ????????? ?????? ?????? ???????????? ??????...
        } else if (staticCustomDataList.customName.contains(locationName.getText().toString())) {

            Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????? ????????? ??????????????????...", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();

            // ???????????? ???????????? ?????? ???????????? ??????
            CustomData customData = new CustomData(locationName.getText().toString(),
                    locationSource.getLastLocation().getLatitude(),
                    locationSource.getLastLocation().getLongitude()
            );
            // Log.d(TAG, " ????????? : " + mFirebaseDatabaseReference);

            // ????????? ??????...
//        mFirebaseDatabaseReference.child(mFirebaseUser.getUid())
//                .child(CUSTOMDATA_CHILD)
//                .child(push())
//                .setValue(customData);

            mFirebaseDatabaseReference.child(mFirebaseUser.getUid())
                    .child(CUSTOMDATA_CHILD)
                    .child(locationName.getText().toString())
                    .setValue(customData);
            // DataChange ?????? ????????? ????????? ?????? ????????? ????????????????????? ?????? ????????? ?????? ????????? ?????? ???????????? ????????? ???.
        }


    }

    @Override
    public void onBackPressed() {
        // AlertDialog ????????? ????????? ????????? ???????????? ?????? ?????????
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("?????????????????????????");

        // "???" ????????? ????????? ???????????? ?????????
        alBuilder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                quitApp();
                //?????? ?????? ??????????????? ???????????? ?????? ??? ???????????? ?????? ??????.
//                ActivityCompat.finishAffinity(MainActivity.this);
//                System.exit(0);
            }
        });
        // "?????????" ????????? ????????? ???????????? ?????????
        alBuilder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // ????????? ????????? ?????? ?????? ????????????
            }
        });
        alBuilder.setTitle("???????????? ??????");
        alBuilder.show(); // AlertDialog.Bulider??? ?????? AlertDialog??? ????????????.
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult On");
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // ?????? ?????????
                Log.d(TAG, "!locationSource.isActivated()");
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }

        /*
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){//???????????????
            Log.d(TAG, "onRequestPermissionsResult ACCESS_FINE_LOCATION  ");
            Intent begin = new Intent(MainActivity.this, MainActivity.class);
            startActivity(begin);
        }
         */
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        try {
            this.naverMap = naverMap;


            naverMap.setLocationSource(locationSource);

            naverMap.setLocationTrackingMode(LocationTrackingMode.Face);

            UiSettings uiSettings = naverMap.getUiSettings();

            uiSettings.setLocationButtonEnabled(true);
            uiSettings.setZoomControlEnabled(false);

            CameraUpdate cameraUpdate = CameraUpdate.zoomTo(12);
            naverMap.moveCamera(cameraUpdate);

            SearchingOverlay = new CircleOverlay();

            // ?????? ????????? ???
            naverMap.addOnLocationChangeListener(location ->
                    aNearbySearch(location, uiSettings)
            );

            //????????? ??????
            naverMap.setOnMapClickListener(this);

            //?????? ??? ????????? ???????????? ???????????? ??????
            infoWindow = new InfoWindow();
            //?????? ??? ??? ?????????
            infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
                @NonNull
                @Override
                protected View getContentView(@NonNull InfoWindow infoWindow) {

                    Marker marker = infoWindow.getMarker();
                    StringBuffer sb = (StringBuffer) marker.getTag();
                    View view = View.inflate(MainActivity.this, R.layout.black_spot_info_window, null);
                    ((TextView) view.findViewById(R.id.data)).setText(sb);
                    return view;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //?????? ???????????? ???
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if (overlay instanceof Marker) {
            Marker marker = (Marker) overlay;

            if (marker.getInfoWindow() != null) {
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }

    //?????? ????????? ?????? ?????? ???
    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        // ????????? ??????
        if (infoWindow.getMarker() != null) {

            infoWindow.close();
        }
    }

    public void setAllTrafficAccidentData(){

        List<SpotName> spotNameList = db.todoDao().SelectSpotName();
        List<CasltCnt> casltCntList = db.todoDao().SelectCasltCnt();
        List<DeathCnt> deathCntList = db.todoDao().SelectDeathCnt();
        List<SeriouslyCnt> seriouslyCntList = db.todoDao().SelectSeriouslyCnt();
        List<SlightlyCnt> slightlyCntList = db.todoDao().SelectSlightlyCnt();
        List<Longitude> longitudeList = db.todoDao().SelectLongitude();
        List<Latitude> latitudeList = db.todoDao().SelectLatitude();
        int size = spotNameList.size();

        for (int i = 0; i < size; i++) {
            spot_name.add(spotNameList.get(i).getSpotName());
            caslt_cnt.add(casltCntList.get(i).getCaslt_cnt());
            dth_dnv_cnt.add(deathCntList.get(i).getDth_dnv_cnt());
            se_dnv_cnt.add(seriouslyCntList.get(i).getSe_dnv_cnt());
            sl_dnv_cnt.add(slightlyCntList.get(i).getSl_dnv_cnt());
            longitude.add(longitudeList.get(i).getLongitude());
            latitude.add(latitudeList.get(i).getLatitude());
        }

        CDatasize = spot_name.size();

        setAllCustomData();

    }

    private void setCustomDataOfFirebase() {
        // T
        mFirebaseDatabaseReference.child(mFirebaseUser.getUid())
                .child(CUSTOMDATA_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Toast.makeText(getApplicationContext(), "addValueEventListener", Toast.LENGTH_LONG).show();
                staticCustomDataList.clearCustomData();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    CustomData customData = ds.getValue(CustomData.class);

                    staticCustomDataList.addCustomName(customData.customName);
                    staticCustomDataList.addCustomla(customData.customLatitude);
                    staticCustomDataList.addCustomlo(customData.customLongitude);
                }



                refreshSearchingOverlay(locationSource.getLastLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllCustomData() {

        //????????? ????????? ????????? ???????????? ??????
        if (mFirebaseUser == null) {
            // Guest
            customDataSharer = new CustomDataSharer(getApplicationContext());
        } else {
            // User
            setCustomDataOfFirebase();
        }
    }

    public void addAllOverlays() {
        //Cdata
        for (int i = 0; i < CDatasize; i++) {
            if (SearchingOverlay.getBounds().contains(new LatLng(latitude.get(i), longitude.get(i)))) {
                Marker marker = new Marker();
                CircleOverlay circleOverlay = new CircleOverlay();
                StringBuffer spotdata = new StringBuffer();

                spotdata.append(spot_name.get(i) + "\n");
                spotdata.append("????????? ??? : " + caslt_cnt.get(i) + "\n");
                spotdata.append("????????? ??? : " + dth_dnv_cnt.get(i) + "\n");
                spotdata.append("????????? ??? : " + se_dnv_cnt.get(i) + "\n");
                spotdata.append("????????? ??? : " + sl_dnv_cnt.get(i));
                marker.setTag(spotdata);
                marker.setPosition(new LatLng(latitude.get(i), longitude.get(i)));
                markers.add(marker);

                circleOverlay.setCenter(new LatLng(latitude.get(i),longitude.get(i)));
                circleOverlay.setColor(mTALocationColor);
                circleOverlay.setRadius(100);
                circleOverlays.add(circleOverlay);
            }
        }

        //custom
        addAllCustomOverlays();
    }

    public void addAllCustomOverlays() {
        for (int i = 0; i < staticCustomDataList.customLo.size(); i++) {
            if (SearchingOverlay.getBounds().contains(new LatLng(staticCustomDataList.customLa.get(i), staticCustomDataList.customLo.get(i)))) {
                Marker marker = new Marker();
                CircleOverlay circleOverlay = new CircleOverlay();
                StringBuffer spotdata = new StringBuffer();

                spotdata.append("(???????????? ????????? ????????????) \n" + staticCustomDataList.customName.get(i) + "\n");

                marker.setTag(spotdata);
                marker.setPosition(new LatLng(staticCustomDataList.customLa.get(i), staticCustomDataList.customLo.get(i)));
                customMarkers.add(marker);

                circleOverlay.setCenter(new LatLng(staticCustomDataList.customLa.get(i), staticCustomDataList.customLo.get(i)));
                circleOverlay.setColor(mTALocationColor);
                circleOverlay.setRadius(100);
                customCircleOverlays.add(circleOverlay);
            }
        }
    }

    public void setAllOverlays(){
        //Cdata set
        for (int i = 0; i < circleOverlays.size(); i++){
            circleOverlays.get(i).setMap(naverMap);
        }
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setMap(naverMap);
            markers.get(i).setOnClickListener(this);
        }

        //CustomData
        setAllCustomOverlays();
    }

    public void setAllCustomOverlays() {
        for (int i = 0; i < customCircleOverlays.size(); i++) {
            customCircleOverlays.get(i).setMap(naverMap);
        }
        for (int i = 0; i < customMarkers.size(); i++) {
            customMarkers.get(i).setMap(naverMap);
            customMarkers.get(i).setOnClickListener(this);
        }
    }

    public void removeAllOverlays(){
        //Cdata ???????????? ??????
        for (int i = 0; i < circleOverlays.size(); i++){
            circleOverlays.get(i).setMap(null);
        }
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setMap(null);
        }

        //Custom data ???????????? ?????? ??? ?????????
        removeAllCustomData();

        //Cdata ?????????
        circleOverlays.clear();

        markers.clear();
    }



    private void removeAllCustomData() {
        for (int i = 0; i < customCircleOverlays.size(); i++) {
            customCircleOverlays.get(i).setMap(null);
        }
        for (int i = 0; i < customMarkers.size(); i++) {
            customMarkers.get(i).setMap(null);
        }

        //Custom data ?????????
        customCircleOverlays.clear();
        customMarkers.clear();
    }

    private void aNearbySearch(Location location, UiSettings uiSettings) {
        //addOnLocationChangeListener??? ?????? ?????? ????????? ??? ???????????? ?????? ????????? ???????????? cpu???????????? ?????? ???????????? ?????? ?????? ????????? ????????? ????????? ?????????
        if (mStartPointCnt == 0) {
            Log.d(TAG, "A_Nearby_Search");
            setSearchingOverlay(location);

            uiSettings.setZoomControlEnabled(true);

            //??????????????? ???????????? ??????????????? ?????? ????????? ?????????????????? ????????? ??????

            new Thread(() -> {
                setAllTrafficAccidentData();

                handler.post(() -> {
                    MainActivity.this.addAllOverlays();
                    MainActivity.this.setAllOverlays();
                    mStartPointCnt = 1;
                });
                intent = new Intent(this, ForegroundService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                    startService(intent);
                }

                foregroundService = new ForegroundService();
            }).start();


        }

        outSearchingOverlay(location);

    }
    //SearchingOverlay??? set???
    private void setSearchingOverlay(Location location) {
        SearchingOverlay.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        SearchingOverlay.setRadius(5000); //100 = 100m
        SearchingOverlay.setOutlineWidth(2);
        SearchingOverlay.setOutlineColor(Color.BLUE);
        SearchingOverlay.setMap(naverMap);

        lastSearchingOverlay = SearchingOverlay.getBounds();

        //????????? SearchingOverlay??? Bounds??? ??????.

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SearchingOverlay.setMap(null);
            }
        }, 2000);
    }

    // ?????? ?????????
    private void refreshSearchingOverlay(Location location) {

        executor.execute(() -> {

            handler.post(() -> {
                //???????????? ??????????????? ?????? ?????? ????????? ????????? ?????? ??????
                removeAllOverlays();

                setSearchingOverlay(location);

                //???????????? ???????????? ??????
                addAllOverlays();
                //????????? ??????
                setAllOverlays();

                //??????????????? ???????????? ??????????????? ?????? ??? ?????? ??????????????? ???????????? circleOverlay???????????? ??????
                foregroundService.setCircleOverlaysCustom(customCircleOverlays);

                foregroundService.setCircleOverlays(circleOverlays);
            });

        });

        Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
    }

    //???????????????
    //????????? SearchingOverlay????????? ???????????? ???????????? ??????..A_Nearby_Search?????? ?????? ??????

    private void outSearchingOverlay(Location location) {
        if (!lastSearchingOverlay.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
            refreshSearchingOverlay(location);
        }
    }

    // ??? ?????? ??????
    private void quitApp() {
        stopService(intent);
        finish();
    }

    private void signOutGoogle() {
        mFirebaseAuth.signOut();
        // User??? ?????? ?????? ??????????????? ???????????? ??? ?????? ?????????
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        staticCustomDataList.clearCustomData();
        nameTextView.setText(R.string.please_sign_in);
        nameTextView.setTextColor(Color.GRAY);
        photoImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                R.drawable.ic_baseline_account_circle_24));
        navMenuItem1.setTitle("?????????");
        Toast.makeText(getApplicationContext(), "???????????? ??????", Toast.LENGTH_LONG).show();

        // ???????????? ????????? ????????? ??? ?????? ???????????? ??????...
        staticCustomDataList.clearCustomData();
        setAllCustomData();
        refreshSearchingOverlay(locationSource.getLastLocation());
    }

    private void setFirebaseDatabaseReference() {
        //???????????????????????? ???????????? ????????? ????????? DatabaseReference??? ??????????????? ?????????.
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void setProfile() {
        //???????????? ?????? ??????
        if (mFirebaseUser == null) {
            // ????????? null??? ??????
            nameTextView.setText("null");
        } else {
            // ?????? ??????
            nameTextView.setText(mFirebaseUser.getDisplayName());
            navMenuItem1.setTitle("????????????");

            nameTextView.setTextColor(Color.BLACK);
            //???????????? ?????? ????????? ?????? ??????
            if (mFirebaseUser.getPhotoUrl() == null) {
                //????????? ?????? ??????
                photoImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                        R.drawable.ic_baseline_account_circle_24));
            } else {
                //????????? ?????? ?????? glide??? ????????? ???????????? ?????????
                Glide.with(MainActivity.this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .into(photoImageView);
            }
        }
    }
}