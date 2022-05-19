package com.adityaamolbavadekar.android.apps.better.room.chats_list

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adityaamolbavadekar.android.apps.better.ui.main.ChatItem


@Entity(tableName = "chat_list_table")
data class ChatEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name:String,
    var phoneNumber:String,
    var lastMessage:String,
    var lastMessageTimestamp:String,
    var chatWithUserId:String,
    var myUserId:String,
    var chatTypes: String
)