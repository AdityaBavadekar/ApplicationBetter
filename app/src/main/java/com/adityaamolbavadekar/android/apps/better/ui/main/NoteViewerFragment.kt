package com.adityaamolbavadekar.android.apps.better.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.databinding.FragmentMainBinding
import com.adityaamolbavadekar.android.apps.better.databinding.FragmentTemplatesBinding


class NoteViewerFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)

        return binding.root
    }
}