package com.main.usermanagement.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.models.entities.Certificate;
import com.main.usermanagement.models.enumerations.ERole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CertificateService {
    private static final String COLLECTION_NAME = "certificates";
    private final CollectionReference certificatesCollection;

    private ERole currRole = null;

    public CertificateService() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.certificatesCollection = db.collection(COLLECTION_NAME);
        currRole = UserService.getCurrRole();
    }

    public void getAllCertificates(ActionCallback action) {
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

    public void getCertificateById(String certificateID, ActionCallback action) {
        certificatesCollection.document(certificateID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Certificate certificate = documentSnapshot.toObject(Certificate.class);

                    if(certificate != null) {
                        certificate.setId(documentSnapshot.getId());
                        action.onSuccess(certificate);
                    }
                    else {
                        action.onFailure("Certificate does not exists!");
                    }
                })
                .addOnFailureListener(action::onError);
    }

    public void createCertificate(String name, String description, ActionCallback<Object> action) {
        if(currRole.equals(ERole.ROLE_ADMIN)) {
            Certificate certificate = new Certificate(name, description);
            certificatesCollection.add(certificate)
                    .addOnSuccessListener(unused -> action.onSuccess())
                    .addOnFailureListener(action::onError);
        }
        else {
            action.onFailure("Does not have permission!");
        }
    }

    public void updateCertificate(String id, Map<String, Object> updateProfile, ActionCallback<Object> action) {
        certificatesCollection.document(id).update(updateProfile)
                .addOnSuccessListener(unused -> action.onSuccess())
                .addOnFailureListener(action::onError);
    }

    public void deleteCertificate(String id, ActionCallback<Object> action) {
        certificatesCollection.document(id).delete()
                .addOnSuccessListener(unused -> action.onSuccess())
                .addOnFailureListener(action::onError);
    }


}
