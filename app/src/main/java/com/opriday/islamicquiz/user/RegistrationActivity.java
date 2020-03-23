package com.opriday.islamicquiz.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.opriday.islamicquiz.R;

public class RegistrationActivity extends AppCompatActivity {

    private String TAG = "RegistrationActivity";
    private FirebaseAuth mAuth;
    EditText email,password;
    ProgressBar progressBar;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        InitUI();
    }

    public void InitUI(){
        email = (EditText) findViewById(R.id.reg_email_et);
        email.requestFocus();
        password = (EditText) findViewById(R.id.reg_pass_et);
        progressBar = (ProgressBar) findViewById(R.id.reg_progress);
        register = (Button) findViewById(R.id.reg_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (validation()){
                    userRegistration(email.getText().toString(),password.getText().toString());
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
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

    public void userRegistration(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signUpWithEmail:success");
                            progressBar.setVisibility(View.INVISIBLE);
                            homeActivity(email);
                        } else {
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed, Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed, Invalid email entered", Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed, Email already registered.", Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    public void homeActivity(String email){
        startActivity(new Intent(RegistrationActivity.this,UserProfileActivity.class).putExtra("email",email));
    }

}
