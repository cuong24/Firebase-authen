package com.example.firebase_authen.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebase_authen.model.User;
import com.example.firebase_authen.model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {
    private static FirebaseFirestore db;

    public static void getUser(String userID, GetUserCallBack getUserCallBack) {
        db = FirebaseFirestore.getInstance();
        Query dbSites = db.collection("user");
        User user = new User();
        dbSites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(userID)) {
                            user.setName((String) document.get("name"));
                            user.setPassword((String) document.get("password"));
                            user.setType(document.get("type").equals("LEADER"), document.get("type").equals("SUPER_USER"));
                            user.setUid(document.getId());
                            getUserCallBack.onCallBack(user);
                        }
                    }
                } else {
                    Log.d("User", "Cannot get user");
                }
            }
        });
    }

    public static void addUser(User user, addUserCallBack addUserCallBack) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addUserCallBack.onCallBack(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addUserCallBack.onCallBack(false);
                    }
                });
    }

    public static void validateUser(String userName, String password, validateUserCallBack validateUserCallBack) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("password").equals(password)) {

                                    User user = new User();
                                    user.setName((String) document.get("name"));
                                    user.setPassword((String) document.get("password"));
                                    user.setType(document.get("type").equals("LEADER"), document.get("type").equals("SUPER_USER"));
                                    user.setUid(document.getId());
                                    UserProfile.getInstance(user);
                                    validateUserCallBack.onCallBack(true, true);
                                } else {
                                    validateUserCallBack.onCallBack(false, true);
                                }
                            }
                        } else {
                            validateUserCallBack.onCallBack(false, false);
                        }
                    }
                });
    }

    public static void getUserName(String userID, GetUserNameCallBack getUserNameCallBack) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(userID)) {
                                    getUserNameCallBack.onCallBack((String) document.get("name"), true);
                                }
                            }
                        } else {
                            getUserNameCallBack.onCallBack(null, false);
                        }
                    }
                });
    }

    public interface GetUserCallBack {
        default void onCallBack(User user) {
        }
    }

    public interface addUserCallBack {
        public void onCallBack(boolean isSuccess);
    }

    public interface validateUserCallBack {
        void onCallBack(boolean isValidUser, boolean getAccounts);
    }

    public interface GetUserNameCallBack {
        void onCallBack(String userName, boolean isSuccess);
    }
}
