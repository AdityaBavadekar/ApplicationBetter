package com.adityaamolbavadekar.android.apps.better.room.dynamic

import androidx.room.PrimaryKey
import com.adityaamolbavadekar.android.apps.better.room.chats_list.ChatEntity
import com.adityaamolbavadekar.android.apps.better.ui.main.ChatItem

data class BaseEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var messagesCount:Int,
    var messagesList:List<ChatEntity>,
    var chatTypes: String
)