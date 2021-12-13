package com.example.firebase_authen.repository;

import com.example.firebase_authen.model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NotificationRepository {
    private static FirebaseFirestore db;

    public static void addNotification(Notification notification, AddNotificationCallBack addNotificationCallBack) {
        db = FirebaseFirestore.getInstance();
        db.collection("notifications").add(notification).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        addNotificationCallBack.onCallBack(true, null);
                    } else {
                        addNotificationCallBack.onCallBack(false, notification.getUserID());
                    }
                }
        );
    }

    public static void getNotification(Boolean isRead, GetNotificationCallBack getNotificationCallBack) {
        db = FirebaseFirestore.getInstance();
        ArrayList<Notification> notifications = new ArrayList<>();
        Query dbSites = db.collection("notifications").whereEqualTo("isSeen", isRead);
        dbSites.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            notifications.add(
                                    new Notification(document.getId(),
                                            (String) document.get("userID"),
                                            (String) document.get("message"),
                                            (boolean) document.get("isSeen"),
                                            (boolean) document.get("isPush")));
                        }
                        getNotificationCallBack.onCallBack(notifications);
                    }
                }
        );
    }

    public static Query getUnpushNotification(String userID) {
        db = FirebaseFirestore.getInstance();
        return db.collection("notifications")
                .whereEqualTo("isPush", false)
                .whereEqualTo("userID", userID);
    }

    public static void setPushNotification(String notiID) {
        db = FirebaseFirestore.getInstance();
        db.collection("notifications").document(notiID).update(
                "isPush", true
        );
    }

    public static void setReadNotification(ArrayList<Notification> notifications, SetReadNotificationCallBack setReadNotificationCallBack) {
        db = FirebaseFirestore.getInstance();
        for (Notification notification : notifications) {
            db.collection("notifications").document(notification.getNotiID()).update(
                    "isSeen", true
            ).addOnCompleteListener(
                    task -> {
                        setReadNotificationCallBack.onCallBack(true);
                    }
            );
        }
    }

    public interface AddNotificationCallBack {
        void onCallBack(Boolean isSuccess, String userID);
    }

    public interface SetReadNotificationCallBack {
        void onCallBack(Boolean isSuccess);
    }

    public interface GetNotificationCallBack {
        void onCallBack(ArrayList<Notification> notifications);
    }
}
