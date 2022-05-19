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

class ContactsUploaderService : Service() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate() {
        super.onCreate()
        db = Firebase.firestore

    }

    override fun onBind(intent: Intent?): IBinder? {
        val monthThis = SimpleDateFormat("MM", Locale.ENGLISH).format(Date())
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && pref.getBoolean(
                Constants.LOGGED_IN_USER_PREF_KEY,
                false
            )
        ) {

            if (pref.getString(Constants.CONTACTS_SYNCED_ON_MONTH, monthThis) != monthThis) {
                upload(pref, monthThis)
            }else if (!pref.getBoolean(Constants.ARE_CONTACTS_SYNCED ,false)){
                upload(pref, monthThis)
            }
        }
        return null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun upload(pref: SharedPreferences, monthThis: String) {

        if (EasyPermissions.hasPermissions(
                this,
                android.Manifest.permission.READ_CONTACTS
            )
        ) {
            val hashMap = Constants().getContacts(this)

            db.collection(Constants.CONTACTS_COLLECTION)
                .document(pref.getString(Constants.ACCOUNT_PHONE_NUMBER, null)!!)
                .set(hashMap)
                .addOnSuccessListener {
                    pref.edit {
                        putBoolean(Constants.ARE_CONTACTS_SYNCED, true)
                        putString(
                            Constants.CONTACTS_SYNCED_ON_SYS_TIME,
                            System.currentTimeMillis().toString()
                        )
                        putString(
                            Constants.CONTACTS_SYNCED_ON_MONTH,
                            monthThis
                        )

                    }
                }
        }
    }


}