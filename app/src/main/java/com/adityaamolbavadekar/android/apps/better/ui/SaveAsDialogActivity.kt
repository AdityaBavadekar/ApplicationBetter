package com.adityaamolbavadekar.android.apps.better.ui

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adityaamolbavadekar.android.apps.better.databinding.SaveAsBinding

class SaveAsDialogActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val binding = SaveAsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            createNote(binding.title.text.toString(), binding.body.text.toString())
        }

        binding.cancelButton.setOnClickListener {
            this.finishAndRemoveTask()
        }

    }

    private fun createNote(title: String, body: String) {
        Toast.makeText(this, "Note saved", Toast.LENGTH_LONG).show()
        this.finishAndRemoveTask()
    }

}