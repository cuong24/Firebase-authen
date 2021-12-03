package com.example.firebase_authen.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.firebase_authen.SiteDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class User {
    private String name;
    String password;
    UserType type;
    String uid;
    FirebaseFirestore db;

    public User() {
        type = UserType.NORMAL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(Boolean isLeader) {
        if (isLeader) {
            this.type = UserType.LEADER;
        }
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return type;
    }

    public User fetchUser(String userID){
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
                            user.setType(document.get("type") == "LEADER");
                            user.setUid(document.getId());
                        }
                    }
                } else {
                    Log.d("User", "Cannot get user");
                }
            }
        });
        return user;
    }

    public void setUser(User user){
        db = FirebaseFirestore.getInstance();
        db.collection("user").document(user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User", "Set user successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("User", "Error writing document", e);
                    }
                });
    }
}
