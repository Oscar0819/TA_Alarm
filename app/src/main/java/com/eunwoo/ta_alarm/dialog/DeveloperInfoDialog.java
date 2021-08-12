package com.eunwoo.ta_alarm.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.DialogCompat;

import com.eunwoo.ta_alarm.R;

public class DeveloperInfoDialog {

    public DeveloperInfoDialog(Context context) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout developerInfoLayout = (ConstraintLayout) vi.inflate(R.layout.developer_info_dialog, null);

        TextView infoView = developerInfoLayout.findViewById(R.id.infoView);
        TextView infoView2 = developerInfoLayout.findViewById(R.id.infoView2);
        infoView.setText("앱 오류 / 건의사항은 아래 이메일로 보내주세요!");
        infoView2.setText("dn0963@naver.com");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("문의")
                .setView(developerInfoLayout)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
