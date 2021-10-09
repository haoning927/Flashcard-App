package com.example.flashcard.dbclass;

public class GPS {

    public static final String TABLE_NAME = "gps_location";
    public static final String SET_ID = "set_id";
    private double latitude;
    private double longitude;
    private int set_id;

    public GPS(double latitude, double longitude, int set_id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.set_id = set_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getSet_id() {
        return set_id;
    }
}
