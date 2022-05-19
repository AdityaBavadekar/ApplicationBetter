package com.adityaamolbavadekar.android.apps.better.ui.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.databinding.FragmentTemplatesBinding


class TemplatesFragment : Fragment() {

    private lateinit var binding: FragmentTemplatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTemplatesBinding.inflate(layoutInflater)

        return binding.root
    }
}