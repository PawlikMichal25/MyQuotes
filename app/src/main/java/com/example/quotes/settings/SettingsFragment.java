package com.example.quotes.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.quotes.AppUtils;
import com.example.quotes.R;
import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Preference preference = findPreference(key);
        if(preference instanceof ColorPreferenceCompat){
            ColorPreferenceCompat cpc = (ColorPreferenceCompat)preference;
            AppUtils.colorChanged = true;
            AppUtils.updateTheme(getActivity());
            AppUtils.colorChanged = true;   // TODO Temporary, it will be better with Observer in future.
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
