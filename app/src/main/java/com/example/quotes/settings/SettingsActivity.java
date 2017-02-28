package com.example.quotes.settings;

import android.os.Bundle;

import com.example.quotes.ThemedActivity;
import com.example.quotes.R;

public class SettingsActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsFragment())
                .commit();
    }
}
