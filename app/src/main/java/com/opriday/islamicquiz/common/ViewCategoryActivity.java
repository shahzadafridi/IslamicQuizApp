package com.opriday.islamicquiz.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.adapter.CategoryAdapter;
import com.opriday.islamicquiz.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ViewCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter adapter;
    List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        InitUI();
    }

    private void InitUI() {
        recyclerView = (RecyclerView) findViewById(R.id.viewCategory_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categoryList,ViewCategoryActivity.this,1);
        recyclerView.setAdapter(adapter);
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
                    }else {
                        Toast.makeText(ViewCategoryActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ViewCategoryActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewCategoryActivity.this,"No Category found.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
