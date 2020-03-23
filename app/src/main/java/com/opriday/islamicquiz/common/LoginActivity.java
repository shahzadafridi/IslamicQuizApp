package com.opriday.islamicquiz.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.Util.Constants;
import com.opriday.islamicquiz.admin.MainActivity;
import com.opriday.islamicquiz.user.HomeActivity;
import com.opriday.islamicquiz.user.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    EditText email,password;
    TextView register;
    ProgressBar progressBar;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        InitUI();
    }

    public void InitUI(){
        register = (TextView) findViewById(R.id.login_register_label);
        email = (EditText) findViewById(R.id.login_email_et);
        email.requestFocus();
        password = (EditText) findViewById(R.id.login_pass_et);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        login = (Button) findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (validation()){
                    userLogin(email.getText().toString(),password.getText().toString());
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    public boolean validation(){

        boolean isValid = true;

        if (TextUtils.isEmpty(email.getText().toString())){
            email.setError("Enter email");
            isValid = false;
        }

        if (TextUtils.isEmpty(password.getText().toString())){
            password.setError("Enter password");
            isValid = false;
        }

        return isValid;
    }

    public void userLogin(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            progressBar.setVisibility(View.INVISIBLE);
                            createSession(email);
                            homeActivity(email);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed, Check email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "signInWithEmail:failure");
                Toast.makeText(LoginActivity.this, "Authentication failed, Check email and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createSession(String email){
        SharedPreferences.Editor editor = Constants.getSharedPrefEditor(this);
        editor.putString("email",email);
        editor.putBoolean("isLogin",true);
        editor.apply();
    }

    public void homeActivity(String email){
        if (email.contains("admin")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
           String email =  Constants.getSharedPref(LoginActivity.this).getString("email",null);
           if (email != null){
               if (email.contains("admin")){
                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                   startActivity(intent);
               }else {
                   Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                   startActivity(intent);
               }
           }
        }
    }
}
