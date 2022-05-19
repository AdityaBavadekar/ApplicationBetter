package com.adityaamolbavadekar.android.apps.better.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ActivityMainBinding
import com.adityaamolbavadekar.android.apps.better.databinding.ActivytChatSelectorBinding
import com.adityaamolbavadekar.android.apps.better.ui.login.LoginFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.hypertrack.hyperlog.HyperLog
import java.io.File
import java.nio.charset.Charset

class ChatSelectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivytChatSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivytChatSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    companion object {
        const val TAG = "ChatSelectorActivity"
    }

    fun createMe(){
        val b = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.fullscreen_selector,null)
        val l = v.findViewById<LinearLayout>(R.id.linearLayout)
        b.setView(v)
        b.create()
        b.show()
    }

}
