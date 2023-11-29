package com.main.usermanagement.ui.activities;//package com.main.usermanagement.ui.activities;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;
import com.main.usermanagement.adapter.UserAdapter;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.services.UserService;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.ui.fragments.SettingFragment;

import java.util.List;

import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;



public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserService userService;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Initialize UserService
        userService = new UserService(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rVListUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        // Fetch user profiles from the server
        fetchUserProfiles();

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFragment settingFragment = new SettingFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, settingFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void fetchUserProfiles() {
        userService.getAllUsers(new ActionCallback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> userProfiles) {
                // Display the user profiles in the RecyclerView
                userAdapter.setUserProfiles(userProfiles);
            }

            @Override
            public void onError(Exception e) {
                // Show an error message
                Toast.makeText(UserListActivity.this, "Failed to fetch user profiles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUserItemClick(UserProfile userProfile) {
        // Open UserDetailActivity to show detailed information about the selected user
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user", userProfile);
        startActivity(intent);
    }
}