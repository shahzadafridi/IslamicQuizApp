package com.opriday.islamicquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opriday.islamicquiz.R;
import com.opriday.islamicquiz.model.Category;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    String arr[];
    List<Category> categoryList;

    public CustomSpinnerAdapter(@NonNull Context context, String[] arr, List<Category> categoryList) {
        this.context = context;
        this.arr = arr;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.spinner_item_tv);
        TextView line = (TextView) convertView.findViewById(R.id.spinner_item_line);
        textView.setText(categoryList.get(position).getName());
        return convertView;
    }
}
