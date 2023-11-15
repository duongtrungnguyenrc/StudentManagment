package com.main.usermanagement.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.main.usermanagement.R;
import com.main.usermanagement.adapter.CertificateAdapter;
import com.main.usermanagement.callback.GetAllCertificatesCallback;
import com.main.usermanagement.models.Certificate;
import com.main.usermanagement.services.CertificateService;
import com.main.usermanagement.ui.skeleton.Skeleton;
import com.main.usermanagement.ui.skeleton.SkeletonScreen;

import java.util.List;

public class HomeFragment extends Fragment {

    private CertificateAdapter adapter;

    private SkeletonScreen skeletonScreen;

    private RecyclerView certificatesRecyclerView;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.certificatesRecyclerView = view.findViewById(R.id.recycler_view_certificates);
        CertificateService service = new CertificateService();
//        service.addCertificate("Ielts", "The international english certificate", new AddCertificateCallback() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
        this.adapter = new CertificateAdapter(getContext());

        certificatesRecyclerView.setAdapter(this.adapter);
        certificatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchCertificates(service);

        return view;
    }

    private void fetchCertificates(CertificateService service) {

        showSkeleton();
        service.getAllCertificates(new GetAllCertificatesCallback() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                hideSkeleton();
                adapter.setCertificates(certificates);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to get certificates: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void showSkeleton() {
        this.skeletonScreen = Skeleton.bind(certificatesRecyclerView)
                .adapter(this.adapter)
                .load(R.layout.layout_skeleton_student)
                .count(10)
                .duration(500)
                .show();
    }

    private void hideSkeleton() {
        this.skeletonScreen.hide();
    }
}