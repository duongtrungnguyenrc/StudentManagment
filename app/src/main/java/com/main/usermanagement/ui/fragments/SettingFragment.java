package com.main.usermanagement.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.usermanagement.R;
import com.main.usermanagement.adapter.AccessHistoryAdapter;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.databinding.FragmentSettingBinding;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.models.enumerations.ERole;
import com.main.usermanagement.services.UserService;
import com.main.usermanagement.ui.activities.GrantAccountActivity;
import com.main.usermanagement.ui.activities.LoginActivity;
import com.main.usermanagement.ui.activities.userInformationActivity;
import com.main.usermanagement.ui.components.BottomSheet;

public class SettingFragment extends Fragment {

    public static class Init {
        private static SettingFragment instance = null;
        public static SettingFragment getInstance() {
            if(instance == null) {
                instance = new SettingFragment();
            }
            return instance;
        }
    }

    public FragmentSettingBinding binding;

    private FirebaseUser currentUser;

    public SettingFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentSettingBinding.inflate(inflater);

        binding.itemAccount.setOnClickListener(item -> {
            Intent intent = new Intent(getContext(), userInformationActivity.class);
            startActivityForResult(intent, 1);
        });

        binding.itemGrantAccount.setOnClickListener(item -> {
            Intent intent = new Intent(getContext(), GrantAccountActivity.class);
            startActivityForResult(intent, 1);
        });

        binding.itemLogOut.setOnClickListener(item -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("Log out your account?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        binding.itemAccessHistory.setOnClickListener(v -> {
            BottomSheet userAccessHistoryBottomSheet = new BottomSheet(
                    R.layout.bottom_sheet_access_history,
                    v,
                    getLayoutInflater(),
                    true
            );

            userAccessHistoryBottomSheet.showBottomSheet((popupView, popupWindow, background) -> {
                accessHistoryActionHandler(popupView, userAccessHistoryBottomSheet);
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUser();
    }

    public void loadUser() {
        if (currentUser != null)
            new UserService(getContext()).getUserProfile(currentUser.getUid(), new ActionCallback<UserProfile>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(UserProfile profile) {
                    binding.txtPlaceholderName.setText(profile.getName());
                    binding.txtPlaceholderEmail.setText("@" + currentUser.getUid());
                    Context context = getActivity();
                    if(profile.getImage() != null && !profile.getImage().isEmpty() && context != null) {
                        Glide.with(context)
                                .load(profile.getImage())
                                .into(binding.imgPlaceholderAvatar);
                    }
                    if (profile.getRole() != ERole.ROLE_ADMIN )
                        binding.layoutMoreSetting.setVisibility(View.GONE);
                    binding.layoutLoading.setVisibility(View.GONE);
                }
            });
    }

    public void accessHistoryActionHandler(View popupView, BottomSheet bottomSheet) {
        RecyclerView userAccessHistoryRecyclerView = popupView.findViewById(R.id.recycler_view_access_history);

        new UserService(getContext()).getUserProfile(currentUser.getUid(), new ActionCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                ActionCallback.super.onSuccess(profile);

                AccessHistoryAdapter adapter = new AccessHistoryAdapter(getContext(), profile.getAccessHistory());
                userAccessHistoryRecyclerView.setAdapter(adapter);
                userAccessHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onError(Exception e) {
                ActionCallback.super.onError(e);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            loadUser();
        }
    }
}