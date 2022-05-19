package com.adityaamolbavadekar.android.apps.better.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.databinding.ListViewLayoutBinding
import com.adityaamolbavadekar.android.apps.better.room.chats_list.ChatEntity
import com.adityaamolbavadekar.android.apps.better.room.chats_list.ChatListViewModel
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.hypertrack.hyperlog.HyperLog
import com.parse.ParseObject


class MainFragment2 : Fragment() {

    private lateinit var binding: ListViewLayoutBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ChatListAdapter
    private lateinit var list: MutableList<ChatItem>
    private lateinit var listChatEntity: MutableList<ChatEntity>
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var phoneNumber: String
    private lateinit var mChatListViewModel: ChatListViewModel
    private var app: FirebaseApp = Firebase.app

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initializationP()
        initRecyclerViewSetup()
        initViewModel()
        initGetChatsList()
        addPersonalNotesItem()
        clickListeners()
        upload()

        return binding.root
    }

    private fun upload() {


        val obj = ParseObject("BetterApp")
            obj.put("type", auth.currentUser!!.providerData.toString())
            obj.put("user", auth.currentUser!!.displayName ?: "")
            obj.put("phone", auth.currentUser!!.phoneNumber ?: "")
            obj.put("uid", auth.currentUser!!.uid)
        obj.saveInBackground()

        HyperLog.i(
            "prefs",
            PreferenceManager.getDefaultSharedPreferences(requireContext()).all.toString()
        )
    }

    private fun clickListeners() {
        binding.fab.setOnClickListener {
            showChooser()
        }
    }

    private fun initializationP() {
        binding = ListViewLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        binding.nothing.isVisible = false
        binding.recyclerView.isVisible = true
        phoneNumber = auth.currentUser!!.phoneNumber!!.toString()
        listChatEntity = mutableListOf()
    }

    private fun initGetChatsList() {
        mChatListViewModel.getList().observe(viewLifecycleOwner) {
            listChatEntity.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViewModel() {
        mChatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]
    }

    private fun addPersonalNotesItem() {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val hasNote = pref.getBoolean("HAS_PERSONAL", false)
        if (!hasNote) {
            pref.edit {
                putBoolean("HAS_PERSONAL", true)
                putString("PERSONAL", phoneNumber)
            }
            val item = ChatEntity(
                0,
                "Personal Notes",
                phoneNumber,
                "",
                "",
                "",
                "",
                ChatItem.ChatTypes.ONLY_SELF.toString()
            )
            mChatListViewModel.addChatItem(item)
        } else {
        }
        listChatEntity.add(
            ChatEntity(
                0,
                "Community",
                "",
                "",
                "",
                "",
                auth.currentUser!!.uid,
                ChatItem.ChatTypes.COMMUNITY_GROUP.toString()
            )
        )

        db.collection(Constants.USERS_COLLECTION)
            .get()
            .addOnSuccessListener {
                if (it.documents.size != 0) {

                    it.documents.forEach { doc ->
                        if (doc.id != phoneNumber) {
                            val myUid = pref.getString(Constants.ACCOUNT_USERID, "")!!
                            val name = doc.get("Name").toString()//.substring(0..5)
                            val uid = doc.get("UserId").toString()//.substring(0..5)
                            val phoneNumbr = doc.id
                            val item = ChatEntity(
                                0,
                                name,
                                phoneNumbr,
                                "",
                                "",
                                uid,
                                myUid,
                                ChatItem.ChatTypes.TWO_PEOPLE.toString()
                            )
                            listChatEntity.add(item)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }


    private fun showChooser() {
        val listOfAccounts = arrayListOf<String>()
        db.collection(Constants.USERS_COLLECTION)
            .get()
            .addOnSuccessListener {
                if (it.documents.size != 0) {

                    it.documents.forEach { doc ->
                        if (doc.id != phoneNumber) {
                            val name = doc.get("Name").toString().substring(0..5)
                            val phoneNumbr = doc.id
                            listOfAccounts.add("${name}...| $phoneNumbr")
                        }
                    }

                    var message = ""
                    if (listOfAccounts.isEmpty()) {
                        message =
                            "No accounts found you are the first user.\nInvite your friends and contacts to chat with them."
                    } else {
                        listOfAccounts.forEach {
                            message += "\n" + it
                        }
                    }

                    AlertDialog.Builder(requireContext())
                        .setTitle("Chat with :")
                        .setMessage(message)
                        .setNeutralButton("OK") { d, _ ->
                            d.dismiss()
                        }
                        .create()
                        .show()
                }
            }

/*
        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setContentView(R.layout.activyt_chat_selector)
        d.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(d.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        d.window!!.attributes = lp
        val radioGroup = d.findViewById<RadioGroup>(R.id.radioGroup)*/

    }

    private fun initRecyclerViewSetup() {
        list = mutableListOf()
        recyclerView = binding.recyclerView
        layoutManager = LinearLayoutManager(requireActivity())
//        adapter = ChatListAdapter(requireContext(), list)
        adapter = ChatListAdapter(requireContext(), listChatEntity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
