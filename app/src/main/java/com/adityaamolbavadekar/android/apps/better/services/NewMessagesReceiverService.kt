package com.adityaamolbavadekar.android.apps.better.services

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

class NewMessagesReceiverService : Service() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate() {
        super.onCreate()
        db = Firebase.firestore
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (intent != null) {

            db.collection("")
//                .add

        }
        return null
    }
}