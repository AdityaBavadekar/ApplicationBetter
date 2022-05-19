package com.adityaamolbavadekar.android.apps.better.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ActivityMainBinding
import com.adityaamolbavadekar.android.apps.better.ui.DebugActivity
import com.adityaamolbavadekar.android.apps.better.ui.accountinfo.AccountInfoFragment
import com.hypertrack.hyperlog.HyperLog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var cFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HyperLog.i(this.javaClass.toString(),"onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.fragment.id, MainFragment2())
                .commit()
        }/* else {
            cFragment = supportFragmentManager.getFragment(savedInstanceState, "LAST_FRAGMENT")!!
        }*/
/*
        binding.fabAdd.setOnClickListener {
            HyperLog.i(TAG, "FabAdd Clicked")
            val text = HyperLog.getDeviceLogsAsStringList().toString()
                val i = Intent(Intent.ACTION_SENDTO)
                i.putExtra(Intent.EXTRA_EMAIL, "ADITYARBAVADEKAR@GMAIL.COM")
                i.putExtra(Intent.EXTRA_SUBJECT, "hYPERlOGS")
                i.putExtra(Intent.EXTRA_TEXT, text)
                i.data = Uri.parse("mailto:")
                startActivity(i)
        }*/
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragment.id, fragment)
            .setReorderingAllowed(false)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(fragment.toString())
            .commit()
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val name = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("ACCOUNT_USER_PUBLIC_NAME", "A")!!
        val item = menu?.findItem(R.id.action_manage_account)
        val fl = item?.actionView as FrameLayout
        val tv = fl.findViewById<TextView>(R.id.nameLetter)
        tv.text = name.first().toString()
        fl.setOnClickListener {
            if (!supportFragmentManager.fragments.contains(AccountInfoFragment())) {
                changeFragment(AccountInfoFragment())
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_debug -> {
                startActivity(Intent(this, DebugActivity::class.java))
            }
        }
        return true
    }

/*
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "LAST_FRAGMENT", cFragment)
    }*/

}
