package com.example.firebase_authen.model;

import java.util.ArrayList;

public class Site {
    private Double longitude, latitude;
    private ArrayList<String> volunteers;
    private String leaderID, siteID;
    private Boolean closed;
    private int noOfPositiveCase, noOfTest, noVolunteers;

    public Site() {
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

    public int getNoOfPositiveCase() {
        return noOfPositiveCase;
    }

    public void setNoOfPositiveCase(int noOfPositiveCase) {
        this.noOfPositiveCase = noOfPositiveCase;
    }

    public int getNoOfTest() {
        return noOfTest;
    }

    public void setNoOfTest(int noOfTest) {
        this.noOfTest = noOfTest;
    }

}
