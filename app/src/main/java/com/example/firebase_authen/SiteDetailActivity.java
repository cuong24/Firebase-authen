package com.example.firebase_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.User;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SiteDetailActivity extends AppCompatActivity {

    private final static String TAG = SiteDetailActivity.class.toString();
    private MapsFragment map;
    private ArrayList<String> volunteers;
    private Button closeSite, download, joinSite;
    private Site currentSite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);
        bindUI();
        addClickListener();
        Intent intent = getIntent();
        getSite(intent.getExtras().get("siteID").toString());
    }

    private void bindUI(){
        closeSite = findViewById(R.id.closeSite);
        download = findViewById(R.id.download);
        joinSite = findViewById(R.id.joinSite);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_site_detail);
    }

    private void addClickListener(){
        closeSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        joinSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinSite();
            }
        });
    }

    private void updateRole(){
        UserProfile user = UserProfile.getInstance(null);
        if (user.getType() == UserType.SUPER_USER){
            joinSite.setVisibility(View.INVISIBLE);
        }
        if (user.getType() == UserType.NORMAL || !user.getUid().equals(currentSite.getLeaderID())){
            download.setVisibility(View.INVISIBLE);
            closeSite.setVisibility(View.INVISIBLE);
        }
        if (currentSite.getVolunteers().contains(user.getUid()) || user.getUid().equals(currentSite.getLeaderID())){
            joinSite.setVisibility(View.INVISIBLE);
        }
    }

    private void getSite(String siteID) {
        Site.getSite(siteID, new Site.getSiteCallBack() {
            @Override
            public void onCallBack(Site site) {
                currentSite = site;
                ArrayList<Site> sites = new ArrayList<>();
                sites.add(site);
                map.refreshSites(sites);
                map.focusSite(site);
                updateRole();
            }
        });
    }

    private void closeSite(){

    }

    private void download(){}

    private void joinSite(){
        UserProfile user = UserProfile.getInstance(null);
        currentSite.getVolunteers().add(user.getUid());
        currentSite.setNoVolunteers(currentSite.getNoVolunteers() + 1);
        Site.setSite(currentSite, new Site.getSiteCallBack() {
            @Override
            public void onCallBack(Site site) {
                Log.d(TAG, site.getSiteID());
                Intent intent = getIntent();
                getSite(intent.getExtras().get("siteID").toString());
                updateRole();
            }
        });
    }
}