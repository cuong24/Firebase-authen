package com.example.firebase_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.firebase_authen.model.Site;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SiteDetailActivity extends AppCompatActivity {

    private MapsFragment map;
    FirebaseFirestore db;
    private ArrayList<String> volunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_site_detail);
        Intent intent = getIntent();
        getSite(intent.getExtras().get("siteID").toString());
    }

    private void getSite(String siteID) {
        ArrayList<Site> sites = new ArrayList<>();
    }
}