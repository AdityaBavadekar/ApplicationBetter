package com.adityaamolbavadekar.android.apps.better.ui.settings

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.adityaamolbavadekar.android.apps.better.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, HeaderFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.title_activity_settings)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)

            val auth = Firebase.auth
            val logout = findPreference<Preference>("logout")
            if (auth.currentUser != null){
                logout?.title = "LOGOUT ("+ auth.currentUser!!.phoneNumber!!.toString() + ")"
                logout?.setOnPreferenceClickListener {
                    val b = AlertDialog.Builder(requireContext())
                    b.setTitle("Logout")
                    b.setMessage(resources.getString(R.string.are_you_sure_logout,auth.currentUser!!.phoneNumber!!.toString()))
                        b.setPositiveButton("LOGOUT"){d,_->
                            auth.signOut()
                            val am = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                            am.clearApplicationUserData()
                            d.dismiss()
                            requireActivity().finishAndRemoveTask()
                        }
                    b.setNeutralButton("CANCEL"){d,_->
                        d.cancel()
                    }
                    b.create()
                    b.show()
                    true
                }
            }else{
                logout?.title = "LOGOUT"
                logout?.setOnPreferenceClickListener {
                    Toast.makeText(it.context,resources.getString(R.string.something_went_wrong),Toast.LENGTH_LONG).show()
                    true
                }
            }


        }
    }

    class MessagesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.messages_preferences, rootKey)
        }
    }

    class SyncFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.sync_preferences, rootKey)
        }
    }

}