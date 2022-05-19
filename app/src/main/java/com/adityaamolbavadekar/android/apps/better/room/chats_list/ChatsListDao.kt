package com.adityaamolbavadekar.android.apps.better.room.chats_list

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatsListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChatItem(chatEntity: ChatEntity)

    @Query("SELECT * FROM chat_list_table ORDER BY id ASC")
    fun getAllItems(): LiveData<List<ChatEntity>>

}