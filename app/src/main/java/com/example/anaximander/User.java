package com.example.anaximander;

import java.util.Date;

public class User {
    private double latitude,longitude,rssi;
    private String sys_info;
    private Date date;

    public User(){}

    public User(double latitude, double longitude, double rssi, String sys_info, Date date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rssi = rssi;
        this.sys_info=sys_info;

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSys_info(String sys_info) {
        this.sys_info = sys_info;
    }

    public String getSys_info() {
        return sys_info;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }
}
