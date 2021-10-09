package com.example.flashcard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.UserSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
//import com.example.flashcard.dbclass.UserDbHelper;
import com.example.flashcard.dbclass.UserInfoContract;
import com.example.flashcard.dbclass.UserSet;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileEye;
    private boolean isOpenEye = false;
    private EditText pUsername;
    private EditText pEmail;
    private EditText pPasssword;
    private Button pSave;
    private DrawerLayout myDL;
    private ActionBarDrawerToggle myTG;
    private ProfileActivity temp;
    private FlashCardDbHelper dbHelper;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String username;
    private String email;
    private String password;
    private String str;
    private int userID;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dbHelper = new FlashCardDbHelper(this);

        profileEye = findViewById(R.id.profile_eye);
        pUsername = findViewById(R.id.profileName);
        pEmail = findViewById(R.id.profileEmail);
        pPasssword = findViewById(R.id.profilePassword);
        pSave = findViewById(R.id.profileSave);
        temp = this;

        profileEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpenEye) {
                    profileEye.setSelected(true);
                    isOpenEye = true;
                    pPasssword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    profileEye.setSelected(false);
                    isOpenEye = false;
                    pPasssword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);

        int UserId = sharedPreferences.getInt("UserId",0);


        UserSet userSet = dbHelper.getUserInfo(UserId);
        userID = userSet.getUserID();
        userName = userSet.getUserName();
        userEmail = userSet.getUserEmail();
        userPassword = userSet.getUserPassword();


        String temp = String.valueOf(userID);
        pUsername.setText(userName);
        pEmail.setText(userEmail);
        pPasssword.setText(userPassword);

        pSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = pUsername.getText().toString();
                email = pEmail.getText().toString();
                password = pPasssword.getText().toString();

                int count = dbHelper.update(userID,username,email,password);
                if(count < 1){
                    Toast.makeText(getApplicationContext(),"No record updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Profile updated",Toast.LENGTH_SHORT).show();
                }
            }

        });

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}