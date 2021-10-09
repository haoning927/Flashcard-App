package com.example.flashcard.dbclass;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class FlashCardDbHelper extends SQLiteOpenHelper implements LocationListener {
    private static final String DB_NAME = "flash_card.db";
    private static final String TAG = "flashCardDB";
    private static final int DB_VERSION = 3;
    private static final int DEFAULT_BUFFER_SIZE = 2;
    //    private static final int DEFAULT_BUFFER_SIZE = 8;
//    private static final int DEFAULT_BUFFER_SIZE = 256;
    private SharedPreferences dbsp;
    private LocationManager locationManager;
    private ArrayList<ContentValues> buffer;

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserInfoContract.Users.TABLE_NAME +
            "(" + UserInfoContract.Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserInfoContract.Users.USER_ID + " TEXT NOT NULL, " +
            UserInfoContract.Users.USER_EMAIL + " TEXT, " +
            UserInfoContract.Users.USER_PASSWORD + " TEXT NOT NULL " + ")";
    private static final String CREATE_SET_TABLE = "CREATE TABLE " + SetInfoContract.SetColumns.TABLE_NAME +
            "(" + SetInfoContract.SetColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SetInfoContract.SetColumns.USER_ID + " INTEGER NOT NULL, " +
            SetInfoContract.SetColumns.SET_NAME + " TEXT NOT NULL" + ")";
    private static final String CREATE_WORD_TABLE = "CREATE TABLE " + WordInfoContract.WordColumns.TABLE_NAME +
            "(" + WordInfoContract.WordColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WordInfoContract.WordColumns.SET_ID + " INTEGER NOT NULL, " + WordInfoContract.WordColumns.WORD_TITLE +
            " TEXT NOT NULL, " + WordInfoContract.WordColumns.WORD_DES + " TEXT NOT NULL, " +
            WordInfoContract.WordColumns.REMEMBER + " INTEGER NOT NULL, " + WordInfoContract.WordColumns.FORGOT +
            " INTEGER NOT NULL" + ")";
    private static final String CREATE_GPS_TABLE = "CREATE TABLE " + GPSInfoContract.GPSColumns.TABLE_NAME +
            "(" + GPSInfoContract.GPSColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GPSInfoContract.GPSColumns.LAT + " DOUBLE NOT NULL, " +
            GPSInfoContract.GPSColumns.LNG + " DOUBLE NOT NULL, " +
            GPSInfoContract.GPSColumns.USER_ID + " INTEGER NOT NULL, " +
            GPSInfoContract.GPSColumns.SET_ID + " INTEGER NOT NULL" + ")";

    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + UserInfoContract.Users.TABLE_NAME;
    private static final String DROP_SET_TABLE = "DROP TABLE IF EXISTS " + SetInfoContract.SetColumns.TABLE_NAME;
    private static final String DROP_WORD_TABLE = "DROP TABLE IF EXISTS " + WordInfoContract.WordColumns.TABLE_NAME;
    private static final String DROP_GPS_TABLE = "DROP TABLE IF EXISTS " + GPSInfoContract.GPSColumns.TABLE_NAME;

    public FlashCardDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (buffer==null) buffer=new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SET_TABLE);
        db.execSQL(CREATE_WORD_TABLE);
        db.execSQL(CREATE_GPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: may need better on upgrade actions
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_SET_TABLE);
        db.execSQL(DROP_WORD_TABLE);
        db.execSQL(DROP_GPS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_SET_TABLE);
        db.execSQL(DROP_WORD_TABLE);
        db.execSQL(DROP_GPS_TABLE);
        onCreate(db);
    }

    @Override // add all buffer content to database before quitting
    public void close() {
        addGPSToDB();
        super.close();
    }

    public boolean insert(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(UserInfoContract.Users.USER_PROPERTY, property);
        contentValues.put(UserInfoContract.Users.USER_ID, username);
        contentValues.put(UserInfoContract.Users.USER_EMAIL, email);
        contentValues.put(UserInfoContract.Users.USER_PASSWORD, password);
        long ins = db.insert(UserInfoContract.Users.TABLE_NAME, null, contentValues);
        db.close();
        if (ins == -1) return false;
        else return true;

    }

    public Boolean chkemail(String email) {
        //String[] columns = {UserInfoContract.Users.USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = UserInfoContract.Users.USER_EMAIL + " LIKE? ";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(UserInfoContract.Users.TABLE_NAME, null,
                selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        db.close();
        //Cursor cursor = db.rawQuery("select * from TABLE_NAME where email=?", new String[]{email});
        if (count > 0) return false;
        else return true;
    }

    public Boolean chkusername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = UserInfoContract.Users.USER_ID + " LIKE? ";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(UserInfoContract.Users.TABLE_NAME, null,
                selection, selectionArgs, null, null, null);
        //Cursor cursor = db.rawQuery("select * from TABLE_NAME where email=?", new String[]{email});
        int count = cursor.getCount();
        db.close();
        if (count > 0) return false;
        else return true;
    }public Boolean emailpassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(UserInfoContract.Users.TABLE_NAME, null,
                UserInfoContract.Users.USER_ID + "=?" + " and " +
                        UserInfoContract.Users.USER_PASSWORD + "=?",
                selectionArgs, null, null, null);
        Cursor cursor1 = db.query(UserInfoContract.Users.TABLE_NAME, null,
                UserInfoContract.Users.USER_EMAIL + "=?" + " and " +
                        UserInfoContract.Users.USER_PASSWORD + "=?",
                selectionArgs, null, null, null);
        int count = cursor.getCount();
        int count1 = cursor1.getCount();
        db.close();
        if (count > 0 || count1 > 0) return true;
        else return false;
    }

    public int getID(String str) {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {str, str};
        Cursor cursor = db.query(UserInfoContract.Users.TABLE_NAME, null,
                UserInfoContract.Users.USER_ID + "=?" + " or " +
                        UserInfoContract.Users.USER_EMAIL + "=?",
                selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            num = cursor.getInt(cursor.getColumnIndex(UserInfoContract.Users._ID));
        }
        cursor.close();
        db.close();
        return num;
    }

    public UserSet getUserInfo(int num) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tar = String.valueOf(num);
        String[] selectionArgs = {tar};
        ArrayList<UserSet> result = new ArrayList<>();
        Cursor cursor = db.query(UserInfoContract.Users.TABLE_NAME, null,
                UserInfoContract.Users._ID + "=?",
                selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            int userID = cursor.getInt(cursor.getColumnIndex(UserInfoContract.Users._ID));
            String userName = cursor.getString(cursor.getColumnIndex(UserInfoContract.Users.USER_ID));
            String userEmail = cursor.getString(cursor.getColumnIndex(UserInfoContract.Users.USER_EMAIL));
            String userPassword = cursor.getString(cursor.getColumnIndex(UserInfoContract.Users.USER_PASSWORD));

            UserSet userSet = new UserSet(userID, userName, userEmail, userPassword);

            cursor.close();
            db.close();
            return userSet;
        }
        else{
            cursor.close();
            db.close();
            return null;
        }

    }

    public int update(int userId, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put(UserInfoContract.Users.USER_ID, username);
        contentValues.put(UserInfoContract.Users.USER_EMAIL, email);
        contentValues.put(UserInfoContract.Users.USER_PASSWORD, password);

        String str = String.valueOf(userId);
        String whereClause = UserInfoContract.Users._ID+ "=?";
        String[] whereArgs = {str};

        int count =
                db.update(UserInfoContract.Users.TABLE_NAME,contentValues,whereClause,whereArgs);
        db.close();
        return count;
    }

    public static void reset(SQLiteDatabase db) {
        db.execSQL(DROP_SET_TABLE);
        db.execSQL(DROP_WORD_TABLE);
        db.execSQL(DROP_GPS_TABLE);
        db.execSQL(CREATE_SET_TABLE);
        db.execSQL(CREATE_WORD_TABLE);
        db.execSQL(CREATE_GPS_TABLE);
    }

    public ArrayList<CardSet> getSets(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = SetInfoContract.SetColumns.USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        ArrayList<CardSet> result = new ArrayList<>();

        Cursor cursor = db.query(SetInfoContract.SetColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null,
                SetInfoContract.SetColumns.SET_NAME);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SetInfoContract.SetColumns._ID));
            String setName = cursor.getString(cursor.getColumnIndex(SetInfoContract.SetColumns.SET_NAME));

            CardSet cardSet = new CardSet(id, userId, setName);
            result.add(cardSet);
        }
        cursor.close();
        db.close();
        return result;
    }

    public CardSet getSet(int setId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = SetInfoContract.SetColumns._ID + "=?";
        String[] selectionArgs = {String.valueOf(setId)};

        Cursor cursor = db.query(SetInfoContract.SetColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null,
                SetInfoContract.SetColumns.SET_NAME);

        if (cursor.moveToNext()) {
            int userId = cursor.getInt(cursor.getColumnIndex(SetInfoContract.SetColumns.USER_ID));
            String setName = cursor.getString(cursor.getColumnIndex(SetInfoContract.SetColumns.SET_NAME));

            CardSet cardSet = new CardSet(setId, userId, setName);
            cursor.close();
            db.close();
            return cardSet;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public long addSet(CardSet cardSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SetInfoContract.SetColumns.USER_ID, cardSet.getUserId());
        contentValues.put(SetInfoContract.SetColumns.SET_NAME, cardSet.getSetName());

        long recordId = db.insert(SetInfoContract.SetColumns.TABLE_NAME, null, contentValues);
        db.close();
        return recordId;
    }

    public boolean removeSet(int setId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = SetInfoContract.SetColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(setId)};

        int count = db.delete(SetInfoContract.SetColumns.TABLE_NAME, whereClause, whereArgs);
        db.close();

        if (count >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateSet(CardSet cardSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SetInfoContract.SetColumns.USER_ID, cardSet.getUserId());
        contentValues.put(SetInfoContract.SetColumns.SET_NAME, cardSet.getSetName());

        String whereClause = SetInfoContract.SetColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(cardSet.getSetId())};

        int count = db.update(SetInfoContract.SetColumns.TABLE_NAME, contentValues, whereClause, whereArgs);
        db.close();

        if (count >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Word> getWords(int setId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = WordInfoContract.WordColumns.SET_ID + "=?";
        String[] selectionArgs = {String.valueOf(setId)};
        ArrayList<Word> result = new ArrayList<>();

        Cursor cursor = db.query(WordInfoContract.WordColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null,
                WordInfoContract.WordColumns.WORD_TITLE);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns._ID));
            String wordTitle = cursor.getString(cursor.getColumnIndex(WordInfoContract.WordColumns.WORD_TITLE));
            String wordDes = cursor.getString(cursor.getColumnIndex(WordInfoContract.WordColumns.WORD_DES));
            int remember = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns.REMEMBER));
            int forgot = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns.FORGOT));

            Word word = new Word(id, setId, wordTitle, wordDes, remember, forgot);
            result.add(word);
        }
        cursor.close();
        db.close();
        return result;
    }

    public Word getWord(int wordId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = WordInfoContract.WordColumns._ID + "=?";
        String[] selectionArgs = {String.valueOf(wordId)};

        Cursor cursor = db.query(WordInfoContract.WordColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null,
                WordInfoContract.WordColumns.WORD_TITLE);

        if (cursor.moveToNext()) {
            int setId = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns.SET_ID));
            String wordTitle = cursor.getString(cursor.getColumnIndex(WordInfoContract.WordColumns.WORD_TITLE));
            String wordDes = cursor.getString(cursor.getColumnIndex(WordInfoContract.WordColumns.WORD_DES));
            int remember = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns.REMEMBER));
            int forgot = cursor.getInt(cursor.getColumnIndex(WordInfoContract.WordColumns.FORGOT));

            Word word = new Word(wordId, setId, wordTitle, wordDes, remember, forgot);
            cursor.close();
            db.close();
            return word;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public long addWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WordInfoContract.WordColumns.SET_ID, word.getSetId());
        contentValues.put(WordInfoContract.WordColumns.WORD_TITLE, word.getWordTitle());
        contentValues.put(WordInfoContract.WordColumns.WORD_DES, word.getWordDes());
        contentValues.put(WordInfoContract.WordColumns.REMEMBER, word.getRemember());
        contentValues.put(WordInfoContract.WordColumns.FORGOT, word.getForgot());

        long recordId = db.insert(WordInfoContract.WordColumns.TABLE_NAME, null, contentValues);
        db.close();
        return recordId;
    }

    public void addWords(ArrayList<Word> words) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put(WordInfoContract.WordColumns.SET_ID, word.getSetId());
            contentValues.put(WordInfoContract.WordColumns.WORD_TITLE, word.getWordTitle());
            contentValues.put(WordInfoContract.WordColumns.WORD_DES, word.getWordDes());
            contentValues.put(WordInfoContract.WordColumns.REMEMBER, word.getRemember());
            contentValues.put(WordInfoContract.WordColumns.FORGOT, word.getForgot());

            db.insert(WordInfoContract.WordColumns.TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    public boolean removeWord(int wordId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = WordInfoContract.WordColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(wordId)};

        int count = db.delete(WordInfoContract.WordColumns.TABLE_NAME, whereClause, whereArgs);
        db.close();

        if (count >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public int removeWordsBySet(int setId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = WordInfoContract.WordColumns.SET_ID + "=?";
        String[] whereArgs = {String.valueOf(setId)};

        int count = db.delete(WordInfoContract.WordColumns.TABLE_NAME, whereClause, whereArgs);
        db.close();

        return count;
    }

    public boolean updateWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WordInfoContract.WordColumns.SET_ID, word.getSetId());
        contentValues.put(WordInfoContract.WordColumns.WORD_TITLE, word.getWordTitle());
        contentValues.put(WordInfoContract.WordColumns.WORD_DES, word.getWordDes());
        contentValues.put(WordInfoContract.WordColumns.REMEMBER, word.getRemember());
        contentValues.put(WordInfoContract.WordColumns.FORGOT, word.getForgot());

        String whereClause = WordInfoContract.WordColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(word.getWordId())};

        int count = db.update(WordInfoContract.WordColumns.TABLE_NAME, contentValues, whereClause, whereArgs);
        db.close();

        if (count >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addGPS(Context context, int user_id, int set_id) {

//        Log.i(TAG, "2");

        if (dbsp==null) dbsp=context.getSharedPreferences("flashCardSP", Context.MODE_PRIVATE);
        if (!dbsp.getBoolean("record_GPS_status", false)) return false;

//        Log.i(TAG, "3");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "No Permission");
            return false;
        }

//        Log.i(TAG, "4");
        if (locationManager==null) locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//        Log.i(TAG, "5");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Location current_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ContentValues contentValues = new ContentValues();
        if (current_location != null) {
            contentValues.put(GPSInfoContract.GPSColumns.LAT, current_location.getLatitude());
            contentValues.put(GPSInfoContract.GPSColumns.LNG, current_location.getLongitude());
            contentValues.put(GPSInfoContract.GPSColumns.USER_ID, user_id);
            contentValues.put(GPSInfoContract.GPSColumns.SET_ID, set_id);
            Log.i(TAG, "RECORD GPS: (" + current_location.getLatitude() + ", " + current_location.getLongitude() + ")");
            buffer.add(contentValues);
        }
//        Log.i(TAG, "6");


//        Log.i(TAG, "7");
//        if (buffer.size()==DEFAULT_BUFFER_SIZE)
        addGPSToDB();

//        Log.i(TAG, "8");
        return true;
    }

    private boolean addGPSToDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < buffer.size(); i++)
            db.insert(GPSInfoContract.GPSColumns.TABLE_NAME, null, buffer.get(i));
        Log.i(TAG, "Added "+buffer.size()+" Locations...");
        buffer.clear();
        db.close();
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        Log.i(TAG, "LOCATION CHANGED -> Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    public ArrayList<Integer> getSetFrequency(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = GPSInfoContract.GPSColumns.USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(user_id)};

        Cursor cursor = db.query(GPSInfoContract.GPSColumns.TABLE_NAME, new String[]{GPSInfoContract.GPSColumns.SET_ID},
                selection, selectionArgs, GPSInfoContract.GPSColumns.SET_ID, null,
                "COUNT(*) DESC");

        ArrayList<Integer> result = new ArrayList<>();
        while (cursor.moveToNext())
            result.add(cursor.getInt(cursor.getColumnIndex(GPSInfoContract.GPSColumns.SET_ID)));

        db.close();
        return result;
    }

    public ArrayList<GPS> getGPSLocations(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = GPSInfoContract.GPSColumns.USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(user_id)};

        Cursor cursor = db.query(GPSInfoContract.GPSColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null,
                null);

        ArrayList<GPS> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(GPSInfoContract.GPSColumns.LAT));
            double longitude = cursor.getDouble(cursor.getColumnIndex(GPSInfoContract.GPSColumns.LNG));
            int set_id = cursor.getInt(cursor.getColumnIndex(GPSInfoContract.GPSColumns.SET_ID));

            GPS temp_GPS = new GPS(latitude, longitude, set_id);
            result.add(temp_GPS);
        }

        db.close();
        return result;
    }
}