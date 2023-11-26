package com.main.usermanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AccessHistoryAdapter extends RecyclerView.Adapter<AccessHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Date> accessRecords;

    public AccessHistoryAdapter(Context context, List<Date> accessRecords) {
        this.context = context;
        this.accessRecords = accessRecords;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtRecordTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtRecordTime = itemView.findViewById(R.id.txt_record_time);
        }

        public TextView getTxtRecordTime() {
            return txtRecordTime;
        }
    }
    @NonNull
    @Override
    public AccessHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_access_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessHistoryAdapter.ViewHolder holder, int position) {

        holder.getTxtRecordTime().setText(accessRecords.get(position).toLocaleString());
    }

    @Override
    public int getItemCount() {
        return accessRecords.size();
    }
}
