package com.example.flashcard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.flashcard.dbclass.FlashCardDbHelper;
//import com.example.flashcard.dbclass.UserDbHelper;


public class SignupActivity extends AppCompatActivity {
    private EditText signupUsername, signupEmail, signupPassword, cPassword;
    private ImageView signupEye, signupEye1;
    private boolean isOpenEye = false;
    private Button register;
    private FlashCardDbHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        signupEye = findViewById(R.id.register_eye);
        signupEye1 = findViewById(R.id.register_eye1);
        signupUsername = findViewById(R.id.registerName);
        signupEmail = findViewById(R.id.registerEmail);
        signupPassword = findViewById(R.id.registerPassword);
        cPassword = findViewById(R.id.confirmPassword);
        signupEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpenEye) {
                    signupEye.setSelected(true);
                    isOpenEye = true;
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    signupEye.setSelected(false);
                    isOpenEye = false;
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        signupEye1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpenEye) {
                    signupEye1.setSelected(true);
                    isOpenEye = true;
                    cPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    signupEye1.setSelected(false);
                    isOpenEye = false;
                    cPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        register = findViewById(R.id.registerSignup);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email_text = signupEmail.getText().toString();
                String Password_text = signupPassword.getText().toString();
                String cPassword_text = cPassword.getText().toString();
                String Username_text = signupUsername.getText().toString();
                if (Email_text.equals("") || Password_text.equals("") || cPassword_text.equals("") || Username_text.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_LONG).show();
                } else {
                    if(isEmail(Email_text) == true){
                        if (Password_text.equals(cPassword_text)) {
                            db = new FlashCardDbHelper(SignupActivity.this);
                            Boolean chkemail = db.chkemail(Email_text);
                            Boolean chkusername = db.chkusername(Username_text);
                            if (chkusername == true ) {
                                if(chkemail == true) {
                                    Boolean insert = db.insert(Username_text, Email_text, Password_text);
                                    if (insert == true) {
                                        Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        signupEmail.setText("");
                                        signupPassword.setText("");
                                        cPassword.setText("");
                                        signupUsername.setText("");
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Email already exist", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Username already exist", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_LONG).show();
                        }

                    } else{
                        Toast.makeText(getApplicationContext(), "Incorrect email format", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

}

