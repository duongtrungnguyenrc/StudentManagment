package com.main.usermanagement.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;
import com.main.usermanagement.adapter.UserAdapter;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.services.UserService;
import com.main.usermanagement.callback.ActionCallback;

import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserService userService;
    private ImageButton deleteButton;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Initialize UserService
        userService = new UserService(this);

        recyclerView = findViewById(R.id.rVListUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        fetchUserProfiles();

        deleteButton = findViewById(R.id.btn_remove);
        btnBack = findViewById(R.id.btnBack);
        deleteButton.setVisibility(View.GONE); // Ẩn nút delete ban đầu

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserListActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedItems();

            }
        });

        userAdapter.setOnItemSelectedListener(hasSelectedItems -> {
            if (hasSelectedItems) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        });
    }

    private void fetchUserProfiles() {
        userService.getAllUsers(new ActionCallback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> userProfiles) {
                userAdapter.setUserProfiles(userProfiles);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(UserListActivity.this, "Failed to fetch user profiles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedItems() {
        List<UserProfile> selectedItems = userAdapter.getSelectedItems();

        for (UserProfile selectedItem : selectedItems) {
            userService.deleteUser(selectedItem.getId(), new ActionCallback<Object>() {
                @Override
                public void onSuccess() {
                    userAdapter.removeUser(selectedItem);
                    Toast.makeText(UserListActivity.this, "Deleted selected items", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(UserListActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void onUserItemClick(UserProfile userProfile) {
        // Open UserDetailActivity to show detailed information about the selected user
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user", userProfile);
        startActivity(intent);
    }
}