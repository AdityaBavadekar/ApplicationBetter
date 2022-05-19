package com.adityaamolbavadekar.android.apps.better.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adityaamolbavadekar.android.apps.better.databinding.Login2Binding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hypertrack.hyperlog.HyperLog

class LoginActivity2 : AppCompatActivity() {

    private lateinit var binding: Login2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HyperLog.i(this.javaClass.toString(),"onCreate")
        binding = Login2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.fragment.id, LoginFragment())
                .commit()
        }
    }

}
