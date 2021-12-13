package com.example.firebase_authen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase_authen.R;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.repository.SiteRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class SuperUserActivity extends AppCompatActivity {


    private ListView listView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);
        bindUI();
        SiteRepository.getAllSites(sites -> {
            ArrayList<HashMap<String, String>> list = new ArrayList<>();
            for (int i = 0; i < sites.size(); i++) {

                HashMap<String, String> siteInfo = new HashMap<>();

                siteInfo.put("siteID", sites.get(i).getSiteID());
                siteInfo.put("leaderID", sites.get(i).getLeaderID());
                list.add(siteInfo);
            }
            updateSiteList(list);
        }, true);
    }

    private void bindUI() {
        logoutButton = findViewById(R.id.logout);
        listView = findViewById(R.id.list);
        logoutButton.setOnClickListener((View view) -> logOut());
    }

    private void logOut() {
        UserProfile.deleteInstance();
        finish();
    }

    private void updateSiteList(ArrayList<HashMap<String, String>> list) {
        String[] from = {"siteID", "leaderID"};

        int[] to = {R.id.lId, R.id.lContent};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.activity_view_site, from, to);

        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SuperUserActivity.this);
                builder.setTitle("What you want to do with this item?")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SuperUserActivity.this, SiteDetailActivity.class);
                                intent.putExtra("siteID", list.get(position).get("siteID"));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
            }
        });
    }

}