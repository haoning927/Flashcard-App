package com.example.flashcard.dbclass;

import android.provider.BaseColumns;

public final class WordInfoContract {

    private WordInfoContract() {
    }

    public static class WordColumns implements BaseColumns {

        public static final String TABLE_NAME = "word_info";
        public static final String SET_ID = "set_id";
        public static final String WORD_TITLE = "word_title";
        public static final String WORD_DES = "word_des";
        public static final String REMEMBER = "remember";
        public static final String FORGOT = "forgot";

    }
}