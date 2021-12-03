package com.example.firebase_authen.repository;

import androidx.annotation.NonNull;

import com.example.firebase_authen.model.Site;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SiteRepository {
    private static FirebaseFirestore db;
    public static void getSite(String siteID, getSiteCallBack siteCallBack) {
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
                            newSite.setLatitude((Double) document.get("latitude"));
                            newSite.setVolunteers((ArrayList<String>) document.get("volunteers"));
                            newSite.setLeaderID((String) document.get("leaderID"));
                            newSite.setSiteID(siteID);
                            newSite.setClosed((boolean) document.get("closed"));
                            siteCallBack.onCallBack(newSite);
                        }
                    }
                }
            }
        });
    }

    public interface getSiteCallBack{
        void onCallBack(Site site);
    }

    public static void setSite(Site site, getSiteCallBack siteCallBack) {
        db = FirebaseFirestore.getInstance();
        db.collection("sites").document(site.getSiteID()).set(site).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                siteCallBack.onCallBack(site);
            }
        });
    }

    public static void getAllSites(getAllSitesCallBack sitesCallBack){
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
                                (ArrayList<String>) document.get("volunteers"),
                                (String) document.get("leaderID"));
                        newSite.setSiteID(document.getId());
                        sites.add(newSite);
                    }
                    sitesCallBack.onCallBack(sites);
                }
            }
        });
    }

    public interface getAllSitesCallBack{
        void onCallBack(ArrayList<Site> site);
    }
}
