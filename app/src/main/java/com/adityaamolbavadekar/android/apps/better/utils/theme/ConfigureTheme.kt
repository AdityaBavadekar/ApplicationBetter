package com.adityaamolbavadekar.android.apps.better.utils.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class ConfigureTheme {

    fun onCreate(activity: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        try {
            when (prefs.getString("theme", "3")) {
                "1" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                "2" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                "3" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        } catch (e: Exception) {
        }

    }

    fun getTheme(activity: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        return prefs.getString("theme", "3").toString()
    }


}
