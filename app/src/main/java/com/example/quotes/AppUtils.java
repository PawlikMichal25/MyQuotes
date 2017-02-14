package com.example.quotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

public class AppUtils {
    public static boolean colorChanged = false;

    public static void updateTheme(Activity activity){
        if(colorChanged){
            setTheme(activity);
            activity.finish();
            Intent intent = activity.getIntent();
            activity.startActivity(intent);
            colorChanged = false;
        }
    }

    public static void setTheme(Activity activity){
        Context ctx = activity.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        int color = prefs.getInt(ctx.getString(R.string.pref_style_color), 0);

        if(color != 0){
            if(activity instanceof MainActivity){
                if (color == ContextCompat.getColor(ctx, R.color.indigo700)) {
                    activity.setTheme(R.style.Indigo_NoActionBar);
                } else if (color == ContextCompat.getColor(ctx, R.color.blue700)) {
                    activity.setTheme(R.style.Blue_NoActionBar);
                } else if (color == ContextCompat.getColor(ctx, R.color.originalGreen)) {
                    activity.setTheme(R.style.Green_NoActionBar);
                } else if (color == ContextCompat.getColor(ctx, R.color.lightGreen700)) {
                    activity.setTheme(R.style.LightGreen_NoActionBar);
                } else if (color == ContextCompat.getColor(ctx, R.color.brown700)) {
                    activity.setTheme(R.style.Brown_NoActionBar);
                } else if (color == ContextCompat.getColor(ctx, R.color.deepOrange700)) {
                    activity.setTheme(R.style.DeepOrange_NoActionBar);
                }
            }
            else {
                if (color == ContextCompat.getColor(ctx, R.color.indigo700)) {
                    activity.setTheme(R.style.Indigo);
                } else if (color == ContextCompat.getColor(ctx, R.color.blue700)) {
                    activity.setTheme(R.style.Blue);
                } else if (color == ContextCompat.getColor(ctx, R.color.originalGreen)) {
                    activity.setTheme(R.style.Green);
                } else if (color == ContextCompat.getColor(ctx, R.color.lightGreen700)) {
                    activity.setTheme(R.style.LightGreen);
                } else if (color == ContextCompat.getColor(ctx, R.color.brown700)) {
                    activity.setTheme(R.style.Brown);
                } else if (color == ContextCompat.getColor(ctx, R.color.deepOrange700)) {
                    activity.setTheme(R.style.DeepOrange);
                }
            }
        }
    }
}
