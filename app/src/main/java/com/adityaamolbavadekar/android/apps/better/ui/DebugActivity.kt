package com.adityaamolbavadekar.android.apps.better.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ActivityDebugBinding
import com.adityaamolbavadekar.android.apps.better.databinding.DebugTextviewBinding
import com.hypertrack.hyperlog.HyperLog
import com.parse.ParseObject
import java.text.SimpleDateFormat
import java.util.*

class DebugActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugBinding
    private var list = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.debug_mode)
        HyperLog.i(this.javaClass.name,"DEBUG MODE initialised...")

        try {
            val firstObject = ParseObject("FirstClass")
            firstObject.put(
                "message",
                "Hey ! First message from android. Parse is now connected on ${Build.MODEL} AT ${
                    SimpleDateFormat(
                        "dd MMM yyyy h:mm a",
                        Locale.getDefault()).format(Date())
                }"
            )
            firstObject.saveInBackground {
                if (it != null) {
                    it.localizedMessage?.let { message ->
                        HyperLog.e("DebugActivity", message)
                    }
                } else {
                    HyperLog.i("DebugActivity", "Object saved.")
                }
            }
        } catch (e: Exception) {
            HyperLog.e(this.javaClass.name, e.toString())
        }
        val adapter = SimpleTextViewAdapter(list)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        list.addAll(HyperLog.getDeviceLogsAsStringList(false))
        adapter.notifyDataSetChanged()


    }


    class SimpleTextViewAdapter(
        private val list: MutableList<String>
    ) : RecyclerView.Adapter<SimpleTextViewAdapter.ItemView>() {

        class ItemView(val binding: DebugTextviewBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
            return ItemView(
                DebugTextviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ItemView, position: Int) {
            val d = list[position]

            holder.apply {
                binding.debug.text = d
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }


    }
}
