package com.adityaamolbavadekar.android.apps.better.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MainRecyclerViewAdapter
    private lateinit var list: MutableList<Note>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)

        val complimentaryNote = Note(
            0,
            "Title",
            "Boooooooooooooooooooooooooody",
            "21 Jan 2022",
            "1 Feb 2022",
            labels = arrayListOf("Welcome", "Better App"),
            "",
            links = arrayListOf("https://github.com/AdityaBavadekar"),
            hasImage = false,
            hasLabels = true,
            hasLinksPreviews = true
        )
        val complimentaryNote2 = Note(
            0,
            "Title",
            "Boooooooooooooooooooooooooody",
            "21 Jan 2022",
            "1 Feb 2022",
            labels = arrayListOf("Welcome", "Better App"),
            "",
            links = arrayListOf("https://youtu.be/jddHwr3Guv"),
            hasImage = false,
            hasLabels = true,
            hasLinksPreviews = true
        )
        val complimentaryNote3 = Note(
            0,
            "Title",
            "Boooooooooooooooooooooooooody",
            "21 Jan 2022",
            "1 Feb 2022",
            labels = arrayListOf("Welcome", "Better App"),
            "",
            links = arrayListOf("https://material.io","https://google.com"),
            hasImage = false,
            hasLabels = true,
            hasLinksPreviews = true
        )
        val complimentaryNote4 = Note(
            0,
            "Title",
            "Boooooooooooooooooooooooooody",
            "21 Jan 2022",
            "1 Feb 2022",
            labels = arrayListOf("Welcome", "Better App"),
            "",
            links = arrayListOf("https://github.com","https://github.com/AdityaBavadekar/Hiphe","https://open.spotify.com","https://developer.android.com"),
            hasImage = false,
            hasLabels = true,
            hasLinksPreviews = true
        )
        list = mutableListOf(complimentaryNote,complimentaryNote2,complimentaryNote2,complimentaryNote2,complimentaryNote3,complimentaryNote4,complimentaryNote3)

        recyclerView = binding.recyclerView
        layoutManager = LinearLayoutManager(requireActivity())
        adapter = MainRecyclerViewAdapter(requireContext(),list)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        return binding.root
    }
}