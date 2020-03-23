package com.opriday.islamicquiz.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.model.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.MyViewHolder> {

    List<Quiz> quizList;
    Context context;
    int[] result;
    int mSelectedItem = -1;
    String TAG = "QuizAdapter";
    boolean isShowResult = false;

    public QuizAdapter(List<Quiz> quizList, Context context) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Quiz quiz = quizList.get(position);
        holder.quizNumber.setText("Question " + (position + 1));
        holder.title.setText(quiz.getTitle());
        holder.radionBtnOption1.setText("\t" + quiz.getOption1());
        holder.radionBtnOption2.setText("\t" + quiz.getOption2());
        holder.radionBtnOption3.setText("\t" + quiz.getOption3());
        holder.radionBtnOption1.setChecked(false);
        holder.radionBtnOption2.setChecked(false);
        holder.radionBtnOption3.setChecked(false);

        if (isShowResult) {
            holder.linearLayout.setVisibility(View.VISIBLE);

            switch (result[position]) {
                case 0:
                    holder.radionBtnOption1.setChecked(false);
                    holder.radionBtnOption2.setChecked(false);
                    holder.radionBtnOption3.setChecked(false);
                    break;
                case 1:
                    holder.radionBtnOption1.setChecked(true);
                    holder.radionBtnOption2.setChecked(false);
                    holder.radionBtnOption3.setChecked(false);
                    break;
                case 2:
                    holder.radionBtnOption1.setChecked(false);
                    holder.radionBtnOption2.setChecked(true);
                    holder.radionBtnOption3.setChecked(false);
                    break;
                case 3:
                    holder.radionBtnOption1.setChecked(false);
                    holder.radionBtnOption2.setChecked(false);
                    holder.radionBtnOption3.setChecked(true);
                    break;
            }
            int correct = Integer.parseInt(quiz.getCorrect());
            if (result[position] == correct) {
                Log.e(TAG, "Correct, Position: [" + position + "] , Result: [" + result[position] + "] , correct option: [" + correct + "]");
                holder.questionStatus.setImageResource(R.drawable.correct_icon);
                holder.questionTitle.setText("Correct Answer");
                holder.questionTitle.setTextColor(context.getResources().getColor(R.color.correct));
            } else {
                Log.e(TAG, "Wrong, Position: [" + position + "] , Result: [" + result[position] + "] , correct option: [" + correct + "]");
                holder.questionStatus.setImageResource(R.drawable.wrong_icon);
                holder.questionTitle.setText("Wrong Answer");
                holder.questionTitle.setTextColor(context.getResources().getColor(R.color.wrong));
            }
            holder.correctOption.setVisibility(View.VISIBLE);
            holder.correctOption.setText("Option " + quiz.getCorrect() + " is correct answer.");

            holder.radioGroup.setEnabled(false);
            holder.radionBtnOption1.setEnabled(false);
            holder.radionBtnOption2.setEnabled(false);
            holder.radionBtnOption3.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public void setQuizList(List<Quiz> list) {
        this.quizList = list;
        notifyDataSetChanged();
    }

    public void showResult() {
        isShowResult = true;
        notifyDataSetChanged();
    }

    public void setResultArray(int result[]) {
        this.result = result;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView questionStatus;
        RadioGroup radioGroup;
        LinearLayout linearLayout;
        RadioButton radionBtnOption1, radionBtnOption2, radionBtnOption3;
        TextView title, questionTitle, quizNumber, correctOption;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.quiz_item_status_ll);
            questionStatus = (ImageView) itemView.findViewById(R.id.quiz_item_status_icon);
            questionTitle = (TextView) itemView.findViewById(R.id.quiz_item_status_title);
            correctOption = (TextView) itemView.findViewById(R.id.quiz_item_correctOPtion);
            quizNumber = (TextView) itemView.findViewById(R.id.quiz_item_numbere);
            title = (TextView) itemView.findViewById(R.id.quiz_item_title);
            radionBtnOption1 = (RadioButton) itemView.findViewById(R.id.quiz_item_option1_radio_btn);
            radionBtnOption2 = (RadioButton) itemView.findViewById(R.id.quiz_item_option2_radio_btn);
            radionBtnOption3 = (RadioButton) itemView.findViewById(R.id.quiz_item_option3_radio_btn);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                    int position = getAdapterPosition();
                    if (checkedRadioButton.isPressed()) {
                        if (R.id.quiz_item_option1_radio_btn == checkedRadioButton.getId()) {
                            radionBtnOption1.setChecked(true);
                            result[position] = 1;
                            Log.e(TAG, "OnClick, Position: [" + position + "] , Result: [" + result[position] + "] , correct option: [" + quizList.get(position).getCorrect() + "]");
                        }

                        if (R.id.quiz_item_option2_radio_btn == checkedRadioButton.getId()) {
                            radionBtnOption2.setChecked(true);
                            result[position] = 2;
                            Log.e(TAG, "OnClick, Position: [" + position + "] , Result: [" + result[position] + "] , correct option: [" + quizList.get(position).getCorrect() + "]");
                        }

                        if (R.id.quiz_item_option3_radio_btn == checkedRadioButton.getId()) {
                            radionBtnOption3.setChecked(true);
                            result[position] = 3;
                            Log.e(TAG, "OnClick, Position: [" + position + "] , Result: [" + result[position] + "] , correct option: [" + quizList.get(position).getCorrect() + "]");
                        }
                    } else {
                        Log.e(TAG, "RadioButton is not pressed");
                    }
                }
            });
        }

    }
}
