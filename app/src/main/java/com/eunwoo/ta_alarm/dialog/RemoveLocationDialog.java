package com.eunwoo.ta_alarm.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eunwoo.ta_alarm.MainActivity;
import com.eunwoo.ta_alarm.customdata.StaticCustomDataList;
import com.eunwoo.ta_alarm.R;
import com.eunwoo.ta_alarm.RemoveCustomDataAdapter;
import com.eunwoo.ta_alarm.SharedPreferences;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RemoveLocationDialog {

    StaticCustomDataList staticCustomDataList;
    SharedPreferences sharedPreferences;
    RemoveCustomDataAdapter adapter;

    int signInCheckCode;

    private RemoveLocationDialog.removeLocationDialogListener removeLocationDialogListener;

    public RemoveLocationDialog(Context context, RemoveLocationDialog.removeLocationDialogListener removeLocationDialogListener) {
        // Guest가 사용할 생성자

        this.signInCheckCode = 1000;
        staticCustomDataList = new StaticCustomDataList();
        sharedPreferences = new SharedPreferences();

        this.removeLocationDialogListener = removeLocationDialogListener;

        //LayoutInflater 객체 사용 준비
        LayoutInflater rl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate 메소드를 이용해 미리 선언해뒀던 remove_recyclerview 레이아웃 파일을 객체화.
        ConstraintLayout removeLocationLayout = (ConstraintLayout) rl.inflate(R.layout.remove_recyclerview, null);

        //다이얼로그 빌더 생성
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);

        //다이얼로그 빌더의 속성 설정
        builder.setTitle("위험지역 삭제").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다이얼로그만 사라지고 아무일도 일어나지않음
            }
            //Layout 파일을 객체화 한 것이 담아져있는 removeLocationLayout을 View로 설정..
        }).setView(removeLocationLayout).show(); //show메소드로 다이얼로그를 보여줌

        // recyclerView에 LayoutManager 지정...
        RecyclerView recyclerView = removeLocationLayout.findViewById(R.id.recycler);
        // rectclerView에 구분선 추가
        recyclerView.addItemDecoration(new DividerItemDecoration(context, 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.setHasFixedSize(true);

        //recyclerView에 정보를 표현하기위해 RemoveCustomDataAdapter 객체 생성과 동시에 CustomName값으로 초기화

        adapter = new RemoveCustomDataAdapter(staticCustomDataList.customName);
        removeData(context, null, null);

        //recyclerView 어댑터 설정
        recyclerView.setAdapter(adapter);
    }

    public RemoveLocationDialog(Context context,
                                FirebaseUser firebaseUser,
                                DatabaseReference firebaseDatabaseReference,
                                RemoveLocationDialog.removeLocationDialogListener removeLocationDialogListener) {
        // User가 사용할 생성자

        this.signInCheckCode = 2000;
        staticCustomDataList = new StaticCustomDataList();
        sharedPreferences = new SharedPreferences();

        this.removeLocationDialogListener = removeLocationDialogListener;

        //LayoutInflater 객체 사용 준비
        LayoutInflater rl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate 메소드를 이용해 미리 선언해뒀던 remove_recyclerview 레이아웃 파일을 객체화.
        ConstraintLayout removeLocationLayout = (ConstraintLayout) rl.inflate(R.layout.remove_recyclerview, null);

        //다이얼로그 빌더 생성
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);

        //다이얼로그 빌더의 속성 설정
        builder.setTitle("위험지역 삭제").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다이얼로그만 사라지고 아무일도 일어나지않음
            }
            //Layout 파일을 객체화 한 것이 담아져있는 removeLocationLayout을 View로 설정..
        }).setView(removeLocationLayout).show(); //show메소드로 다이얼로그를 보여줌

        // recyclerView에 LayoutManager 지정...
        RecyclerView recyclerView = removeLocationLayout.findViewById(R.id.recycler);
        // rectclerView에 구분선 추가
        recyclerView.addItemDecoration(new DividerItemDecoration(context, 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.setHasFixedSize(true);

        //recyclerView에 정보를 표현하기위해 RemoveCustomDataAdapter 객체 생성과 동시에 CustomName값으로 초기화

        adapter = new RemoveCustomDataAdapter(staticCustomDataList.customName);
        removeData(context, firebaseDatabaseReference, firebaseUser);

        //recyclerView 어댑터 설정
        recyclerView.setAdapter(adapter);
    }

    private void removeData(Context context, DatabaseReference firebaseDatabaseReference, FirebaseUser firebaseUser) {
        adapter.setOnItemClickListener(new RemoveCustomDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) { //pos를 통해 클릭한 아이템의 위치를 알 수 있음
                Toast.makeText(context, "item clicked. pos = " + pos, Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(context).setMessage("삭제하시겠습니까?")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (signInCheckCode == 1000) {
                                            // Guest
                                            //sharedPreferences에 원하는 데이터를 삭제

                                            sharedPreferences.removePreferences(context, staticCustomDataList.customName.get(pos));
                                            //메모리에 있는 데이터 삭제.
                                            //recyclerView의 데이터 순서와 ArrayList에 저장된 데이터의 인덱스는 같음.
                                            //메모리에 저장된 데이터를 안 지우면 sharedPreferences에 저장된 데이터를 삭제해도 지도에 계속 남아있음.

                                            staticCustomDataList.customName.remove(pos);
                                            staticCustomDataList.customLo.remove(pos);
                                            staticCustomDataList.customLa.remove(pos);

                                            //삭제 후 수정된 데이터를 지도에 갱신 시켜줌. 리스너 호출...
                                            removeLocationDialogListener.onClick();


                                            //삭제 후 어댑터에서도 지워서 recyclerView에 삭제된 아이템이 안 뜨게 만듬
                                            adapter.notifyItemRemoved(pos);
                                        } else {
                                            // User
                                            firebaseDatabaseReference.child(firebaseUser.getUid())
                                                    .child(MainActivity.CUSTOMDATA_CHILD)
                                                    .child(staticCustomDataList.customName.get(pos))
                                                    .removeValue();

                                            staticCustomDataList.customName.remove(pos);
                                            staticCustomDataList.customLo.remove(pos);
                                            staticCustomDataList.customLa.remove(pos);

                                            //삭제 후 어댑터에서도 지워서 recyclerView에 삭제된 아이템이 안 뜨게 만듬
                                            adapter.notifyItemRemoved(pos);

                                        }

                                    }
                                }).show();
            }
        });
    }

    public interface removeLocationDialogListener {
        void onClick();
    }
}
