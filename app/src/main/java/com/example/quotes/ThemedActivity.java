package com.example.quotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;


public class ThemedActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
    }


    private void setTheme(){
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        int color = prefs.getInt(getString(R.string.pref_style_color), ContextCompat.getColor(ctx, R.color.blue700));

        if(this instanceof MainActivity){
            if (color == ContextCompat.getColor(ctx, R.color.indigo700)) {
                setTheme(R.style.Indigo_NoActionBar);
            } else if (color == ContextCompat.getColor(ctx, R.color.blue700)) {
                setTheme(R.style.Blue_NoActionBar);
            } else if (color == ContextCompat.getColor(ctx, R.color.green700)) {
                setTheme(R.style.Green_NoActionBar);
            } else if (color == ContextCompat.getColor(ctx, R.color.lightGreen700)) {
                setTheme(R.style.LightGreen_NoActionBar);
            } else if (color == ContextCompat.getColor(ctx, R.color.brown700)) {
                setTheme(R.style.Brown_NoActionBar);
            } else if (color == ContextCompat.getColor(ctx, R.color.deepOrange700)) {
                setTheme(R.style.DeepOrange_NoActionBar);
            }
        }
        else {
            if (color == ContextCompat.getColor(ctx, R.color.indigo700)) {
                setTheme(R.style.Indigo);
            } else if (color == ContextCompat.getColor(ctx, R.color.blue700)) {
                setTheme(R.style.Blue);
            } else if (color == ContextCompat.getColor(ctx, R.color.green700)) {
                setTheme(R.style.Green);
            } else if (color == ContextCompat.getColor(ctx, R.color.lightGreen700)) {
                setTheme(R.style.LightGreen);
            } else if (color == ContextCompat.getColor(ctx, R.color.brown700)) {
                setTheme(R.style.Brown);
            } else if (color == ContextCompat.getColor(ctx, R.color.deepOrange700)) {
                setTheme(R.style.DeepOrange);
            }
        }
    }
}
