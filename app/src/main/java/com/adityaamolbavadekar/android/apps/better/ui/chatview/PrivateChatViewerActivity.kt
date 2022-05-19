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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.CommunityChatBinding
import com.adityaamolbavadekar.android.apps.better.network.retreiver.ChatUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hypertrack.hyperlog.HyperLog
import java.util.*

class PrivateChatViewerActivity : AppCompatActivity() {

    private var messages: MutableList<MessagesEntity> = mutableListOf()
    private lateinit var adapter: MessageAdapter
    private lateinit var binding: CommunityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var myUid: String
    private lateinit var uid: String
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
    }

    private fun initializeProperties() {
        auth = Firebase.auth
        myUid = auth.currentUser!!.uid!!
        db = Firebase.firestore
        auth = Firebase.auth
        uid = intent.getStringExtra("item_uid") ?: ""
        collectionRef = "chats/" + myUid + "#" + uid + "/Messages"
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
//            SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME_CHAT_MSG
            val item = MessagesEntity(
                "You", d.toString(), message, true
            )
            messages.add(item)
            adapter.notifyDataSetChanged()
            saveMessage(item)
            binding.editTextMessage.text = null
        }

        binding.btnAdd.setOnClickListener {
            dialog.show()
        }
    }

    private fun saveMessage(item: MessagesEntity) {
        val s = MessagesEntity.Saver(myUid, uid)
        ChatUtils.save(db, item, s)
    }

    private fun setUpAppearance() {
        if (intent != null) {
            val id = intent.getStringExtra("item_id") ?: ""
            val chatTitle = intent.getStringExtra("item_title") ?: ""
            val isPersonalSpace = intent.getBooleanExtra("item_is_user_channel", false)
            if (!isPersonalSpace) {
                supportActionBar?.title = chatTitle

//                if (DoesNetworkHaveInternet.execute()) {
                getMessagesFromServer()
//                } else {
//                    supportActionBar?.subtitle = "Failed to sync messages, Check network"
//                }
            } else {
                supportActionBar?.title = getString(R.string.personal_notes)
            }
        }
    }

    private fun getMessagesFromServer() {
        ChatUtils.GetMessages(object : ChatUtils.GetMessagesCallback {
            override fun onSuccess(mutableList: MutableList<MessagesEntity>) {
                mutableList.forEach {
                    messages.add(it)
                }
            }

            override fun onException(e: Exception) {
                supportActionBar?.subtitle = "Failed to sync messages, Check network"
            }
        }).execute(collectionRef, db, this)
    }

    private fun initRecyclerView() {
        adapter = MessageAdapter(this, messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PrivateChatViewerActivity)
            adapter = this@PrivateChatViewerActivity.adapter
        }
    }

    private fun toggleSendButton() {
        binding.btnSend.isEnabled = false
    }

    private fun toggleSendButtonOn() {
        binding.btnSend.isEnabled = true
    }

    companion object {
        const val TAG = "ChatViewerActivity"
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
