package com.eunwoo.ta_alarm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RemoveCustomDataAdapter extends RecyclerView.Adapter<RemoveCustomDataAdapter.ViewHolder> {

    private ArrayList<String> CustomData = null;
    //어댑터 밖에서도 아이템 이벤트 처리를 하기위해
    //커스텀 인터페이스 (OnItemClickListener) 정의
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    //리스너 객체를 전달하는 메서드(setOnItemClickListener())와 전달된 객체를 저장할 변수(mListener) 추가
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;

        ViewHolder(View itemView) {
            super(itemView);

            //아이템 클릭 이벤트 핸들러 메서드에서 리스너 객체 메서드(onItemClick) 호출.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);

                        notifyItemChanged(pos);
                    }
                }
            });

            // 뷰 객체에 대한 참조
            textView1 = itemView.findViewById(R.id.textView);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음
    public RemoveCustomDataAdapter(ArrayList<String> list) {
        CustomData = list;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @NonNull
    @Override
    public RemoveCustomDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_item_view, parent, false);
        RemoveCustomDataAdapter.ViewHolder vh = new RemoveCustomDataAdapter.ViewHolder(view);
        return vh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템에 표시.
    @Override
    public void onBindViewHolder(@NonNull RemoveCustomDataAdapter.ViewHolder holder, int position) {
        String text = CustomData.get(position);
        holder.textView1.setText(text);
    }

    @Override
    public int getItemCount() {
        return CustomData.size();
    }

}
