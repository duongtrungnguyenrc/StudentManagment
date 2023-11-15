package com.main.usermanagement.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.usermanagement.callback.AddCertificateCallback;
import com.main.usermanagement.callback.GetAllCertificatesCallback;
import com.main.usermanagement.models.Certificate;

import java.util.ArrayList;
import java.util.List;

public class CertificateService {
    private static final String COLLECTION_NAME = "certificates";
    private FirebaseFirestore db;
    private CollectionReference certificatesCollection;

    public CertificateService() {
        this.db = FirebaseFirestore.getInstance();
        this.certificatesCollection = db.collection(COLLECTION_NAME);
    }

    public void getAllCertificates(GetAllCertificatesCallback action) {
        List<Certificate> certificates = new ArrayList<>();

        certificatesCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Certificate certificate = document.toObject(Certificate.class);
                            certificate.setId(document.getId());
                            certificates.add(certificate);
                        }
                        action.onSuccess(certificates);
                    } else {
                        action.onError(task.getException());
                    }
                });
    }

    public void addCertificate(String name, String description, AddCertificateCallback action) {
        Certificate certificate = new Certificate(name, description);
        certificatesCollection.add(certificate).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                action.onSuccess();
            }
            else {
                action.onError(task.getException());
            }
        });
    }
}
