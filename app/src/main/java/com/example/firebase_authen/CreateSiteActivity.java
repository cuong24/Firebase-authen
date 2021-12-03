package com.example.firebase_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.User;
import com.example.firebase_authen.model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreateSiteActivity extends AppCompatActivity {

    private CreateSiteFragment createSiteFragment;
    Button btnCreateSite;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_site);
        createSiteFragment = (CreateSiteFragment) getSupportFragmentManager().findFragmentById(R.id.create_site_fragment);
        btnCreateSite = findViewById(R.id.createSite);
        btnCreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSite();
            }
        });
    }

    private void createSite(){
        UserProfile user = UserProfile.getInstance(null);
        Site site = new Site(createSiteFragment.marker.getPosition().longitude,
                             createSiteFragment.marker.getPosition().latitude,
                             new ArrayList<>(),user.getUid());
        db = FirebaseFirestore.getInstance();

        db.collection("sites")
                .add(site)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateSiteActivity.this, "Cannot create new user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}