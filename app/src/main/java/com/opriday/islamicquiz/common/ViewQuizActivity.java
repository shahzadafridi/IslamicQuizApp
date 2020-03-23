package com.opriday.islamicquiz.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.Util.Constants;
import com.opriday.islamicquiz.adapter.QuizAdapter;
import com.opriday.islamicquiz.admin.MainActivity;
import com.opriday.islamicquiz.model.Quiz;
import com.opriday.islamicquiz.user.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewQuizActivity extends AppCompatActivity {

    QuizAdapter adapter;
    RecyclerView recyclerView;
    List<Quiz> quizList = new ArrayList<>();
    int result[];
    TextView title, count,timer;
    Button next, finish;
    LinearLayoutManager linearLayoutManager;
    int counter = 1;
    int totalQuiz = 0;
    String TAG = "ViewQuizActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quiz);
        InitUI();
    }

    private void InitUI() {
        timer = (TextView) findViewById(R.id.viewQuiz_timer);
        next = (Button) findViewById(R.id.viewQuiz_next_btn);
        finish = (Button) findViewById(R.id.viewQuiz_finish);
        title = (TextView) findViewById(R.id.viewQuiz_title);
        count = (TextView) findViewById(R.id.viewQuiz_count);
        recyclerView = (RecyclerView) findViewById(R.id.viewQuiz_rv);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.canScrollHorizontally(0);
        adapter = new QuizAdapter(quizList, ViewQuizActivity.this);
        recyclerView.setAdapter(adapter);
        title.setText(getIntent().getStringExtra("category"));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < totalQuiz) {
                    counter = counter + 1;
                    linearLayoutManager.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);
                    count.setText(counter + " / " + totalQuiz);
                } else {
                    next.setVisibility(View.INVISIBLE);
                    finish.setVisibility(View.VISIBLE);
                }
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finish.getText().toString().toLowerCase().contentEquals("finish")) {
                    showResultDialog();
                } else {
                    String email = Constants.getSharedPref(ViewQuizActivity.this).getString("email", "");
                    Intent intent;
                    if (email != "" && email.contains("admin")) {
                        intent = new Intent(ViewQuizActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(ViewQuizActivity.this, HomeActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        getQuizDataFromDB();
    }

    private void showResultDialog() {
        countDownTimer.cancel();
        timer.setVisibility(View.GONE);
        final Dialog dialog = Constants.onCreateDialog(ViewQuizActivity.this, R.layout.quiz_result_layout, false);
        finish.setText("Exit");
        next.setVisibility(View.INVISIBLE);
        count.setVisibility(View.INVISIBLE);
        TextView totalQ = (TextView) dialog.findViewById(R.id.quiz_result_total_question);
        TextView correctAns = (TextView) dialog.findViewById(R.id.quiz_result_correct_ans);
        TextView percentage = (TextView) dialog.findViewById(R.id.quiz_result_percent);
        int totalCorrectAns = getTotalCorrectAns();
        totalQ.setText(String.valueOf(quizList.size()));
        correctAns.setText("" + totalCorrectAns);
        Button btn = (Button) dialog.findViewById(R.id.quiz_result_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                linearLayoutManager.scrollToPosition(0);
                adapter.showResult();
            }
        });
        updateProfile(totalCorrectAns, quizList.size());
        dialog.show();
    }

    private void updateProfile(final int totalCorrectAns, final int totalQuiz) {
        DatabaseReference profile_path = FirebaseDatabase.getInstance().getReference("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profile_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    int total_attempt = Integer.parseInt(map.get("total_attempt"));
                    int score = Integer.parseInt(map.get("score"));
                    int total = Integer.parseInt(map.get("total"));
                    total_attempt = total_attempt + 1;
                    score = score + totalCorrectAns;
                    total = total + totalQuiz;
                    Map<String, Object> update = new HashMap<>();
                    update.put("total_attempt", String.valueOf(total_attempt));
                    update.put("score", String.valueOf(score));
                    update.put("total", String.valueOf(total));
                    dataSnapshot.getRef().updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "Profile updated");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Profile failed to update , " + e.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewQuizActivity", "Failed to update profile");
            }
        });
    }

    private void getQuizDataFromDB() {
        FirebaseDatabase.getInstance().getReference().child("quiz").child(getIntent().getStringExtra("category")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Quiz quiz = snap.getValue(Quiz.class);
                            quizList.add(quiz);
                        }
                        totalQuiz = quizList.size();
                        initResultArrayWithZero(totalQuiz);
                        adapter.setQuizList(quizList);
                        count.setText("1 / " + totalQuiz);
                        if (!Constants.getSharedPref(ViewQuizActivity.this).getString("email","").contains("admin")) {
                            showTimer();
                        }else {
                            timer.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(ViewQuizActivity.this, "No Quiz found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewQuizActivity.this, "No Quiz found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewQuizActivity.this, "No Quiz found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    CountDownTimer countDownTimer;
    private void showTimer(){
        countDownTimer =  new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished / 1000);
                long minutes = seconds/60;
                seconds = seconds % 60;
                timer.setText(minutes +": " + seconds);
            }

            public void onFinish() {
                timer.setText("00:00");
                showResultDialog();
            }
        }.start();
    }

    private int getTotalCorrectAns() {
        int correct = 0;
        for (int i = 0; i < result.length; i++) {
            Log.e("result", "Position: ["+i+"] , Result[" + i + "]: " + result[i] + " , Correct option: [" + quizList.get(i).getCorrect()+"]");
            if (String.valueOf(result[i]).contentEquals(quizList.get(i).getCorrect())) {
                correct = correct + 1;
            }
        }
        return correct;
    }


    private void initResultArrayWithZero(int totalQuiz) {
        result = new int[totalQuiz];
        adapter.setResultArray(result);
        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }
    }
}
