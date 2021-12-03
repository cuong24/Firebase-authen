package com.example.firebase_authen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.firebase_authen.R;
import com.example.firebase_authen.fragment.MapsFragment;
import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.example.firebase_authen.repository.SiteRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
        bindUI();
        addOnClick();
//        UserProfile.deleteInstance(); // This is to start fresh with new user
        validateUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserProfile user = UserProfile.getInstance(null);
        if (UserProfile.getInstance(null) != null) {
            getAllSites();
            updateRole();
        }
    }

    private void bindUI(){
        btnFilter = findViewById(R.id.filter);
        btnCreateSite = findViewById(R.id.createSite);
        btnNotification = findViewById(R.id.notification);
        numOfVolunteers = findViewById(R.id.numOfVolunteers);
        leaderIdSpinner = findViewById(R.id.leaderIdSpinner);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment1);
    }

    private void addOnClick(){
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
    }

    private void validateUser() {
        UserProfile user = UserProfile.getInstance(null);
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
        }
    }

    private void updateRole(){
        UserProfile user = UserProfile.getInstance(null);
        if (user.getType() == UserType.NORMAL){
            btnCreateSite.setVisibility(View.INVISIBLE);
        }
    }

    private void getAllSites() {
        SiteRepository.getAllSites(new SiteRepository.getAllSitesCallBack() {
            @Override
            public void onCallBack(ArrayList<Site> sites) {
                updateSpinner(sites);
                map.refreshSites(sites);
            }
        });
    }

    private void filterSite() {
        SiteRepository.getAllSites(new SiteRepository.getAllSitesCallBack() {
            @Override
            public void onCallBack(ArrayList<Site> sites){
                map.refreshSites(sites);
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