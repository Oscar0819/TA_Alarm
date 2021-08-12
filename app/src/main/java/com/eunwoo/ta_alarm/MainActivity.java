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

    //UI 구현 객체 생성
    private DrawerLayout drawerLayout;
    private MenuItem navMenuItem1;
    private View header;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CircleImageView photoImageView;
    private TextView nameTextView;

    private final int SINGLE_PERMISSION = 1004; //권한 변수

    private final int SIGN_IN_REQUEST_CODE = 3000;

    private DatabaseReference mFirebaseDatabaseReference;

    public static final String CUSTOMDATA_CHILD = "customdata";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    ArrayList<String> spot_name = new ArrayList<>(); // 위험지역이름
    ArrayList<Double> longitude = new ArrayList<>(); // 경도
    ArrayList<Double> latitude = new ArrayList<>(); // 위도
    ArrayList<Integer> caslt_cnt = new ArrayList<>(); // 사상자 수
    ArrayList<Integer> dth_dnv_cnt = new ArrayList<>(); //사망자 수
    ArrayList<Integer> se_dnv_cnt = new ArrayList<>(); // 중상자 수
    ArrayList<Integer> sl_dnv_cnt = new ArrayList<>(); // 경상자 수

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

    //마지막 SearchingOverlay의 위치
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
        //setContentView()가 호출되기 전에 setRequestedOrientation()가 호출되어야함

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


        //네비 구현을 위한
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        navMenuItem1 = navigationView.getMenu().findItem(R.id.nav_menu1);

        // 네비의 구글 프로필 사진 및 이름 View
        nameTextView = header.findViewById(R.id.nameTextView);
        photoImageView = header.findViewById(R.id.photoImageView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser(); //로그인을 안하면 이 객체는 NULL

        if (mFirebaseUser == null) {
            //로그인을 하지 않았을 경우
            navMenuItem1.setTitle("로그인");
            return;
        } else {
            //로그인을 했을 경우
            setProfile();

            //데이터베이스에서 데이터를 읽거나 쓰려면 DatabaseReference의 인스턴스가 필요함.
            setFirebaseDatabaseReference();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        quitApp();

        //여러 메인 엑티비티가 쌓여있는 것을 다 종료하기 위해 사용.
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

        // 로그인이 되면 바로 뷰를 설정하기 위한 코드
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == 33) {
                // 로그인이 되면 프로필 설정 및 User갱신
                mFirebaseUser = mFirebaseAuth.getCurrentUser(); //로그인을 안하면 이 객체는 NULL

                setProfile();

                //데이터베이스에서 데이터를 읽거나 쓰려면 DatabaseReference의 인스턴스가 필요함.
                setFirebaseDatabaseReference();

                // 로그인 후 실시간 데이터베이스의 데이터를 적용
                staticCustomDataList.clearCustomData();
                setAllCustomData();

                // 오버레이 재설정
                addAllCustomOverlays();
                setAllCustomOverlays();

                refreshSearchingOverlay(locationSource.getLastLocation());
            }
        }
    }

    //툴바 액션 버튼 세팅
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    //툴바 액션 터치 이벤트
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
                                        //로그인을 하지 않았을 경우
                                        onClickGuest(locationName);
                                    } else {
                                        //로그인을 했을 경우
                                        onClickUser(locationName);
                                    }

                                }
                            });
                }
                return true;
            case R.id.remove_location : //툴바의 remove_location버튼을 누르면 작동
                // Toast.makeText(this, "remove_location", Toast.LENGTH_SHORT).show();
                if (mStartPointCnt == 1) {
                    if (mFirebaseUser == null) {
                        // 로그인 X
                        RemoveLocationDialog removeLocationDialog = new RemoveLocationDialog(MainActivity.this,
                                new RemoveLocationDialog.removeLocationDialogListener() {
                                    @Override
                                    public void onClick() {
                                        refreshSearchingOverlay(locationSource.getLastLocation());
                                    }
                                });
                    } else {
                        // 로그인 O
                        // 생성자 인자 한 개를 추가..
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

    //네비 콜백
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
        // 이름이 비어 있거나 중복될 경우 다시 재요청..
        if (locationName.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), "위험지역 이름을 입력해주세요...", Toast.LENGTH_SHORT).show();
            //sharedPreferences의 containsPreferences를 이용해 중복되는 이름이 있을 경우 메세지를 날림...
        } else if (sharedPreferences.containsPreferences(getApplicationContext(), locationName.getText().toString())) {

            Toast.makeText(getApplicationContext(), "이름이 중복됩니다 다른 이름을 입력해주세요...", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "위험지역 지정 완료", Toast.LENGTH_SHORT).show();

            //데이터가 정상적인지 확인하기 위한 로그
//            Log.i(TAG, "제목 : " + locationName.getText() + ", Latitude : " +
//                    locationSource.getLastLocation().getLatitude() +
//                    ", Longitude : " + locationSource.getLastLocation().getLongitude());

            //SharedPreferences에 이름과 위경도 저장
            sharedPreferences.savePreferences(getApplicationContext(), locationName.getText().toString(),
                    locationSource.getLastLocation().getLongitude() + "," +
                            locationSource.getLastLocation().getLatitude());

            //위험지역을 sharedPreferneces에 추가했을 때 바로 지도에 올릴 수 있게 메모리에 추가.
            customDataSharer.addCustomData(locationName.getText().toString(), locationSource.getLastLocation());

            //새로고침
            refreshSearchingOverlay(locationSource.getLastLocation());
        }

    }

    private void onClickUser(EditText locationName) {
        // Toast.makeText(getApplicationContext(), "onClickUser", Toast.LENGTH_SHORT).show();

        if (locationName.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), "위험지역 이름을 입력해주세요...", Toast.LENGTH_SHORT).show();
            //sharedPreferences의 containsPreferences를 이용해 중복되는 이름이 있을 경우 메세지를 날림...
        } else if (staticCustomDataList.customName.contains(locationName.getText().toString())) {

            Toast.makeText(getApplicationContext(), "이름이 중복됩니다 다른 이름을 입력해주세요...", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "위험지역 지정 완료", Toast.LENGTH_SHORT).show();

            // 위험지역 데이터를 담은 인스턴스 생성
            CustomData customData = new CustomData(locationName.getText().toString(),
                    locationSource.getLastLocation().getLatitude(),
                    locationSource.getLastLocation().getLongitude()
            );
            // Log.d(TAG, " 데이ㅌ : " + mFirebaseDatabaseReference);

            // 프로토 타입...
//        mFirebaseDatabaseReference.child(mFirebaseUser.getUid())
//                .child(CUSTOMDATA_CHILD)
//                .child(push())
//                .setValue(customData);

            mFirebaseDatabaseReference.child(mFirebaseUser.getUid())
                    .child(CUSTOMDATA_CHILD)
                    .child(locationName.getText().toString())
                    .setValue(customData);
            // DataChange 콜백 메소드 코드에 갱신 코드가 입력되어있어서 유저 코드는 갱신 코드를 따로 입력하지 않아도 됨.
        }


    }

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                quitApp();
                //여러 메인 엑티비티가 쌓여있는 것을 다 종료하기 위해 사용.
