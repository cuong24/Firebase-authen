package com.example.firebase_authen.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase_authen.R;
import com.example.firebase_authen.model.Notification;
import com.example.firebase_authen.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    private ListView pastNotificationList, newNotificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        bindUI();
        updateNotificationList(true);
        updateNotificationList(false);

    }

    private void bindUI() {
        pastNotificationList = findViewById(R.id.past_notification_list);
        newNotificationList = findViewById(R.id.new_notification_list);
    }

    private void updateNotificationList(Boolean isRead) {
        NotificationRepository.getNotification(isRead, notifications -> {
            ArrayList<HashMap<String, String>> list = new ArrayList<>();
            for (Notification notification : notifications) {
                HashMap<String, String> notiInfo = new HashMap<>();
                notiInfo.put("message", notification.getMessage());
                list.add(notiInfo);
            }
            renderNotifications(isRead, list);
            if (!isRead) {
                NotificationRepository.setReadNotification(notifications, isSuccess -> {
                });
            }
        });
    }

    private void renderNotifications(Boolean isRead, ArrayList<HashMap<String, String>> messages) {
        String[] from = {"message"};

        int[] to = {R.id.lId};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), messages, R.layout.activity_view_notification, from, to);
        if (isRead) {
            pastNotificationList.setAdapter(simpleAdapter);
        } else {
            newNotificationList.setAdapter(simpleAdapter);
        }
    }
}