package com.adityaamolbavadekar.android.apps.better.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.Shortcuts
import com.adityaamolbavadekar.android.apps.better.ui.login.LoginActivity
import com.adityaamolbavadekar.android.apps.better.ui.main.MainActivity
import com.adityaamolbavadekar.android.apps.better.utils.Constants

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    "new_users",
                    "New users notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notified when new account is created"
                }
            )
        }
        val pf = PreferenceManager.getDefaultSharedPreferences(this)
        val l = pf.getBoolean(Constants.LOGGED_IN_USER_PREF_KEY, false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            Shortcuts.setUp(this)
        }
        if (l) {
            normalBehaviour()
        } else {
            behaviourSub()
        }

    }

    private fun normalBehaviour() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }

    private fun behaviourSub() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}