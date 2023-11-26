package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.main.usermanagement.R;
import com.main.usermanagement.adapter.CertificateAdapter;
import com.main.usermanagement.adapter.StudentCertificateAdapter;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityStudentDetailBinding;
import com.main.usermanagement.models.entities.Certificate;
import com.main.usermanagement.models.entities.Student;
import com.main.usermanagement.models.enumerations.EStatus;
import com.main.usermanagement.services.CertificateService;
import com.main.usermanagement.services.StudentService;
import com.main.usermanagement.ui.components.BottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentDetailActivity extends AppCompatActivity {

    private ActivityStudentDetailBinding binding;

    private Student student;

    private StudentCertificateAdapter studentCertificateAdapter;

    private CertificateAdapter certificateAdapter;

    private List<Certificate> certificates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.certificates = new ArrayList<>();

        Intent intent = getIntent();

        String studentID = intent.getStringExtra("student_id");
        fetchStudent(studentID);

        this.studentCertificateAdapter = new StudentCertificateAdapter(this);

        binding.recyclerViewStudentCertificates.setAdapter(studentCertificateAdapter);
        binding.recyclerViewStudentCertificates.setLayoutManager(new LinearLayoutManager(this));

        this.binding.btnEditInformation.setOnClickListener(view -> {
            BottomSheet editStudentInformationBottomSheet = new BottomSheet(
                    R.layout.bottom_sheet_edit_student_information,
                    view,
                    getLayoutInflater(),
                    true
            );
            editStudentInformationBottomSheet.showBottomSheet((popupView, popupWindow, background) -> {
                editStudentInformationActionHandler(popupView, editStudentInformationBottomSheet);
            });
        });

        this.binding.btnEditCertificate.setOnClickListener(view -> {
            BottomSheet editStudentCertificatesBottomSheet = new BottomSheet(
                    R.layout.bottom_sheet_edit_student_certificates,
                    view,
                    getLayoutInflater(),
                    true
            );
            editStudentCertificatesBottomSheet.showBottomSheet((popupView, popupWindow, background) -> {
                editStudentCertificatesActionHandler(popupView, editStudentCertificatesBottomSheet);
            });
        });


    }

    private void fetchStudent(String studentID) {
        StudentService service = new StudentService();
        service.getStudentById(studentID, new ActionCallback<Student>() {
            @Override
            public void onSuccess(Student student) {
                setStudent(student);
                CertificateService certificateService = new CertificateService();
                setCertificates(new ArrayList<>());
                int expectedCertificatesCount = student.getCertificates().size();
                AtomicInteger receivedCertificatesCount = new AtomicInteger(0);

                for (DocumentReference certificateRef : student.getCertificates()) {
                    certificateService.getCertificateById(certificateRef.getId(), new ActionCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(Object result) {
                            if (result instanceof Certificate certificate) {
                                certificates.add(certificate);
                                int count = receivedCertificatesCount.incrementAndGet();
                                if (count == expectedCertificatesCount) {
                                    studentCertificateAdapter.setCertificates(certificates);
                                    studentCertificateAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            certificates.add(new Certificate("Invalid", "This certificate has expired"));
                            studentCertificateAdapter.setCertificates(certificates);
                            studentCertificateAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ex", e.getMessage());
                        }
                    });
                }

                EStatus studentStatus = student.getStatus();

                getBinding().imgStatus.setImageResource(studentStatus == EStatus.NORMAL ? R.drawable.ic_user_check_100 : R.drawable.ic_user_warning_100);
                getBinding().btnEditInformation.setVisibility(studentStatus == EStatus.NORMAL ? View.VISIBLE : View.GONE);
                getBinding().btnEditCertificate.setVisibility(studentStatus == EStatus.NORMAL ? View.VISIBLE : View.GONE);
                getBinding().txtId.setText(student.getId());
                getBinding().txtName.setText(student.getName());
                getBinding().txtPhone.setText(student.getPhone());
                getBinding().txtAge.setText(String.valueOf(student.getAge()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to fetch Student: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private ActivityStudentDetailBinding getBinding() {
        return this.binding;
    }

    private void setStudent(Student student) {
        this.student = student;
    }

    private void editStudentInformationActionHandler(View popupView, BottomSheet bottomSheet) {
        TextView txtStudentId = popupView.findViewById(R.id.txt_student_id);
        EditText edtStudentName = popupView.findViewById(R.id.edt_name);
        EditText edtStudentAge = popupView.findViewById(R.id.edt_age);
        EditText edtStudentPhone = popupView.findViewById(R.id.edt_phone);
        Button btnSave = popupView.findViewById(R.id.btn_save_student);

        txtStudentId.setText(student.getId());
        edtStudentName.setText(student.getName());
        edtStudentAge.setText(String.valueOf(student.getAge()));
        edtStudentPhone.setText(student.getPhone());

        btnSave.setOnClickListener(view -> {
            StudentService service = new StudentService();
            service.updateStudent(
                student.getId(),
                edtStudentName.getText().toString(),
                Integer.parseInt(edtStudentAge.getText().toString()),
                edtStudentPhone.getText().toString(),
                new ActionCallback<Object>() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getBaseContext(), "Successfully to update student", Toast.LENGTH_LONG).show();

                        getBinding().txtName.setText(edtStudentName.getText().toString());
                        getBinding().txtAge.setText(edtStudentAge.getText().toString());
                        getBinding().txtPhone.setText(edtStudentPhone.getText().toString());
                        bottomSheet.dismiss();
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getBaseContext(), "Failed to update student: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getBaseContext(), "Update student failed: " + message, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            );
        });
    }

    private void editStudentCertificatesActionHandler(View popupView, BottomSheet bottomSheet) {
        RecyclerView editStudentCertificatesRecyclerView = popupView.findViewById(R.id.recycler_view_edit_student_certificates);

        StudentService studentService = new StudentService();
        CertificateService certificateService = new CertificateService();
        this.certificateAdapter = new CertificateAdapter(StudentDetailActivity.this);

        certificateService.getAllCertificates(new ActionCallback() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof List) {
                    List<?> resultList = (List<?>) result;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Certificate) {
                        List<Certificate> certificates = (List<Certificate>) result;
                        certificateAdapter.setCertificates(certificates);
                        certificateAdapter.setCheckedCertificates(getCertificates());
                        editStudentCertificatesRecyclerView.setAdapter(certificateAdapter);
                        editStudentCertificatesRecyclerView.setLayoutManager(new LinearLayoutManager(StudentDetailActivity.this));
                    }
                }

            }
        });

        Button btnSaveStudentCertificatesUpdate = popupView.findViewById(R.id.btn_save_certificates_update);

        btnSaveStudentCertificatesUpdate.setOnClickListener(view -> {
            studentService.grantStudentCertificates(
                    student.getId(),
                    certificateAdapter.getCheckedCertificates(),
                    new ActionCallback<Object>() {
                @Override
                public void onSuccess() {
                    StudentCertificateAdapter adapter = getStudentCertificateAdapter();
                    adapter.setCertificates(getCertificateAdapter().getCheckedCertificates());
                    Toast.makeText(
                            getApplicationContext(),
                            "Successfully to grant certificate",
                            Toast.LENGTH_LONG).show();
                    bottomSheet.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to grant certificate with error: " +  e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getBaseContext(), "Grant student certificate failed: " + message, Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    public StudentCertificateAdapter getStudentCertificateAdapter() {
        return this.studentCertificateAdapter;
    }

    public CertificateAdapter getCertificateAdapter() {
        return this.certificateAdapter;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }


}