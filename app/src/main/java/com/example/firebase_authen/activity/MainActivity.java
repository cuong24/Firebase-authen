package com.example.firebase_authen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.firebase_authen.R;
import com.example.firebase_authen.fragment.MapsFragment;
import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.example.firebase_authen.repository.SiteRepository;
import com.example.firebase_authen.service.NotificationService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapsFragment map;
    Button btnFilter, btnCreateSite, btnNotification, logout;
    EditText numOfVolunteers;
    Spinner leaderIdSpinner;
    private SearchView searchView;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindUI();
        addOnClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserProfile user = UserProfile.getInstance(null);
        validateUser();
        if (user != null) {
            getAllSites();
            updateRole();
            Intent intent = new Intent(MainActivity.this, NotificationService.class);
            startService(intent);
        }
    }

    private void bindUI() {
        logout = findViewById(R.id.logout);
        btnFilter = findViewById(R.id.filter);
        btnCreateSite = findViewById(R.id.createSite);
        btnNotification = findViewById(R.id.notification);
        numOfVolunteers = findViewById(R.id.numOfVolunteers);
        leaderIdSpinner = findViewById(R.id.leaderIdSpinner);
        searchView = findViewById(R.id.idSearchView);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment1);
    }

    private void addOnClick() {
        searchView.setOnQueryTextListener(MapsFragment.searchLocation(this, searchView, map));
        logout.setOnClickListener(view -> {
            logout();
        });
        btnFilter.setOnClickListener(view -> {
            filterSite();
        });
        btnCreateSite.setOnClickListener(view -> {
            createSite();
        });
        btnNotification.setOnClickListener(view -> {
            goToNotification();
        });
    }

    private void logout() {
        UserProfile.deleteInstance();
        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(intent);
    }

    private void validateUser() {
        UserProfile user = UserProfile.getInstance(null);
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
        }
    }

    private void updateRole() {
        UserProfile user = UserProfile.getInstance(null);
        if (user.getType() == UserType.NORMAL) {
            btnCreateSite.setVisibility(View.GONE);
        }
        if (user.getType() == UserType.SUPER_USER) {
            Intent intent = new Intent(MainActivity.this, SuperUserActivity.class);
            startActivity(intent);
        }
    }

    private void getAllSites() {
        SiteRepository.getAllSites(sites -> {
            updateSpinner(sites);
            map.refreshSites(sites);
        }, false);
    }

    private void filterSite() {
        SiteRepository.getFilterSites(leaderIdSpinner.getSelectedItem().toString(), numOfVolunteers.getText().toString(), sites -> {
            map.refreshSites(sites);
        });
    }

    private void goToNotification() {
        Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
        startActivity(intent);
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
        leaderIdSpinner.setSelection(0);
    }

    private void createSite() {
        Intent intent = new Intent(MainActivity.this, CreateSiteActivity.class);
        startActivity(intent);
    }
}