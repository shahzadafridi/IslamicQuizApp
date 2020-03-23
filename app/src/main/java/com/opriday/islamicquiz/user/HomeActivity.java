package com.opriday.islamicquiz.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.adapter.CategoryAdapter;
import com.opriday.islamicquiz.common.LoginActivity;
import com.opriday.islamicquiz.common.ViewCategoryActivity;
import com.opriday.islamicquiz.model.Category;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ImageView profile,logout;
    RecyclerView recyclerView;
    List<Category> categoryList = new ArrayList<>();
    CategoryAdapter adapter;
    ProgressBar progressBar;
    String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        InitUI();
        checkUserProfileExists();
    }

    private void checkUserProfileExists() {
        DatabaseReference profile_path = FirebaseDatabase.getInstance().getReference("profile");
        profile_path.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.e(TAG,"User profile exists");
                }else{
                    startActivity(new Intent(HomeActivity.this,UserProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"User Porfile, failed , "+databaseError.getMessage());
            }
        });
    }

    private void InitUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_home);
        recyclerView = (RecyclerView) findViewById(R.id.home_rv_user);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new CategoryAdapter(categoryList, HomeActivity.this,2);
        recyclerView.setAdapter(adapter);
        profile = (ImageView) findViewById(R.id.profile_img_user);
        logout = (ImageView) findViewById(R.id.logout_img_user);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ViewUserProfileActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                },2000);
            }
        });
        getCategoriesDataFromDB();
    }

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
                        adapter.setQuizList(categoryList);
                        progressBar.setVisibility(View.INVISIBLE);
                    }else {
                        Toast.makeText(HomeActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
