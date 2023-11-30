package com.main.usermanagement.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.main.usermanagement.callback.ActionCallback;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.models.enumerations.ERole;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserService {

    private final CollectionReference userProfileCollection;
    private final FirebaseAuth firebaseAuth;
    private final StorageReference storageReference;
    private static ERole currRole = null;

    public UserService(Context context) {
        FirebaseApp.initializeApp(context);
        this.firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        this.userProfileCollection = store.collection("users");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    public void createAccount(String email, String password, UserProfile newUserProfile, ActionCallback<Object> action) {
        this.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser account = firebaseAuth.getCurrentUser();
                assert account != null;
                createProfile(account.getUid(), newUserProfile, action);
            } else {
                action.onError(task.getException());
            }
        });
    }

    private void createProfile(String id, UserProfile newUserProfile, ActionCallback<Object> action) {
        if (newUserProfile.getImage() != null) {
            uploadImage(Uri.parse(newUserProfile.getImage())).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(uri -> {
                    newUserProfile.setImage(uri.toString());

                    userProfileCollection.document(id).set(newUserProfile)
                            .addOnSuccessListener(unused -> action.onSuccess())
                            .addOnFailureListener(action::onError);
                }).addOnFailureListener(action::onError);
            }).addOnFailureListener(action::onError);
        } else {
            userProfileCollection.document(id).set(newUserProfile)
                    .addOnSuccessListener(unused -> action.onSuccess())
                    .addOnFailureListener(action::onError);
        }
    }


    private UploadTask uploadImage(Uri uri) {
        final String key = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("image/" + key);
        return imageRef.putFile(uri);
    }

    public void authenticate(String email, String password, ActionCallback<FirebaseUser> action) {
        if(email.isEmpty() || password.isEmpty()) {
            action.onFailure("Please enter " + (email.isEmpty() ? "email" : "password"));
        }
        else {
            this.firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnSuccessListener(unused -> {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    accessRecord(firebaseUser.getUid());
                    getUserProfile(firebaseUser.getUid(), new ActionCallback<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile result) {
                            currRole = result.getRole();
                            action.onSuccess(firebaseUser);
                        }

                        @Override
                        public void onError(Exception e) {
                            action.onError(e);
                        }
                    });
                })
                .addOnFailureListener(action::onError);
        }
    }

    public void accessRecord(String uid) {
        Map<String, Object> updateProfile = new HashMap<>();
        getUserProfile(firebaseAuth.getCurrentUser().getUid(), new ActionCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                List<Date> accessRecords = profile.getAccessHistory();
                accessRecords.add(new Date());
                updateProfile.put("accessHistory", accessRecords);
                updateUserProfile(
                        uid,
                        updateProfile,
                        new ActionCallback<Object>() {}
                );
            }
        });
    }

    public void getUserProfile(String uid, ActionCallback<UserProfile> action) {
        userProfileCollection.document(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                UserProfile profile = task.getResult().toObject(UserProfile.class);
                action.onSuccess(profile);
            }
            else {
                action.onError(task.getException());
            }
        });
    }

    public void updateUserProfile(String uid, Map<String, Object> updateProfile, ActionCallback<Object> action) {
        if(updateProfile.getOrDefault("image", null) != null) {
            uploadImage((Uri) updateProfile.get("image")).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(uri -> {
                    updateProfile.replace("image", uri.toString());

                    userProfileCollection.document(uid).update(updateProfile)
                            .addOnSuccessListener(unused -> action.onSuccess())
                            .addOnFailureListener(action::onError);
                }).addOnFailureListener(action::onError);
            });
        }
        else {
            userProfileCollection.document(uid).update(updateProfile)
                    .addOnSuccessListener(unused -> action.onSuccess())
                    .addOnFailureListener(action::onError);
        }
    }

    public void deleteUser(String uid, ActionCallback<Object> action) {
        userProfileCollection.document(uid).delete()
                .addOnSuccessListener(unused -> action.onSuccess())
                .addOnFailureListener(action::onError);
    }

    public static ERole getCurrRole() {
        return currRole;
    }

    public void getAllUsers(ActionCallback<List<UserProfile>> action) {
        userProfileCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<UserProfile> users = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    UserProfile profile = document.toObject(UserProfile.class);
                    users.add(profile);
                }
                action.onSuccess(users);
            } else {
                action.onError(task.getException());
            }
        });
    }
}
