package com.example.firebase_authen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.firebase_authen.R;
import com.example.firebase_authen.fragment.MapsFragment;
import com.example.firebase_authen.model.Notification;
import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.example.firebase_authen.repository.NotificationRepository;
import com.example.firebase_authen.repository.SiteRepository;
import com.example.firebase_authen.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SiteDetailActivity extends AppCompatActivity {

    private final static String TAG = SiteDetailActivity.class.toString();
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
                        SiteRepository.setSite(currentSite, site -> {
                            createNoti();
                            finish();
                        });
                    }
                }
            });
    private MapsFragment map;
    private EditText peopleTested, positiveCases;
    private Button closeSite, download, joinSite;
    private Site currentSite;
    private ArrayList<HashMap<String, String>> volunteers;
    private ListView listView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);
        bindUI();
        addClickListener();
        Intent intent = getIntent();
        getSite(intent.getExtras().get("siteID").toString());
    }

    private void bindUI() {
        peopleTested = findViewById(R.id.people_tested);
        positiveCases = findViewById(R.id.positive_cases);
        closeSite = findViewById(R.id.closeSite);
        download = findViewById(R.id.download);
        joinSite = findViewById(R.id.joinSite);
        searchView = findViewById(R.id.idSearchView);
        map = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_site_detail);
        listView = findViewById(R.id.list);
    }

    private void addClickListener() {
        searchView.setOnQueryTextListener(MapsFragment.searchLocation(this, searchView, map));
        closeSite.setOnClickListener(view -> closeSite());
        download.setOnClickListener(view -> download());
        joinSite.setOnClickListener(view -> joinSite());
    }

    private void updateRole() {
        UserProfile user = UserProfile.getInstance(null);
        if (currentSite.getClosed() || user.getType() == UserType.SUPER_USER || currentSite.getVolunteers().contains(user.getUid()) || user.getUid().equals(currentSite.getLeaderID())) {
            joinSite.setVisibility(View.GONE);
        }
        if ((user.getType() == UserType.NORMAL || !user.getUid().equals(currentSite.getLeaderID())) && user.getType() != UserType.SUPER_USER) {
            download.setVisibility(View.GONE);
            closeSite.setVisibility(View.GONE);
        }
        if (currentSite.getClosed()) {
            closeSite.setVisibility(View.GONE);
        }
        peopleTested.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentSite.setNoOfTest(Integer.parseInt(editable.toString()));
                SiteRepository.setSite(currentSite, task -> {
                });
            }
        });
        positiveCases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentSite.setNoOfPositiveCase(Integer.parseInt(editable.toString()));
                SiteRepository.setSite(currentSite, task -> {
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getSite(String siteID) {
        SiteRepository.getSite(siteID, site -> {
            currentSite = site;
            ArrayList<Site> sites = new ArrayList<>();
            sites.add(site);
            map.refreshSites(sites);
            map.focusSite(site);
            updateRole();
            getUserInfo(site.getVolunteers());
            if (site.getClosed()) {
                positiveCases.setText(site.getNoOfPositiveCase() + "");
                peopleTested.setText(site.getNoOfTest() + "");
            }
        });

    }

    private void closeSite() {
        Intent intent = new Intent(SiteDetailActivity.this, CloseSiteActivity.class);
        intent.putExtra("siteID", currentSite.getSiteID());
        closeSiteActivityResultLauncher.launch(intent);
    }

    private void download() {
        StringBuilder fileContents = new StringBuilder();
        File filesDir = this.getFilesDir();
        File file = new File(filesDir, "volunteers");
        for (HashMap<String, String> line : volunteers) {
            fileContents.append("User ID:" + line.get("userID"))
                    .append(", User Name:" + line.get("userName")).append('\n');
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinSite() {
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

    private void createNoti() {
        String message = "The site " + currentSite.getSiteID() + " has been closed";
        for (String userID : currentSite.getVolunteers()) {
            Notification notification = new Notification(userID, message);
            NotificationRepository.addNotification(notification, (isSuccess, userID1) -> {
                if (!isSuccess) {
                    String mess = "Fail to notify user " + userID1;
                    Toast.makeText(SiteDetailActivity.this, mess, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getUserInfo(ArrayList<String> userIdLists) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        if (userIdLists.size() != 0) {
            for (int i = 0; i < userIdLists.size() - 1; i++) {

                HashMap<String, String> volunteer = new HashMap<>();

                volunteer.put("userID", userIdLists.get(i));
                UserRepository.getUserName(userIdLists.get(i), (userName, isSuccess) -> {
                    volunteer.put("userName", userName);
                    list.add(volunteer);
                });
            }
            HashMap<String, String> volunteer = new HashMap<>();
            volunteer.put("userID", userIdLists.get(userIdLists.size() - 1));
            UserRepository.getUserName(userIdLists.get(userIdLists.size() - 1), (userName, isSuccess) -> {
                volunteer.put("userName", userName);
                list.add(volunteer);
                updateUserList(list);
                volunteers = list;
            });
        }
    }

    private void updateUserList(ArrayList<HashMap<String, String>> list) {
        String[] from = {"userID", "userName"};

        int[] to = {R.id.lId, R.id.lContent};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.activity_view_record, from, to);

        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SiteDetailActivity.this);
            builder.setTitle("What you want to do with this item?")
                    .setNeutralButton("Read", (dialog, which) -> {
                    })
                    .setPositiveButton("Delete", (dialog, which) -> {
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .create().show();
        });
    }
}
