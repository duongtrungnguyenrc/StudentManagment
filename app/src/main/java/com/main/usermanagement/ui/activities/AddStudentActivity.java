package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.main.usermanagement.R;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityAddStudentBinding;
import com.main.usermanagement.models.enumerations.EStatus;
import com.main.usermanagement.services.StudentService;

public class AddStudentActivity extends AppCompatActivity {

    private ActivityAddStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarAddStudent);

        Intent intent = getIntent();

        StudentService service = new StudentService();

        binding.btnSave.setOnClickListener(view -> {
            final String name = binding.inputStudentName.getText().toString();
            final int age = Integer.parseInt(binding.inputStudentAge.getText().toString());
            final String phone = binding.inputStudentPhone.getText().toString();
            service.addStudent(name, age, phone, EStatus.NORMAL, new ActionCallback<Object>() {
                @Override
                public void onSuccess() {
                    binding.inputStudentName.setText("");
                    binding.inputStudentAge.setText("");
                    binding.inputStudentPhone.setText("");
                    Toast.makeText(getBaseContext(), "Add student success", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getBaseContext(), "Add student failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getBaseContext(), "Add student failed: " + message, Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });

        binding.btnCancel.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();


        return(super.onOptionsItemSelected(item));
    }
}