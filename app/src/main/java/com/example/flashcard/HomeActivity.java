package com.example.flashcard;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.UserSet;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    private String TAG = "HomeActivity";
    private TextView User_name;
    private DrawerLayout myDL;
    private ActionBarDrawerToggle myTG;
    private HomeActivity temp;
    private String[] strs = {};
    private Button manage;
    private String userName;
    private ImageView image;
    private TextView textView;
    private TextView textView1;
    private ListView listView;
    private int userId;
    ArrayAdapter<CardSet> mAdapter;
    ArrayList<CardSet> cardSets;
    TextView profile;
    public static final String CHANNEL_ID = "FlashCard_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        home();
        createNotificationChannnel();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "FlashCard",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void home(){

        setContentView(R.layout.activity_sideview);
        listView = findViewById(R.id.wordSet);
        image = findViewById(R.id.home_image);
        textView = findViewById(R.id.home_textview);
        textView1 = findViewById(R.id.home_nothing);
        manage = findViewById(R.id.home_manage_set);

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);

        FlashCardDbHelper dbHelper = new FlashCardDbHelper(HomeActivity.this);
        UserSet userSet = dbHelper.getUserInfo(userId);
        userName = userSet.getUserName();


        temp = this;

        //Drawer
        myDL = (DrawerLayout) findViewById(R.id.drawLayout);
        myTG = new ActionBarDrawerToggle(this, myDL, R.string.open, R.string.close);

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

                } else if (id == R.id.GPS_map) {
                    i = new Intent(temp, MapsActivity.class);
                    startActivity(i);
                } else if (id == R.id.Manage_Set) {
                    i = new Intent(temp, ManageSetActivity.class);
                    startActivity(i);
                } else if (id == R.id.Settings) {
                    i = new Intent(temp, SettingActivity.class);
                    startActivity(i);
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

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(temp, ManageSetActivity.class);
                startActivity(i);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CardSet cardSet = (CardSet) parent.getItemAtPosition(position);
                int setId = cardSet.getSetId();

                Intent myIntent = new Intent(view.getContext(), FlashCardActivity.class);
                myIntent.putExtra("Set Id", setId);
                startActivity(myIntent);

            }
        });
    }

    public void updateData(){
        Log.d(TAG, "update data start");
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(HomeActivity.this);

        cardSets = dbHelper.getSets(userId);

        if(cardSets.isEmpty()){
            Log.d(TAG, "cardSets is Empty");
            listView.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            manage.setVisibility(View.VISIBLE);
        }
        else{
            Log.d(TAG, "cardSets is not Empty");
            image.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            textView1.setVisibility(View.INVISIBLE);
            manage.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

            mAdapter = new ArrayAdapter<CardSet>(HomeActivity.this, R.layout.set_item, R.id.text_view_set,
                    cardSets);
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "on Resume");
        updateData();
        Log.d(TAG, "resume end");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (myTG.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder isExit = new AlertDialog.Builder(this);
            isExit.setTitle("Confirmation");
            isExit.setMessage("You are going to sign out to login page. Are you sure?");
            isExit.setPositiveButton("Yes", diaListener);
            isExit.setNegativeButton("No", diaListener);
            isExit.show();
        }
        return false;
    }

    DialogInterface.OnClickListener diaListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int buttonId) {
            switch (buttonId) {
                case AlertDialog.BUTTON_POSITIVE:
                    Intent intent = new Intent(temp, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }
        }
    };
}