package com.main.usermanagement.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.main.usermanagement.R;
import com.main.usermanagement.databinding.ActivityMainBinding;
import com.main.usermanagement.ui.fragments.DashboardFragment;
import com.main.usermanagement.ui.fragments.CertificateFragment;
import com.main.usermanagement.ui.fragments.SettingFragment;
import com.main.usermanagement.ui.viewmodels.ViewModelImplementation;


public class MainActivity extends AppCompatActivity {

    private ViewModelImplementation viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.viewModel = new ViewModelProvider(this).get(ViewModelImplementation.class);

        if (viewModel.getCurrentFragment().getValue() == null) {
            loadFragment(new DashboardFragment());
        } else {
            loadFragment(viewModel.getCurrentFragment().getValue());
        }

        binding.navigationBottom.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.action_dashboard) {
                loadFragment(DashboardFragment.Init.getInstance());
                return true;
            }
            else if(item.getItemId() == R.id.action_certificate) {
                loadFragment(CertificateFragment.Init.getInstance());
                return true;
            }
            else if(item.getItemId() == R.id.action_setting) {
                loadFragment(SettingFragment.Init.getInstance());
                return true;
            }
            else
            return false;
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
        viewModel.setCurrentFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}