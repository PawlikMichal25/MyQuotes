package com.example.quotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;


public class ThemedActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private boolean resumed = true;
    private boolean pendingThemeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTheme(this);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        if(pendingThemeChanged)
            this.recreate();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if(key.equals("pref_style_color") && resumed){

            // Looks much better, than sudden recreate
            this.finish();
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            this.startActivity(this.getIntent());

            pendingThemeChanged = false;
        }
        else
            pendingThemeChanged = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
