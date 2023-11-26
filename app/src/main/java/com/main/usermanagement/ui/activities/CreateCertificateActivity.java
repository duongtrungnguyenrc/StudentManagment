package com.main.usermanagement.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityCreateCertificateBinding;
import com.main.usermanagement.services.CertificateService;

public class CreateCertificateActivity extends AppCompatActivity {

    private ActivityCreateCertificateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityCreateCertificateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(view -> finish());

        binding.btnSave.setOnClickListener(view -> {
            CertificateService service = new CertificateService();
            service.createCertificate(
                binding.inputName.getText().toString(),
                binding.inputDescription.getText().toString(),
                new ActionCallback<Object>() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getBaseContext(), "Successfully to create new certificate!", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getBaseContext(), "Failed to create new certificate: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getBaseContext(), "Failed to create new certificate: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            );
        });
    }
}