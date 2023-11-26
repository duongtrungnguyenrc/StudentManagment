package com.main.usermanagement.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.models.entities.Certificate;
import com.main.usermanagement.models.entities.Student;
import com.main.usermanagement.services.CertificateService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {
    private Context context;

    private List<Certificate> certificates;

    private List<Certificate> checkedCertificates;
    private final CertificateService service = new CertificateService();

    public CertificateAdapter(Context context) {
        this.context = context;
    }

    public CertificateAdapter(Context context, List<Certificate> certificates) {
        this.context = context;
        this.certificates = certificates;
        this.checkedCertificates = new ArrayList<>();
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
        this.checkedCertificates = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtDescription;
        private CheckBox cbChecked;


        public ViewHolder(@NonNull View view) {
            super(view);

            this.txtName = view.findViewById(R.id.txt_name);
            this.txtDescription = view.findViewById(R.id.txt_description);
            this.cbChecked = view.findViewById(R.id.cb_checked);
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }

        public CheckBox getCbChecked() {
            return cbChecked;
        }
    }

    @NonNull
    @Override
    public CertificateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_certificate, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapter.ViewHolder holder, int position) {
        Certificate certificate = certificates.get(position);
        holder.getTxtName().setText(certificate.getName());
        holder.getTxtDescription().setText(certificate.getDescription());

        checkedCertificates.forEach(item -> {
            if(item instanceof  Certificate
                && item.getId() != null
                && !item.getId().isEmpty()
                && item.getId().equalsIgnoreCase(certificates.get(position).getId())) {

                holder.getCbChecked().setChecked(true);
            }
        });

        holder.getCbChecked().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                this.checkedCertificates.add(certificates.get(position));
            } else {
                this.checkedCertificates = checkedCertificates.stream().filter(checkedCertificate -> {
                    if(checkedCertificate.getId() != null)
                        return !checkedCertificate.getId().equals(certificates.get(position).getId());
                    return false;
                }).collect(Collectors.toList());
            }
        });
    }

    @Override
    public int getItemCount() {
        return certificates != null ? certificates.size() : 0;
    }

    public List<Certificate> getCheckedCertificates() {
        return this.checkedCertificates;
    }

    public void setCheckedCertificates(List<Certificate> checkedCertificates) {
        this.checkedCertificates = checkedCertificates;
    }

    public void deleteCertificate(int position) {
        final Certificate certificate = certificates.get(position);

        new AlertDialog.Builder(context)
                .setMessage("Delete " + certificate.getName() + " ?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    service.deleteCertificate(certificate.getId(), new ActionCallback() {
                        @Override
                        public void onSuccess() {
                            certificates.remove(position);
                            notifyDataSetChanged();

                            View rootView = ((Activity) context).findViewById(android.R.id.content);

                            Snackbar snackbar = Snackbar
                                    .make(rootView, "Student from the list.", Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", view -> {
//                                addStudent(student, position);
                                Toast.makeText(context, "Undo successfully", Toast.LENGTH_LONG).show();
                            });

                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
