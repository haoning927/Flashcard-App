package com.example.flashcard.dbclass;

import android.provider.BaseColumns;

public final class GPSInfoContract {

    private GPSInfoContract(){}

    public static class GPSColumns implements BaseColumns {

        public static final String TABLE_NAME = "gps_location";
        public static final String LAT = "latitude";
        public static final String LNG = "longitude";
        public static final String SET_ID = "set_id";
        public static final String USER_ID = "user_id";
    }
}
