package com.opriday.islamicquiz.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opriday.islamicquiz.R;

import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity {

    EditText categoryEt;
    Button done;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        InitUI();
    }

    public void InitUI(){
        categoryEt = (EditText) findViewById(R.id.add_category);
        done = (Button) findViewById(R.id.add_category_btn);
        progressBar = (ProgressBar) findViewById(R.id.add_category_progress);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (validation()){
                    done.setEnabled(false);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("category").push();
                    Map<String,String> map = new HashMap<>();
                    map.put("name",categoryEt.getText().toString().toLowerCase());
                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(AddCategoryActivity.this,MainActivity.class));
                                    }
                                },1000);
                            }else {
                                done.setEnabled(true);
                                Toast.makeText(AddCategoryActivity.this,"Error, Category added unsuccessfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            done.setEnabled(true);
                            Toast.makeText(AddCategoryActivity.this,"Error, Category added unsuccessfully",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    public boolean validation(){
        if (TextUtils.isEmpty(categoryEt.getText().toString())){
            return false;
        }
        return true;
    }
}
