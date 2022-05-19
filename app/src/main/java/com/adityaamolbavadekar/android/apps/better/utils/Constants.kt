package com.adityaamolbavadekar.android.apps.better.utils

import android.content.Context
import android.os.Build
import android.provider.ContactsContract
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Constants {

    companion object {

        const val APP_PACKAGE_NAME = "com.adityaamolbavadekar.android.apps.better"
        const val TIMESTAMP_FORMAT = "dd MMM yyyy"
        const val TIMESTAMP_FORMAT_TIME = "dd MMM yyyy HH:mm:ss"
        const val TIMESTAMP_FORMAT_TIME_CHAT_MSG = "dd MMM yyyy HH:mm"
        const val TIMESTAMP_FORMAT_TIME_UNDERSCORED = "dd_MMM_yyyy_HH:mm:ss"
        const val SINGLE_DATE_FORMAT_CHAT = "dd"
        const val SINGLE_DATE_MONTH_FORMAT_CHAT = "MMM"
        const val DATABASE_VERSION = 1
        const val DATABASE_TABLE_NAME = "better_notes"
        const val LOGGED_IN_USER_PREF_KEY = "LOGGED_IN"
        const val USER_PHONE_PREF_KEY = "USER_PHONE"
        const val ACCOUNT_CREATION_TIMESTAMP = "ACCOUNT_CREATION_TIMESTAMP"
        const val ACCOUNT_CREATION_SYS_TIMESTAMP = "ACCOUNT_CREATION_SYS_TIMESTAMP"
        const val ACCOUNT_USERID = "ACCOUNT_USERID"
        const val ACCOUNT_PHONE_NUMBER = "ACCOUNT_PHONE_NUMBER"
        const val ACCOUNT_NAME = "ACCOUNT_USER_PUBLIC_NAME"
        const val ACCOUNT_ABOUT = "ACCOUNT_USER_PUBLIC_ABOUT"
        const val USER_CHATS_COLLECTION = "chats"
        const val USERS_COLLECTION = "users"
        const val CONTACTS_COLLECTION = "contacts"
        const val ARE_CONTACTS_SYNCED = "ARE_CONTACTS_SYNCED"
        const val CONTACTS_SYNCED_ON_SYS_TIME = "CONTACTS_SYNCED_ON_SYS"
        const val CONTACTS_SYNCED_ON_MONTH = "CONTACTS_SYNCED_ON_MONTH"

    }

    public fun setUserLoginData(context: Context, phoneNumber: String) {
        val pf = PreferenceManager.getDefaultSharedPreferences(context)
        pf.edit {
            putBoolean(Constants.LOGGED_IN_USER_PREF_KEY, true)
            putString(Constants.USER_PHONE_PREF_KEY, phoneNumber)
        }
    }

    public fun setUserLoginTimeData(
        context: Context,
        accountCreationTimestamp: String,
        systemLong: Long,
        userId: String,
        phoneNumber: String
    ) {
        val pf = PreferenceManager.getDefaultSharedPreferences(context)
        pf.edit {
            putString(ACCOUNT_CREATION_TIMESTAMP, accountCreationTimestamp)
            putLong(ACCOUNT_CREATION_SYS_TIMESTAMP, systemLong)
            putString(ACCOUNT_USERID, userId)
            putString(ACCOUNT_PHONE_NUMBER, phoneNumber)
        }
    }

    public fun deleteUserLoginData(context: Context) {
        val pf = PreferenceManager.getDefaultSharedPreferences(context)
        pf.edit {
            remove(LOGGED_IN_USER_PREF_KEY)
            remove(USER_PHONE_PREF_KEY)
        }
    }

    fun setUserFirebaseName(context: Context, auth: FirebaseAuth, name: String) {
        val u = auth.currentUser!!
        u.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        ).addOnCompleteListener {
            Toast.makeText(context, context.getString(R.string.profile_name_updated), Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getContacts(context: Context): HashMap<String, String> {

        val hashMap = hashMapOf<String,String>()
        val c =
            context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null)

        if (c != null && c.count > 0) {

            while (c.moveToNext()) {
                val cHAS_PHONE = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                val cID = c.getColumnIndex(ContactsContract.Contacts._ID)
                val id = c.getString(cID)!!
                val cNAME = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val name = c.getString(cNAME)!!

                if (c.getInt(cHAS_PHONE) > 0) {
                    val rs = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (rs?.moveToNext() == true){
                        val rsPHONE = rs.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneNumber = rs.getString(rsPHONE)
                        hashMap[phoneNumber] = name
                    }
                    rs?.close()
                }
            }

        }else{
            c?.close()
        }

        return hashMap
    }

}
