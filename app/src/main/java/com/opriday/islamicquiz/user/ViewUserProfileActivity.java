package com.opriday.islamicquiz.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.model.User;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, email, age, score, total;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        InitUI();
    }

    private void InitUI() {
        profile_image = (CircleImageView) findViewById(R.id.viewProfile_image);
        username = (TextView) findViewById(R.id.viewProfile_username5);
        email = (TextView) findViewById(R.id.viewProfile_email);
        age = (TextView) findViewById(R.id.viewProfile_age3);
        score = (TextView) findViewById(R.id.viewProfile_attempt2);
        total = (TextView) findViewById(R.id.viewProfile_attempt);
        back = (ImageView) findViewById(R.id.viewProfile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadProfile();
    }

    private void loadProfile() {
        DatabaseReference profile_path = FirebaseDatabase.getInstance().getReference("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profile_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = (User) dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    email.setText(user.getEmail());
                    age.setText(user.getAge());
                    score.setText(user.getScore() + "/" + user.getTotal());
                    total.setText(user.getTotal_attempt());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewQuizActivity", "Failed to update profile");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
