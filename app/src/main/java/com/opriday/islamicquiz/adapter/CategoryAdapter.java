package com.opriday.islamicquiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.common.ViewQuizActivity;
import com.opriday.islamicquiz.model.Category;
import com.opriday.islamicquiz.model.Quiz;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Category> categoryList;
    Context context;
    String TAG = "CategoryAdapter";
    int CATEGORY_TYPE = 1;
    int HOME_CATEGORY_TYPE = 2;
    int VIEW_TYPE = 0;

    public CategoryAdapter(List<Category> categoryList, Context context, int VIEW_TYPE) {
        this.categoryList = categoryList;
        this.context = context;
        this.VIEW_TYPE = VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE == CATEGORY_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }else if (VIEW_TYPE == HOME_CATEGORY_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.home_category_item, parent, false);
            MySecondViewHolder viewHolder = new MySecondViewHolder(view);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if (VIEW_TYPE == CATEGORY_TYPE){
            ((MyViewHolder)holder).title.setText(category.getName());
        }else {
            ((MySecondViewHolder)holder).title.setText(category.getName());
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setQuizList(List<Category> list) {
        this.categoryList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.category_item_title);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Intent intent = new Intent(context, ViewQuizActivity.class);
            intent.putExtra("category",categoryList.get(pos).getName().toLowerCase());
            context.startActivity(intent);
        }
    }

    public class MySecondViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        public MySecondViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.category_title);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Intent intent = new Intent(context, ViewQuizActivity.class);
            intent.putExtra("category",categoryList.get(pos).getName().toLowerCase());
            context.startActivity(intent);
        }
    }
}
