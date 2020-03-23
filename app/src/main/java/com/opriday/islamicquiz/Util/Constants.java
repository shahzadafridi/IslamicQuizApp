package com.opriday.islamicquiz.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class Constants {

    public static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences("app",Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getSharedPrefEditor(Context context){
        return context.getSharedPreferences("app",Context.MODE_PRIVATE).edit();
    }

    public static Dialog onCreateDialog(Context context, int layout, boolean cancelable) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height=metrics.heightPixels;
        Dialog dialog = new Dialog(context,android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout((6*width)/7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(cancelable);
        return dialog;
    }
}
