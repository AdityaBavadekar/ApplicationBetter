package com.adityaamolbavadekar.android.apps.better.room.chats_list

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class], version = 1, exportSchema = false)
abstract class ChatListDatabase : RoomDatabase() {

    abstract fun dao(): ChatsListDao

    companion object {

        @Volatile
        private var INSTANCE: ChatListDatabase? = null

        fun getDatabase(context: Context): ChatListDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ChatListDatabase::class.java,
                    "chats_list_table"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }


}