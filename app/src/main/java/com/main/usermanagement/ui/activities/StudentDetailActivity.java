package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.main.usermanagement.R;
import com.main.usermanagement.adapter.CertificateAdapter;
import com.main.usermanagement.callback.GetStudentByIdCallback;
import com.main.usermanagement.callback.UpdateStudentCallback;
import com.main.usermanagement.databinding.ActivityStudentDetailBinding;
import com.main.usermanagement.models.Certificate;
import com.main.usermanagement.models.Student;
import com.main.usermanagement.services.StudentService;

import java.util.List;

public class StudentDetailActivity extends AppCompatActivity {

    private RecyclerView studentCertificatesRecyclerView;

    private TextView txtId, txtName, txtPhone, txtAge;

    private ImageButton btnEditInformation, btnEditCertificate;

    private ActivityStudentDetailBinding binding;

    private Student student;

    private CertificateAdapter certificateAdapter;


    private List<Certificate> certificates;

    private PopupWindow background, editStudentInformationPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.studentCertificatesRecyclerView = binding.recyclerViewStudentCertificates;
        this.txtId = binding.txtId;
        this.txtName = binding.txtName;
        this.txtPhone = binding.txtPhone;
        this.txtAge = binding.txtAge;
        this.btnEditInformation = binding.btnEditInformation;
        this.btnEditCertificate = binding.btnEditCertificate;

        Intent intent = getIntent();

        String studentID = intent.getStringExtra("student_id");
        fetchStudent(studentID);

        this.certificateAdapter = new CertificateAdapter(this);

        studentCertificatesRecyclerView.setAdapter(certificateAdapter);
        studentCertificatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.btnEditInformation.setOnClickListener(view -> {
            showEditInformationBottomSheet(view);
        });
    }

    private void fetchStudent(String studentID) {
        StudentService service = new StudentService();
        service.getStudentById(studentID, new GetStudentByIdCallback() {
            @Override
            public void onSuccess(Student student) {
                setStudent(student);
                txtId.setText(student.getId());
                txtName.setText(student.getName());
                txtPhone.setText(student.getPhone());
                txtAge.setText(String.valueOf(student.getAge()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to fetch Student: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }



    private void setStudent(Student student) {
        this.student = student;
    }


    private void showEditInformationBottomSheet(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_student_information, null);

        this.background = new PopupWindow(
                getLayoutInflater().inflate(R.layout.bottom_sheet_background, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        background.setAnimationStyle(R.style.PopupBackgroundAnimation);

        this.editStudentInformationPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        editStudentInformationPopup.setAnimationStyle(R.style.PopupAnimation);

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        editStudentInformationPopup.setFocusable(true);

        background.showAtLocation(anchorView, Gravity.TOP, location[0], location[1]);
        editStudentInformationPopup.showAtLocation(anchorView, Gravity.TOP, location[0], location[1]);

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
                    new UpdateStudentCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getBaseContext(), "Successfully to update student", Toast.LENGTH_LONG).show();

                            txtName.setText(edtStudentName.getText().toString());
                            txtAge.setText(edtStudentAge.getText().toString());
                            txtPhone.setText(edtStudentPhone.getText().toString());
                            background.dismiss();
                            editStudentInformationPopup.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getBaseContext(), "Failed to update student: " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
            );
        });

        popupView.findViewById(R.id.btn_dismiss).setOnClickListener(view -> {
            background.dismiss();
            editStudentInformationPopup.dismiss();

        });

    }
}