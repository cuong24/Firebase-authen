package com.example.firebase_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.User;
import com.example.firebase_authen.model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MapsFragment map;
    Button btnFilter, btnCreateSite, btnNotification;
    EditText numOfVolunteers;
    Spinner leaderIdSpinner;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFilter = findViewById(R.id.filter);
        btnCreateSite = findViewById(R.id.createSite);
        btnNotification = findViewById(R.id.notification);
        numOfVolunteers = findViewById(R.id.numOfVolunteers);
        leaderIdSpinner = findViewById(R.id.leaderIdSpinner);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment1);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSite();
            }
        });
        btnCreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSite();
            }
        });
        UserProfile.deleteInstance();
        validateUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllSites();
    }

    private void validateUser() {
        UserProfile user = UserProfile.getInstance(null);
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
        }
    }

    private void getAllSites() {
        ArrayList<Site> sites = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        Query dbSites = db.collection("sites").whereEqualTo("closed", false);
        dbSites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Site newSite = new Site((Double) document.get("longitude"),
                                (Double) document.get("latitude"),
                                new ArrayList<>(),
                                (String) document.get("leaderID"));
                        newSite.setSiteID(document.getId());
                        sites.add(newSite);
                        updateSpinner(sites);
                    }
                    map.refreshSites(sites);
                } else {
                    Toast.makeText(MainActivity.this, "Error getting data. Please check you internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterSite() {
        ArrayList<Site> sites = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        Query dbSites = db.collection("sites").whereEqualTo("closed", false);
        if (!numOfVolunteers.getText().toString().equals("")) {
            dbSites = dbSites.whereEqualTo("noVolunteers", Integer.parseInt(numOfVolunteers.getText().toString()));
        }
        if (leaderIdSpinner.getSelectedItem() != null && leaderIdSpinner.getSelectedItem().toString() != "All") {
            dbSites = dbSites.whereEqualTo("leaderID", leaderIdSpinner.getSelectedItem().toString());
        }
        dbSites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Site newSite = new Site((Double) document.get("longitude"),
                                (Double) document.get("latitude"),
                                new ArrayList<>(),
                                (String) document.get("leaderID"));
                        newSite.setSiteID(document.getId());
                        sites.add(newSite);
                    }
                    map.refreshSites(sites);
                } else {
                    Toast.makeText(MainActivity.this, "Error getting data. Please check you internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSpinner(ArrayList<Site> sites) {
        ArraySet<String> leaderIDs = new ArraySet<>();
        for (Site site : sites) {
            leaderIDs.add(site.getLeaderID());
        }
        leaderIDs.add("All");
        ArrayList<String> leaderIdOptions = new ArrayList<>(leaderIDs);
        ArrayAdapter adp = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, leaderIdOptions);
        leaderIdSpinner.setAdapter(adp);
    }

    private void createSite() {
        Intent intent = new Intent(MainActivity.this, CreateSiteActivity.class);
        startActivity(intent);
    }
}