package com.main.usermanagement.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityGrantAccountBinding;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.models.enumerations.ERole;
import com.main.usermanagement.models.enumerations.EStatus;
import com.main.usermanagement.services.UserService;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GrantAccountActivity extends AppCompatActivity {

    private ActivityGrantAccountBinding binding;
    private Uri avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityGrantAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<ERole> dataAdapter = new ArrayAdapter<ERole>((Context) this, android.R.layout.simple_spinner_item,
                Arrays.stream(ERole.values()).filter((role) -> role != ERole.ROLE_ADMIN).collect(Collectors.toList()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRole.setAdapter(dataAdapter);

        UserService userService = new UserService(this);

        binding.btnEditImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 11);
        });

        binding.btnCancel.setOnClickListener(view -> {
            finish();
        });

        binding.btnSave.setOnClickListener(view -> {
            userService.createAccount(
                    binding.inputEmail.getText().toString(),
                    binding.inputPassword.getText().toString(),
                    new UserProfile(
                            binding.inputName.getText().toString(),
                            Integer.parseInt(binding.inputAge.getText().toString()),
                            binding.inputPhone.getText().toString(),
                            EStatus.NORMAL,
                            ERole.valueOf(binding.spinnerRole.getSelectedItem().toString()),
                            (avatarUri != null ? avatarUri.toString() : null)
                    ),
                    new ActionCallback<Object>() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext() , "Successfully to grant new user", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext() , "Failed to grant new user with error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.avatarUri = data.getData();
            binding.imgPlaceholderAvatar.setImageURI(data.getData());
        }
    }
}