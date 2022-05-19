package com.adityaamolbavadekar.android.apps.better.ui.chatview

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.CommunityChatBinding
import com.adityaamolbavadekar.android.apps.better.utils.CommunityMessageObject
import com.adityaamolbavadekar.android.apps.better.utils.CommunityUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hypertrack.hyperlog.HyperLog
import java.util.*

class CommunityChatViewerActivity : AppCompatActivity() {

    private var messages: MutableList<CommunityMessageObject> = mutableListOf()
    private lateinit var adapter: CommunityMessagesAdapter
    private lateinit var binding: CommunityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var myUid: String
    private lateinit var collectionRef: String
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HyperLog.i(this.javaClass.name.toString(), "onCreate")
        binding = CommunityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initializeProperties()
        dialog = BottomSheetDatabase().showAddBottomSheet(this)
        toggleSendButton()
        setUpViewListeners()
        setUpAppearance()
        getMessagesFromServer()
    }

    private fun initializeProperties() {
        auth = Firebase.auth
        myUid = auth.currentUser!!.uid!!
        db = Firebase.firestore
        auth = Firebase.auth
        collectionRef = "chats/community/messages"
    }

    private fun setUpViewListeners() {

        binding.toolbar.setOnClickListener {
            val y = ""
        }
        initRecyclerView()
        binding.editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val t = s.toString()
                    if (!TextUtils.isEmpty(t) && t.trim().isNotEmpty()) {
                        toggleSendButtonOn()
                    } else {
                        toggleSendButton()
                    }
                } else {
                    toggleSendButton()
                }

            }
        })

        binding.btnSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            val d = System.currentTimeMillis()
            val item = MessagesEntity(
                "You", d.toString(), message, true
            )
            saveMessage(item)
            binding.editTextMessage.text = null
        }

        binding.btnAdd.setOnClickListener {
            dialog.show()
        }
    }

    private fun saveMessage(item: MessagesEntity) {
        val result = CommunityUtils.save(db, item, auth.currentUser!!)

        if (result != null) {
            messages.add(result)
            adapter.notifyDataSetChanged()
        } else {
            val msgObject =
                CommunityMessageObject(
                    auth.currentUser!!.displayName.toString(),
                    auth.currentUser!!.phoneNumber.toString(),
                    auth.currentUser!!.uid,
                    "",
                    item.message,
                    item.timestamp
                )
            messages.add(msgObject)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpAppearance() {
        if (intent != null) {
            val id = intent.getStringExtra("item_id") ?: ""
            val chatTitle = intent.getStringExtra("item_title") ?: ""
            supportActionBar?.title = chatTitle
        }
    }

    private fun getMessagesFromServer() {
        val ref = db.collection("community")
        ref.get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    HyperLog.i(this.javaClass.name,"Docs, messages = ${it.size()} , MetaData = ${it.metadata}")
                    for (doc in it.documents) {
                        val from = doc.get("from").toString()
                        val fromName = doc.get("fromName").toString()
                        val fromPhoneNumber = doc.get("fromPhoneNumber").toString()
                        val id = doc.id
                        val msg = doc.get("message").toString()
                        val timestamp = doc.get("timestamp").toString()
                        val message = CommunityMessageObject(fromName, fromPhoneNumber,from,id,msg,timestamp)
//                        val message = doc.toObject(CommunityMessageObject::class.java)
                        messages.add(message)
                        adapter.notifyDataSetChanged()
                    }

                }
                HyperLog.i(this.javaClass.name,"Docs, messages = empty")
            }
            .addOnFailureListener {
                HyperLog.e(this.javaClass.name.toString(), it.toString())
            }

/*        val listener = object : CommunityUtils.OnMessagesListener {
            override fun onSuccess(messagesList: MutableList<CommunityMessageObject>) {
                messagesList.forEach {
                    messages.add(it)
                }
            }

            override fun onError() {
                supportActionBar?.subtitle = "Failed to sync messages, Check network"
            }
        }
        CommunityUtils.getMessages(db, listener)*/
    }

    private fun initRecyclerView() {
        adapter = CommunityMessagesAdapter(this, messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CommunityChatViewerActivity)
            adapter = this@CommunityChatViewerActivity.adapter
        }
    }

    private fun toggleSendButton() {
        binding.btnSend.isEnabled = false
    }

    private fun toggleSendButtonOn() {
        binding.btnSend.isEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    private var count = 1
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_wallpaper -> {
                binding.root.setBackgroundColor(Color.GREEN)
                /*               when (count) {
                                   1 -> {
                                       binding.root.setBackgroundColor(Color.GREEN)
                                       count = 2
                                   }

                                   2 -> {
                                       binding.root.setBackgroundColor(Color.RED)
                                       count = 3
                                   }

                                   3 -> {
                                       binding.root.setBackgroundColor(
                                           ResourcesCompat.getColor(
                                               resources,
                                               R.color.colorTheme,
                                               this.theme
                                           )
                                       )
                                       count = 1
                                   }

                               }*/
            }
        }
        return true
    }

}
