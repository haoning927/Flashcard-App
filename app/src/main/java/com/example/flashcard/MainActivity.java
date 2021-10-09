package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.example.flashcard.dbclass.UserDbHelper;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.UserInfoContract;
import com.example.flashcard.dbclass.Word;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private ImageView logEye;
    private boolean isOpenEye = false;
    private Button login;
    private Button signup;
    private FlashCardDbHelper db;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();

        email = findViewById(R.id.userName);
        password = findViewById(R.id.passWord);
        logEye = findViewById(R.id.login_eye);
        signup = findViewById(R.id.signUp);
        login = findViewById(R.id.Login);
        logEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpenEye) {
                    logEye.setSelected(true);
                    isOpenEye = true;
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    logEye.setSelected(false);
                    isOpenEye = false;
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_text = email.getText().toString();
                String password_text = password.getText().toString();
                db = new FlashCardDbHelper(MainActivity.this);
                Boolean chkemailpassword = db.emailpassword(email_text, password_text);
                if (email_text.equals("") || password_text.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_LONG).show();
                }else{
                    if (chkemailpassword == true) {
                        Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();

                        int UserId = db.getID(email_text);

                        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("UserId", UserId);
                        editor.commit();
                        email.setText("");
                        password.setText("");


                    final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putInt("current_user", UserId);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username/email or password", Toast.LENGTH_LONG).show();
                }
            }
        }
        });


    }

    public void signup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
//        FlashCardDbHelper myDBHelper = new FlashCardDbHelper(MainActivity.this);
//        FlashCardDbHelper.reset(myDBHelper.getWritableDatabase());
//        Log.i(TAG, "Resetting database...");
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}