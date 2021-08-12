package com.eunwoo.ta_alarm.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.eunwoo.ta_alarm.R;

public class ProviderInfoDialog {

    public ProviderInfoDialog(Context context) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout developerInfoLayout = (ConstraintLayout) vi.inflate(R.layout.provider_info_dialog, null);

        TextView infoView = developerInfoLayout.findViewById(R.id.infoView);
        TextView infoView2 = developerInfoLayout.findViewById(R.id.infoView2);
        infoView.setText("데이터 제공자 : 도로교통공단");
        infoView2.setText("https://data.go.kr/data/15058925/openapi.do");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("제공자 정보")
                .setView(developerInfoLayout)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
