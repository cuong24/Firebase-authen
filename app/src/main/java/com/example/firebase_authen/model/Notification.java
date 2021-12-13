package com.example.firebase_authen.model;

public class Notification {
    private String notiID;
    private String userID;
    private String message;
    private boolean isSeen;
    private boolean isPush;

    public Notification(String notiID, String userID, String message, boolean isSeen, boolean isPush) {
        this.notiID = notiID;
        this.userID = userID;
        this.message = message;
        this.isSeen = isSeen;
        this.isPush = isPush;
    }

    public Notification(String userID, String message) {
        this.userID = userID;
        this.message = message;
        this.isSeen = false;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getNotiID() {
        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }
}
