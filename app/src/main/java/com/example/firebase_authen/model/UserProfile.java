package com.example.firebase_authen.model;

public final class UserProfile {
    private static UserProfile instance;
    private String name;
    private String password;
    private UserType type;
    private String uid;

    private UserProfile(User user) {
        this.name = user.getName();
        this.password = user.getPassword();
        this.type = user.getType();
        this.uid = user.getUid();
    }

    public static UserProfile getInstance(User user) {
        if (instance == null && user != null) {
            instance = new UserProfile(user);
        }
        return instance;
    }

    public static void deleteInstance(){
        instance = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}