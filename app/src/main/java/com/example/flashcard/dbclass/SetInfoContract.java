package com.example.flashcard.dbclass;

import android.provider.BaseColumns;

public final class SetInfoContract {

    private SetInfoContract() {
    }

    public static class SetColumns implements BaseColumns {
        public static final String TABLE_NAME = "set_info";
        public static final String USER_ID = "user_id";
        public static final String SET_NAME = "set_name";
    }
}