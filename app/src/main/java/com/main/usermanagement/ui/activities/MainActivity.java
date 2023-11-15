package com.main.usermanagement.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.usermanagement.R;
import com.main.usermanagement.databinding.ActivityMainBinding;
import com.main.usermanagement.ui.fragments.DashboardFragment;
import com.main.usermanagement.ui.fragments.HomeFragment;
import com.main.usermanagement.ui.fragments.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView mBottomBar = binding.navigationBottom;

        loadFragment(new DashboardFragment());

        mBottomBar.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.action_dashboard) {
                loadFragment(new DashboardFragment());
                return true;
            }
            else if(item.getItemId() == R.id.action_certificate) {
                loadFragment(new HomeFragment());
                return true;
            }
            else if(item.getItemId() == R.id.action_setting) {
                loadFragment(new SettingFragment());
                return true;
            }
            else
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}