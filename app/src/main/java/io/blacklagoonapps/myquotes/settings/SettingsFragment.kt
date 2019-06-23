package io.blacklagoonapps.myquotes.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

import io.blacklagoonapps.myquotes.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, s: String) {
        addPreferencesFromResource(R.xml.preferences)
    }
}
