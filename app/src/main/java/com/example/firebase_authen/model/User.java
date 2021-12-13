package com.example.firebase_authen.model;

public class User {
    private String name;
    private String password;
    private UserType type;
    private String uid;

    public User() {
        type = UserType.NORMAL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(Boolean isLeader, Boolean isSuperUser) {
        if (isLeader) {
            this.type = UserType.LEADER;
        }
        if (isSuperUser) {
            this.type = UserType.SUPER_USER;
        }
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return type;
    }

}
