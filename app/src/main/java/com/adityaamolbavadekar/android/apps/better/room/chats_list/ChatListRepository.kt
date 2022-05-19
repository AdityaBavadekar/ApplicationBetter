package com.adityaamolbavadekar.android.apps.better.room.chats_list

import androidx.lifecycle.LiveData

class ChatListRepository(private val chatsListDao: ChatsListDao) {


    fun getList(): LiveData<List<ChatEntity>> {
        return chatsListDao.getAllItems()
    }

    fun addChatItem(chatEntity: ChatEntity) {
        chatsListDao.addChatItem(chatEntity = chatEntity)
    }



}