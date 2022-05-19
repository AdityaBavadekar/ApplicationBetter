package com.adityaamolbavadekar.android.apps.better.utils

import com.adityaamolbavadekar.android.apps.better.ui.chatview.MessagesEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.hypertrack.hyperlog.HyperLog

object TwoPeopleUtils {

    fun getMessages(
        db: FirebaseFirestore,
        saver: MessagesEntity.Saver
    ): MutableList<MessageObject> {
        val docRef = saver.toString()
        val ref = db.collection("chats/${docRef}/messages")
        val task = ref.get()

        return if (task.isSuccessful && !task.result.isEmpty) {
            getData(task.result)

        } else {
            HyperLog.e(this.javaClass.name.toString(), task.exception.toString())
            mutableListOf()
        }
    }

    private lateinit var messages: MutableList<MessageObject>
    private fun getData(it: QuerySnapshot): MutableList<MessageObject> {
        val list = mutableListOf<MessageObject>()
        if (!it.isEmpty) {
            for (doc in it.documents) {
                val message = doc.toObject(MessageObject::class.java)
                if (message != null) {
                    list.add(message)
                }
            }
            messages.addAll(list)
            return messages
        }
        return messages
    }

}