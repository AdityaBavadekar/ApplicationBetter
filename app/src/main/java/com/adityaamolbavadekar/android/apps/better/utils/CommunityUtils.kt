package com.adityaamolbavadekar.android.apps.better.utils

import com.adityaamolbavadekar.android.apps.better.ui.chatview.MessagesEntity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.hypertrack.hyperlog.HyperLog

object CommunityUtils {

    fun getMessages(
        db: FirebaseFirestore,
        listener: OnMessagesListener
    ) {
        val ref = db.collection("chats/community/messages")
        messages = mutableListOf()
        val task = ref.get()
            .addOnSuccessListener {
                listener.onSuccess(getData(it))
            }
            .addOnFailureListener {
                HyperLog.e(this.javaClass.name.toString(), it.toString())
                listener.onError()
            }
    }

    interface OnMessagesListener {
        fun onSuccess(messagesList: MutableList<CommunityMessageObject>)
        fun onError()
    }

    private lateinit var messages: MutableList<CommunityMessageObject>
    private fun getData(it: QuerySnapshot): MutableList<CommunityMessageObject> {
        val list = mutableListOf<CommunityMessageObject>()
        if (!it.isEmpty) {
            for (doc in it.documents) {
                val message = doc.toObject(CommunityMessageObject::class.java)
                if (message != null) {
                    list.add(message)
                }
            }
            messages.addAll(list)
            return messages
        }
        return messages
    }


    fun save(
        db: FirebaseFirestore,
        messagesEntity: MessagesEntity,
        user: FirebaseUser
    ): CommunityMessageObject? {
        val docCollectionPath = "community"
        val docRef = db.collection(docCollectionPath).document()
        val message = CommunityMessageObject(
            user.displayName!!.toString(),
            user.phoneNumber!!.toString(),
            user.uid,
            docRef.id,
            messagesEntity.message,
            messagesEntity.timestamp
        )
        val task = docRef.set(message)

        return if (task.isSuccessful){
            message
        } else{
            null
        }
        //PATH chat[collection]/ [document]`myUid+"#"+uid`/Messages[collection]
        //chat/docId/Messages
    }


}