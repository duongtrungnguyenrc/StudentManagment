package com.main.usermanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;
import com.main.usermanagement.models.Certificate;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private Context context;

    private List<Certificate> certificates;

    public CertificateAdapter(Context context) {
        this.context = context;
    }

    public CertificateAdapter(Context context, List<Certificate> certificates) {
        this.context = context;
        this.certificates = certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtDescription;

        public ViewHolder(@NonNull View view) {
            super(view);

            this.txtName = view.findViewById(R.id.txt_name);
            this.txtDescription = view.findViewById(R.id.txt_description);
        }


        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }
    }

    @NonNull
    @Override
    public CertificateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_certificate, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapter.ViewHolder holder, int position) {
        Certificate certificate = certificates.get(position);
        holder.getTxtName().setText(certificate.getName());
        holder.getTxtDescription().setText(certificate.getDescription());
    }

    @Override
    public int getItemCount() {
        return certificates != null ? certificates.size() : 0;
    }
}