//                ActivityCompat.finishAffinity(MainActivity.this);
//                System.exit(0);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult On");
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                Log.d(TAG, "!locationSource.isActivated()");
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }

        /*
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){//권한있을때
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

            // 수정 테스트 중
            naverMap.addOnLocationChangeListener(location ->
                    aNearbySearch(location, uiSettings)
            );

            //리스너 등록
            naverMap.setOnMapClickListener(this);

            //정보 창 객체를 생성하고 어댑터를 지정
            infoWindow = new InfoWindow();
            //정보 창 뷰 띄우기
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

    //마커 클릭했을 때
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

    //지도 화면을 클릭 했을 때
    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        // 정보창 닫기
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

        //커스텀 데이터 공유자 인스턴스 생성
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
                spotdata.append("사상자 수 : " + caslt_cnt.get(i) + "\n");
                spotdata.append("사망자 수 : " + dth_dnv_cnt.get(i) + "\n");
                spotdata.append("중상자 수 : " + se_dnv_cnt.get(i) + "\n");
                spotdata.append("경상자 수 : " + sl_dnv_cnt.get(i));
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

                spotdata.append("(사용자가 지정한 위험지역) \n" + staticCustomDataList.customName.get(i) + "\n");

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
        //Cdata 지도에서 삭제
        for (int i = 0; i < circleOverlays.size(); i++){
            circleOverlays.get(i).setMap(null);
        }
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setMap(null);
        }

        //Custom data 지도에서 삭제 및 초기화
        removeAllCustomData();

        //Cdata 초기화
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

        //Custom data 초기화
        customCircleOverlays.clear();
        customMarkers.clear();
    }

    private void aNearbySearch(Location location, UiSettings uiSettings) {
        //addOnLocationChangeListener에 의해 계속 실행될 시 메모리를 계속 추가로 차지하고 cpu사용량이 점점 늘어나는 것에 대한 팅김과 느려짐 방지용 조건문
        if (mStartPointCnt == 0) {
            Log.d(TAG, "A_Nearby_Search");
            setSearchingOverlay(location);

            uiSettings.setZoomControlEnabled(true);

            //백그라운드 스레드와 포그라운드 작동 시기를 조절하기위해 여기로 옮김

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
    //SearchingOverlay를 set함
    private void setSearchingOverlay(Location location) {
        SearchingOverlay.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        SearchingOverlay.setRadius(5000); //100 = 100m
        SearchingOverlay.setOutlineWidth(2);
        SearchingOverlay.setOutlineColor(Color.BLUE);
        SearchingOverlay.setMap(naverMap);

        lastSearchingOverlay = SearchingOverlay.getBounds();

        //마지막 SearchingOverlay의 Bounds를 저장.

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SearchingOverlay.setMap(null);
            }
        }, 2000);
    }

    // 갱신 메소드
    private void refreshSearchingOverlay(Location location) {

        executor.execute(() -> {

            handler.post(() -> {
                //데이터를 삭제안하면 위에 계속 쌓여서 메모리 누수 발생
                removeAllOverlays();

                setSearchingOverlay(location);

                //메모리에 데이터를 올림
                addAllOverlays();
                //지도에 표현
                setAllOverlays();

                //백그라운드 상태에서 위험지역을 읽을 수 있게 포그라운드 서비스에 circleOverlay데이터를 저장
                foregroundService.setCircleOverlaysCustom(customCircleOverlays);

                foregroundService.setCircleOverlays(circleOverlays);
            });

        });

        Toast.makeText(this, "갱신", Toast.LENGTH_SHORT).show();
    }

    //시뮬레이터
    //마지막 SearchingOverlay영역을 벗어나면 자동으로 갱신..A_Nearby_Search함수 안에 있음

    private void outSearchingOverlay(Location location) {
        if (!lastSearchingOverlay.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
            refreshSearchingOverlay(location);
        }
    }

    // 앱 종료 함수
    private void quitApp() {
        stopService(intent);
        finish();
    }

    private void signOutGoogle() {
        mFirebaseAuth.signOut();
        // User도 새로 값을 설정해줘야 로그아웃 한 것을 인식함
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        staticCustomDataList.clearCustomData();
        nameTextView.setText(R.string.please_sign_in);
        nameTextView.setTextColor(Color.GRAY);
        photoImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                R.drawable.ic_baseline_account_circle_24));
        navMenuItem1.setTitle("로그인");
        Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_LONG).show();

        // 로그아웃 상태가 되었을 때 다시 위험지역 검색...
        staticCustomDataList.clearCustomData();
        setAllCustomData();
        refreshSearchingOverlay(locationSource.getLastLocation());
    }

    private void setFirebaseDatabaseReference() {
        //데이터베이스에서 데이터를 읽거나 쓰려면 DatabaseReference의 인스턴스가 필요함.
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void setProfile() {
        //로그인을 했을 경우
        if (mFirebaseUser == null) {
            // 객체가 null일 경우
            nameTextView.setText("null");
        } else {
            // 아닐 경우
            nameTextView.setText(mFirebaseUser.getDisplayName());
            navMenuItem1.setTitle("로그아웃");

            nameTextView.setTextColor(Color.BLACK);
            //사용자의 구글 프로필 사진 설정
            if (mFirebaseUser.getPhotoUrl() == null) {
                //사진이 없을 경우
                photoImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                        R.drawable.ic_baseline_account_circle_24));
            } else {
                //사진이 있을 경우 glide를 이용해 이미지를 뿌려줌
                Glide.with(MainActivity.this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .into(photoImageView);
            }
        }
    }
}