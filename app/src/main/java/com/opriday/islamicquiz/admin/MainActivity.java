package com.opriday.islamicquiz.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.Util.Constants;
import com.opriday.islamicquiz.common.LoginActivity;
import com.opriday.islamicquiz.common.ViewCategoryActivity;
import com.opriday.islamicquiz.common.ViewQuizActivity;
import com.opriday.islamicquiz.model.Category;
import com.opriday.islamicquiz.user.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    LinearLayout addQuiz,addCategory,viewQuiz,viewCategory;
    List<Category> categoryList = new ArrayList<>();
    ImageView logout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUI();
        getCategoriesDataFromDB();
    }

    private void InitUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_mainAct);
        logout = (ImageView) findViewById(R.id.logout_img);
        addCategory = (LinearLayout) findViewById(R.id.linearLayout_1);
        addQuiz = (LinearLayout) findViewById(R.id.linearLayout_2);
        viewQuiz = (LinearLayout) findViewById(R.id.linearLayout_3);
        addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryList.size() > 0) {
                    startActivity(new Intent(MainActivity.this, AddQuizActivity.class));
                }else {
                    Toast.makeText(MainActivity.this,"Add Category first",Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Category not found");
                }
            }
        });
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddCategoryActivity.class));
            }
        });
        viewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewCategoryActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                logout.setEnabled(false);
                FirebaseAuth.getInstance().signOut();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                },2000);
            }
        });
    }

    //Save categories in cache.
    private void getCategoriesDataFromDB() {
        FirebaseDatabase.getInstance().getReference().child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue() != null){
                        for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            Category category = snap.getValue(Category.class);
                            categoryList.add(category);
                        }
                        Gson gson = new Gson();
                        String json = gson.toJson(categoryList);
                        updateSession(json);
                    }else {
                        Log.e(TAG,"No Category found.");
                    }
                }else {
                    Log.e(TAG,"No Category found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"No Category found.");
            }
        });
    }

    public void updateSession(String json){
        SharedPreferences.Editor editor = Constants.getSharedPrefEditor(this);
        editor.putString("category",json);
        editor.apply();
        Log.e(TAG,"Category updated in session.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.moveTaskToBack(true);
    }
}
