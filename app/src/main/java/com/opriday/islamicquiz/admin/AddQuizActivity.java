package com.opriday.islamicquiz.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.Util.Constants;
import com.opriday.islamicquiz.adapter.CustomSpinnerAdapter;
import com.opriday.islamicquiz.model.Category;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuizActivity extends AppCompatActivity {

    String TAG = "AddQuizActivity";
    String[] categoriesArr = {"Select Category","Islamic","Genral","Science","Knowledge"};
    EditText quizTitle,option1,option2,option3,correctOption;
    Button done;
    ProgressBar progressBar;
    Spinner categories;
    CustomSpinnerAdapter adapter;
    List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        InitUI();
    }

    public void InitUI(){
        quizTitle = (EditText) findViewById(R.id.quiz_title_et);
        option1 = (EditText) findViewById(R.id.quiz_option1_et);
        option2 = (EditText) findViewById(R.id.quiz_option2_et);
        option3 = (EditText) findViewById(R.id.quiz_option3_et);
        correctOption = (EditText) findViewById(R.id.quiz_option4_et2);
        done = (Button) findViewById(R.id.add_quiz_button);
        progressBar = (ProgressBar) findViewById(R.id.add_quiz_progress);
        categories = (Spinner) findViewById(R.id.add_quiz_spinner);
        adapter = new CustomSpinnerAdapter(this,categoriesArr,getCategoryList());
        categories.setAdapter(adapter);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (validation()){
                    done.setEnabled(false);
                    postQuiz(
                            categoryList.get(categories.getSelectedItemPosition()).getName(),
                            quizTitle.getText().toString(),
                            option1.getText().toString(),
                            option2.getText().toString(),
                            option3.getText().toString(),
                            correctOption.getText().toString()
                    );
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    done.setEnabled(true);
                    Toast.makeText(AddQuizActivity.this,"Enter complete details",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void postQuiz(String category, String title, String option1, String option2, String option3,String correctOption){
        final Map<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("category",category.toLowerCase());
        map.put("option1",option1);
        map.put("option2",option2);
        map.put("option3",option3);
        map.put("correct",correctOption);
        FirebaseDatabase.getInstance().getReference().child("quiz").child(category).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddQuizActivity.this,"Quiz added successfully", Toast.LENGTH_SHORT);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddQuizActivity.this,MainActivity.class));
                        }
                    },1000);
                }else {
                    done.setEnabled(true);
                    Toast.makeText(AddQuizActivity.this,"Error, Quiz added unsccessfully", Toast.LENGTH_SHORT);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                done.setEnabled(true);
                Toast.makeText(AddQuizActivity.this,"Error, Quiz added unsccessfully", Toast.LENGTH_SHORT);
            }
        });
    }

    public boolean validation(){

        boolean isValid = true;

        if (TextUtils.isEmpty(quizTitle.getText().toString())){
            quizTitle.setError("Enter Title");
            isValid = false;
        }

        if (TextUtils.isEmpty(option1.getText().toString())){
            option1.setError("Enter Text");
            isValid = false;
        }

        if (TextUtils.isEmpty(option2.getText().toString())){
            option2.setError("Enter Text");
            isValid = false;
        }

        if (TextUtils.isEmpty(option3.getText().toString())){
            option3.setError("Enter Text");
            isValid = false;
        }

        if (TextUtils.isEmpty(correctOption.getText().toString())){
            correctOption.setError("Enter Text");
            isValid = false;
        }

//        if (categories.getSelectedItemPosition() == 0){
//            isValid = false;
//        }

        return isValid;
    }

    public List<Category> getCategoryList(){
        Gson gson = new Gson();
        String json = Constants.getSharedPref(AddQuizActivity.this).getString("category", null);
        Type type = new TypeToken<List<Category>>() {}.getType();
        categoryList = gson.fromJson(json, type);
        Log.e(TAG,new Gson().toJson(categoryList));
        return  categoryList;
    }

}
