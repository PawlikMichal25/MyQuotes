package io.blacklagoonapps.myquotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

@SuppressLint("Registered")
open class ThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
    }

    private fun setTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val color = prefs.getInt(getString(R.string.pref_style_color), ContextCompat.getColor(this, R.color.blue700))

        if (this is MainActivity) {
            when (color) {
                ContextCompat.getColor(this, R.color.indigo700) -> setTheme(R.style.Indigo_NoActionBar)
                ContextCompat.getColor(this, R.color.blue700) -> setTheme(R.style.Blue_NoActionBar)
                ContextCompat.getColor(this, R.color.green700) -> setTheme(R.style.Green_NoActionBar)
                ContextCompat.getColor(this, R.color.lightGreen700) -> setTheme(R.style.LightGreen_NoActionBar)
                ContextCompat.getColor(this, R.color.brown700) -> setTheme(R.style.Brown_NoActionBar)
                ContextCompat.getColor(this, R.color.deepOrange700) -> setTheme(R.style.DeepOrange_NoActionBar)
            }
        } else {
            when (color) {
                ContextCompat.getColor(this, R.color.indigo700) -> setTheme(R.style.Indigo)
                ContextCompat.getColor(this, R.color.blue700) -> setTheme(R.style.Blue)
                ContextCompat.getColor(this, R.color.green700) -> setTheme(R.style.Green)
                ContextCompat.getColor(this, R.color.lightGreen700) -> setTheme(R.style.LightGreen)
                ContextCompat.getColor(this, R.color.brown700) -> setTheme(R.style.Brown)
                ContextCompat.getColor(this, R.color.deepOrange700) -> setTheme(R.style.DeepOrange)
            }
        }
    }
}
