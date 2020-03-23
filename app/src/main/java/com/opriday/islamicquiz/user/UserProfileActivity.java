package com.opriday.islamicquiz.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.Util.Constants;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String TAG = "UserProfileActivity";
    private FirebaseAuth mAuth;
    EditText username,age;
    ProgressBar progressBar;
    CircleImageView profileImage;
    Button done;
    int PICK_IMAGE = 001;
    Uri imageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(String.valueOf(System.currentTimeMillis()));
        InitUI();
    }

    public void InitUI(){
        username = (EditText) findViewById(R.id.profile_username_et);
        username.requestFocus();
        age = (EditText) findViewById(R.id.profile_age_et);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);
        done = (Button) findViewById(R.id.profile_btn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (validation()){
                    if (imageUri == null){
                        updateProfile("",username.getText().toString(), age.getText().toString());
                    }else {
                        uploadImage(imageUri,username.getText().toString(), age.getText().toString());
                    }
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }else {
                    ActivityCompat.requestPermissions(UserProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},001);
                    Toast.makeText(UserProfileActivity.this,"Give permission to access gallery.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validation(){

        boolean isValid = true;

        if (TextUtils.isEmpty(username.getText().toString())){
            username.setError("Enter username");
            isValid = false;
        }

        if (TextUtils.isEmpty(age.getText().toString())){
            age.setError("Enter age");
            isValid = false;
        }

        return isValid;
    }

    public void updateProfile(final String imageUrl, final String username, final String age){
        DatabaseReference profile_path = FirebaseDatabase.getInstance().getReference("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String, String> map = new HashMap<>();
        map.put("email",Constants.getSharedPref(UserProfileActivity.this).getString("email",""));
        map.put("username",username);
        map.put("age",age);
        map.put("image",imageUrl);
        map.put("total_attempt","0");
        map.put("score","0");
        map.put("total","0");
        map.put("percentage","0");
        profile_path.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                createSession(username,age,imageUrl);
                progressBar.setVisibility(View.INVISIBLE);
                homeActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UserProfileActivity.this,"Failed to update profile",Toast.LENGTH_LONG).show();
                Log.e(TAG,e.getMessage());
            }
        });
    }

    private void uploadImage(Uri file, final String username, final String age) {

        mStorageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downUri = task.getResult();
                                Toast.makeText(UserProfileActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                Log.e("onComplete", "onComplete: Url: " + downUri.toString());
                                progressBar.setVisibility(View.INVISIBLE);
                                updateProfile(downUri.toString(), username, age);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG,"Error:"+exception.getMessage());
                        Toast.makeText(UserProfileActivity.this,"Uploaded failed",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void createSession(String username,String age, String image){
        SharedPreferences.Editor editor = Constants.getSharedPrefEditor(this);
        editor.putString("username",username);
        editor.putString("age",age);
        editor.putString("image",image);
        editor.apply();
    }

    public void homeActivity(){
        Intent intent = new Intent(UserProfileActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            profileImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 001){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }
}
