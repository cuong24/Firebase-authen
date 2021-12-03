package com.example.firebase_authen.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebase_authen.model.User;
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


    public static User getUser(String userID, getUserCallBack userCallBack){
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
                            user.setType(document.get("type").equals("LEADER"));
                            user.setUid(document.getId());
                            userCallBack.onCallBack(user);
                        }
                    }
                } else {
                    Log.d("User", "Cannot get user");
                }
            }
        });
        return user;
    }

    public static void addUser(User user, addUserCallBack userCallBack){
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        userCallBack.onCallBack(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userCallBack.onCallBack(false);
                    }
                });
    }

    public interface getUserCallBack{
        public void onCallBack(User user);
    }

    public interface addUserCallBack{
        public void onCallBack(boolean isSuccess);
    }
}
