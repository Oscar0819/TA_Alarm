package com.eunwoo.ta_alarm.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.eunwoo.ta_alarm.R;
import com.google.firebase.auth.FirebaseUser;

public class AddLocationDialog {

    private AddLocationDialog.addLocationDialogListener addLocationDialogListener;

    public AddLocationDialog(Context context, AddLocationDialog.addLocationDialogListener addLocationDialogListener) {

        this.addLocationDialogListener = addLocationDialogListener;

        //LayoutInflater 객체는 사용 준비
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate 메소드를 이용해 미리 선언해뒀던 addLocationdialog 레이아웃 파일을 객체화.
        ConstraintLayout addLocationLayout = (ConstraintLayout) vi.inflate(R.layout.addlocationdialog, null);

        //위험지역 이름값을 받을 EditText의 id값을 식별해서 객체화
        EditText locationName = addLocationLayout.findViewById(R.id.locationName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("위험지역 추가").setView(addLocationLayout)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // interface를 이용한 호출
                                addLocationDialogListener.onClick(locationName);
                            }
                        }).show();
    }

    public interface addLocationDialogListener {
        void onClick(EditText locationName);
    }

//    private TestDialogListener testDialogListener;
//
//    public TestDialog(Context context, TestDialogListener testDialogListener){
//        super(context);
//        this.testDialogListener = testDialogListener;
//    }
//
//    public interface TestDialogListener{
//        void clickBtn();
//    }
}