package com.example.firebase_authen.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.firebase_authen.R;
import com.example.firebase_authen.fragment.MapsFragment;
import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.example.firebase_authen.repository.SiteRepository;

import java.util.ArrayList;

public class SiteDetailActivity extends AppCompatActivity {

    private final static String TAG = SiteDetailActivity.class.toString();
    private MapsFragment map;
    private ArrayList<String> volunteers;
    private Button closeSite, download, joinSite;
    private Site currentSite;

    ActivityResultLauncher<Intent> closeSiteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        currentSite.setNoOfPositiveCase((int) data.getExtras().get("noOfPositiveCase"));
                        currentSite.setNoOfTest((int) data.getExtras().get("noOfTest"));
                        currentSite.setClosed(true);
                        SiteRepository.setSite(currentSite, new SiteRepository.getSiteCallBack() {
                            @Override
                            public void onCallBack(Site site) {
                                finish();
                            }
                        });
                    }
                }
            });

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
                closeSite();
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
        SiteRepository.getSite(siteID, new SiteRepository.getSiteCallBack() {
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
        Intent intent = new Intent(SiteDetailActivity.this,CloseSiteActivity.class);
        intent.putExtra("siteID", currentSite.getSiteID());
        closeSiteActivityResultLauncher.launch(intent);
    }

    private void download(){}

    private void joinSite(){
        UserProfile user = UserProfile.getInstance(null);
        currentSite.getVolunteers().add(user.getUid());
        currentSite.setNoVolunteers(currentSite.getNoVolunteers() + 1);
        SiteRepository.setSite(currentSite, new SiteRepository.getSiteCallBack() {
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