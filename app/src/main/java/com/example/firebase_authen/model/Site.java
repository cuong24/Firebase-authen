package com.example.firebase_authen.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.firebase_authen.SiteDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class Site {
    Double longitude;
    Double latitude;
    ArrayList<String> volunteers;
    String leaderID;
    Boolean closed;
    int noVolunteers;
    String siteID;
    FirebaseFirestore db;

    public Site(){

    }

    public Site(Double longitude, Double latitude, ArrayList<String> volunteers, String leaderID) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.volunteers = volunteers;
        this.leaderID = leaderID;
        this.closed = false;
        this.noVolunteers = 0;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(ArrayList<String> volunteers) {
        this.volunteers = volunteers;
    }

    public String getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(String leaderID) {
        this.leaderID = leaderID;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public int getNoVolunteers() {
        return noVolunteers;
    }

    public void setNoVolunteers(int noVolunteers) {
        this.noVolunteers = noVolunteers;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public Site getSite(String siteID){
        db = FirebaseFirestore.getInstance();
        Site newSite = new Site();
        Query dbSites = db.collection("sites").whereEqualTo("closed", false);
        dbSites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(siteID)) {
                            newSite.setLongitude((Double) document.get("longitude"));
                            newSite.setLongitude((Double) document.get("latitude"));
                            newSite.setVolunteers((ArrayList<String>) document.get("volunteers"));
                            newSite.setLeaderID((String) document.get("leaderID"));
                        }
                    }
                }
            }
        });
        return newSite;
    }
}
