package com.adityaamolbavadekar.android.apps.better.room.chats_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListViewModel(application: Application) : AndroidViewModel(application) {
//    private val getList: LiveData<List<ChatEntity>>
    fun getList(): LiveData<List<ChatEntity>>{
        return repo.getList()
    }
    private val repo: ChatListRepository

    init {
        val chatsListDao = ChatListDatabase.getDatabase(application).dao()
        repo = ChatListRepository(chatsListDao)
//        getList = repo.getList()
    }

    fun addChatItem(chatEntity: ChatEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addChatItem(chatEntity)
        }
    }

}
