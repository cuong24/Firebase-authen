package com.example.firebase_authen.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.firebase_authen.R;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.repository.NotificationRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class NotificationService extends Service {
    private static final String TAG = "Notification Service";
    private static final String CHANNEL_ID = "1";
    private ArrayList<ListenerRegistration> registrationsListeners;


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        listenForUpdate(UserProfile.getInstance(null).getUid());
        return super.onStartCommand(intent, flags, startId);
    }

    public void listenForUpdate(String userID) {
        registrationsListeners = new ArrayList<>();
        Query notiQuery = NotificationRepository.getUnpushNotification(userID);
        registrationsListeners.add(notiQuery.addSnapshotListener(
                (QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                createNotification(dc.getDocument().getData());
                                NotificationRepository.setPushNotification(dc.getDocument().getId());
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                }
                )
        );
    }


    public void createNotification(Map<String, Object> message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder
                .setSmallIcon(R.mipmap.ic_launcher) //Must have an icon :D
                .setContentTitle((String) message.get("message"))
                .setAutoCancel(false);
        Intent notificationIntent = new Intent(this, NotificationService.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notificationPendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}