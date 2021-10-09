package com.example.flashcard.dbclass;

import android.provider.BaseColumns;

public final class UserInfoContract {
    private UserInfoContract() {
    }

    public static class Users implements BaseColumns{
        public static final String TABLE_NAME = "users_info";
        public static final String USER_PROPERTY = "user_property";
        public static final String USER_ID = "user_id";
        public static final String USER_PASSWORD = "password";
        public static final String USER_EMAIL = "email";
    }
}
