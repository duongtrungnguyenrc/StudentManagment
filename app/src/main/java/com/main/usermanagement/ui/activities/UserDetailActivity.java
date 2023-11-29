package com.main.usermanagement.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.usermanagement.R;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.ActivityUserInformationBinding;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.services.UserService;

import java.util.HashMap;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity {

    private ActivityUserInformationBinding binding;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private Uri avatarUri;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityUserInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userProfile = getIntent().getParcelableExtra("user");

        if (userProfile != null) {
            displayUserDetails(userProfile);
        } else {
            new UserService(getApplicationContext()).getUserProfile(currentUser.getUid(), new ActionCallback<UserProfile>() {
                @Override
                public void onSuccess(UserProfile profile) {
                    userProfile = profile;
                    displayUserDetails(profile);
                }

                @Override
                public void onError(Exception e) {
                    ActionCallback.super.onError(e);
                    // Handle the error
                }
            });
        }

        binding.btnEditImg.setOnClickListener(v -> {
            getImage();
        });

        binding.btnCancel.setOnClickListener(v -> {
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            Map<String, Object> updateProfile = new HashMap<>();
            if (!binding.edtName.getText().toString().isEmpty())
                updateProfile.put("name", binding.edtName.getText().toString());
            if (!binding.edtAge.getText().toString().isEmpty())
                updateProfile.put("age", Integer.parseInt(binding.edtAge.getText().toString()));
            if (!binding.edtPhone.getText().toString().isEmpty())
                updateProfile.put("phone", binding.edtPhone.getText().toString());
            if (avatarUri != null)
                updateProfile.put("image", avatarUri);

            new UserService(getApplicationContext()).updateUserProfile(
                    currentUser.getUid(),
                    updateProfile,
                    new ActionCallback<Object>() {
                        @Override
                        public void onSuccess() {
                            ActionCallback.super.onSuccess();
                            Toast.makeText(getApplicationContext(), "Successfully updated " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            ActionCallback.super.onError(e);
                            Toast.makeText(getApplicationContext(), "Failed to update with error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
    }

    private void displayUserDetails(UserProfile profile) {
        Glide.with(getApplicationContext())
                .load(profile.getImage())
                .into(binding.imgAvatar);

        binding.txtHeadingName.setText(profile.getName());
        binding.txtHeadingEmail.setText(profile.getPhone());
        binding.edtName.setHint(profile.getName());
        binding.edtAge.setHint(String.valueOf(profile.getAge()));
        binding.edtPhone.setHint(profile.getPhone());
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 11);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            binding.imgAvatar.setImageURI(data.getData());
            this.avatarUri = data.getData();
        }
    }
}