package io.blacklagoonapps.myquotes.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.blacklagoonapps.myquotes.R;


public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
