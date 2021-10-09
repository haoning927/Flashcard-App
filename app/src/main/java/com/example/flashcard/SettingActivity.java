package com.example.flashcard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.UserSet;
import com.example.flashcard.dbclass.GPS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    TextView profile;
    TextView User_name;
    private String userName;
    private int userId;
    private RadioButton btnTyping;
    private RadioButton btnChoice;
    private RadioButton btnCard;
    private DrawerLayout myDL;
    private ActionBarDrawerToggle myTG;
    private SettingActivity temp;
    private Switch switch_GPS;
    private Switch switch_notification;
    private RelativeLayout wholeView;
    private static final int DEFAULT_NOTIFICATION_ID = 666;
    private static final String TAG = "SettingActivity";
    private static FlashCardDbHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId",0);

        FlashCardDbHelper dbHelper = new FlashCardDbHelper(SettingActivity.this);
        UserSet userSet = dbHelper.getUserInfo(userId);
        userName = userSet.getUserName();
        profile = findViewById(R.id.Profile);
        temp = this;

        // get saved user states
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        int user_id = sp.getInt("current_user", -1);
        final String default_method_key = "default_method"+Integer.toString(user_id);
        if (!sp.getBoolean("is_initialized", false)) {
            editor.putBoolean("is_initialized", true);
            editor.putBoolean("record_GPS_status", false);
            editor.putBoolean("notification_status", false);
            editor.putInt(default_method_key, 0);
            editor.commit(); // we should change .commit() to .apply() when I/O requirement is high
        }

        // Radio button group
        btnTyping = (RadioButton) findViewById(R.id.btnTyping);
        btnChoice = (RadioButton) findViewById(R.id.btnChoice);
        btnCard = (RadioButton) findViewById(R.id.btnCard);
        int curr_method = sp.getInt(default_method_key, 0);
        if (curr_method==1) btnChoice.setChecked(true);
        if (curr_method==2) btnCard.setChecked(true);
        btnTyping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTyping.setChecked(true);
                editor.putInt(default_method_key, 0);
                editor.commit();
            }
        });
        btnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChoice.setChecked(true);
                editor.putInt(default_method_key, 1);
                editor.commit();
            }
        });
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCard.setChecked(true);
                editor.putInt(default_method_key, 2);
                editor.commit();
            }
        });

        //Drawer
        myDL = (DrawerLayout) findViewById(R.id.drawLayout);
        myTG = new ActionBarDrawerToggle(this, myDL, R.string.open,R.string.close);

        myDL.addDrawerListener(myTG);
        myTG.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.side_bar_settings);
        View header = navigationView.getHeaderView(0);
        User_name = (TextView) header.findViewById(R.id.side_text);
        User_name.setText(userName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                Intent i;
                myDL.closeDrawer(Gravity.LEFT);
                if (id == R.id.home) {
                    finish();

                } else if (id == R.id.GPS_map) {
                    i = new Intent(temp, MapsActivity.class);
                    startActivity(i);
                    finish();
                    //i = new Intent(temp, .class);
                    //startActivity(i);
                } else if (id == R.id.Manage_Set) {
                    i = new Intent(temp, ManageSetActivity.class);
                    startActivity(i);
                    finish();
                    //i = new Intent(temp, .class);
                    //startActivity(i);
                } else if (id == R.id.Settings) {

                } else if (id == R.id.Sign_out) {
                    Intent intent = new Intent(temp, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });

        // add switch functions
        wholeView = (RelativeLayout) findViewById(R.id.relativeLayout);
        switch_GPS = (Switch) findViewById(R.id.switchLocation);
        switch_notification = (Switch) findViewById(R.id.switchNotification);

        switch_GPS.setChecked(sp.getBoolean("record_GPS_status", false));
        switch_GPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(SettingActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    return;
                }
                Log.d(TAG, String.valueOf(isChecked));
                SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("record_GPS_status", isChecked);
                editor.commit();
            }
        });

        switch_notification.setChecked(sp.getBoolean("notification_status", false));
        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("notification_status", isChecked);
                editor.commit();
                if (isChecked) showNotificationTime();
                else removeNotificationTime();
            }
        });

        if (sp.getBoolean("notification_status", false)) showNotificationTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                // If request is cancelled, the result arrays are empty.
                SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    editor.putBoolean("record_GPS_status", true);
                    editor.commit();
                    switch_GPS.setChecked(true);
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    editor.putBoolean("record_GPS_status", false);
                    editor.commit();
                    switch_GPS.setChecked(false);
                }
                return;


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showNotificationTime() {
        TextView tv = new TextView(this);
        tv.setId(DEFAULT_NOTIFICATION_ID);
        tv.setText("               Schedules");
        tv.setTextColor(Color.GRAY);
        tv.setTextSize(25);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.Notification);
        params.topMargin = 100;

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAlarmSchedules(v);
            }
        });

        wholeView.addView(tv, params);

        TextView edit_profile = (TextView) findViewById(R.id.Profile);
        RelativeLayout.LayoutParams edit_profile_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        edit_profile_params.addRule(RelativeLayout.BELOW, DEFAULT_NOTIFICATION_ID);
        edit_profile_params.addRule(RelativeLayout.ALIGN_LEFT, R.id.Notification);
        edit_profile_params.topMargin = 100;
        edit_profile.setLayoutParams(edit_profile_params);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.i(TAG, token);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void removeNotificationTime() {
        wholeView.removeView((TextView) findViewById(DEFAULT_NOTIFICATION_ID));

        TextView edit_profile = (TextView) findViewById(R.id.Profile);
        RelativeLayout.LayoutParams edit_profile_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        edit_profile_params.addRule(RelativeLayout.BELOW, R.id.Notification);
        edit_profile_params.addRule(RelativeLayout.ALIGN_LEFT, R.id.Notification);
        edit_profile_params.topMargin = 100;
        edit_profile.setLayoutParams(edit_profile_params);
    }

    public void goToAlarmSchedules(View view) {
        Intent intent = new Intent(this, AlarmSchedules.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (myTG.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void edit(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

//    public void temp_func(View view) {
//        if (myDBHelper==null) myDBHelper=new FlashCardDbHelper(view.getContext());
//        FlashCardDbHelper.reset(myDBHelper.getWritableDatabase());
//        Log.i(TAG, "Done resetting database");
//    }

    public void temp_func(View view) {
        if (myDBHelper==null) myDBHelper=new FlashCardDbHelper(view.getContext());
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        int user_id = sp.getInt("current_user", -1);
        myDBHelper.addGPS(view.getContext(), user_id, 3);
    }

}
