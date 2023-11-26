package com.main.usermanagement.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.main.usermanagement.R;
import com.main.usermanagement.adapter.CertificateAdapter;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.callback.SwipeToDeleteCallback;
import com.main.usermanagement.databinding.FragmentCertificateBinding;
import com.main.usermanagement.models.entities.Certificate;
import com.main.usermanagement.services.CertificateService;
import com.main.usermanagement.ui.activities.CreateCertificateActivity;
import com.main.usermanagement.ui.skeleton.Skeleton;
import com.main.usermanagement.ui.skeleton.SkeletonScreen;

import java.util.List;

public class CertificateFragment extends Fragment {

    private CertificateAdapter adapter;

    private SkeletonScreen skeletonScreen;

    private FragmentCertificateBinding binding;

    private CertificateService service;

    public static class Init {
        private static CertificateFragment instance = null;
        public static CertificateFragment getInstance() {
            if(instance == null) {
                instance = new CertificateFragment();
            }
            return instance;
        }
    }

    public CertificateFragment() {
        this.service = new CertificateService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentCertificateBinding.inflate(inflater);

        this.binding.btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CreateCertificateActivity.class);
            startActivityForResult(intent, 111);
        });

        this.adapter = new CertificateAdapter(getContext());
        this.binding.recyclerViewEditStudentCertificates.setAdapter(this.adapter);
        this.binding.recyclerViewEditStudentCertificates.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipeToDelete();

        fetchCertificates();

        return binding.getRoot();
    }

    private void fetchCertificates() {

        showSkeleton();
        service.getAllCertificates(new ActionCallback<List<Certificate>>() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                hideSkeleton();
                adapter.setCertificates(certificates);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to get certificates: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                adapter.deleteCertificate(position);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(this.binding.recyclerViewEditStudentCertificates);
    }

    private void showSkeleton() {
        this.skeletonScreen = Skeleton.bind(this.binding.recyclerViewEditStudentCertificates)
                .adapter(this.adapter)
                .load(R.layout.layout_skeleton_student)
                .count(10)
                .duration(500)
                .show();
    }

    private void hideSkeleton() {
        this.skeletonScreen.hide();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK){
            fetchCertificates();
        }
    }
}