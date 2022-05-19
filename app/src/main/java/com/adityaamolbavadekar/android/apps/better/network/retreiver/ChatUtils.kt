package com.adityaamolbavadekar.android.apps.better.network.retreiver

import android.content.Context
import android.os.AsyncTask
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.room.chats_list.ChatEntity
import com.adityaamolbavadekar.android.apps.better.ui.chatview.MessagesEntity
import com.adityaamolbavadekar.android.apps.better.ui.main.ChatItem
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object ChatUtils {

    fun getConversationCollectionRef(it: ChatEntity): String {
        val docId = it.myUserId + "#" + it.chatWithUserId
        return "chats/${docId}/Messages"
    }

    class GetChats(private val callback: GetChatsCallback) {
        fun execute(collectionRef: String, db: FirebaseFirestore, context: Context) {
            Get(collectionRef, db, context, callback).execute()
        }

        private class Get(
            private val collectionRef: String,
            private val db: FirebaseFirestore,
            private val context: Context,
            private val callback: GetChatsCallback
        ) : AsyncTask<Void, Void, Void>() {
            private val list = mutableListOf<ChatEntity>()
            private var e: Exception? = null
            override fun doInBackground(vararg params: Void?): Void? {

                val myUid = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(Constants.ACCOUNT_USERID, "")!!

                db.collection(Constants.USERS_COLLECTION)
                    .get()
                    .addOnSuccessListener {

                        if (it.size() != 0) {

                            it.documents.forEach { doc: DocumentSnapshot ->
                                val name = doc.get("Name").toString()//.substring(0..5)
                                val uid = doc.get("UserId").toString()//.substring(0..5)
                                val phoneNumbr = doc.id
                                val item = ChatEntity(
                                    0,
                                    name,
                                    phoneNumbr,
                                    "",
                                    "",
                                    uid,
                                    myUid,
                                    ChatItem.ChatTypes.ONLY_SELF.toString()
                                )
                                list.add(item)
                            }
                        }

                    }
                    .addOnFailureListener {
                        e = it
                    }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                if (e != null) {
                    callback.onSuccess(list)
                } else {
                    callback.onException(e!!)
                }
            }
        }
    }

    class GetMessages(private val callback: GetMessagesCallback) {

        fun execute(collectionRef: String, db: FirebaseFirestore, context: Context) {
            Get(collectionRef, db, context, callback).execute()
        }

        private class Get(
            private val collectionRef: String,
            private val db: FirebaseFirestore,
            private val context: Context,
            private val callback: GetMessagesCallback
        ) : AsyncTask<Void, Void, String>() {
            private lateinit var list: MutableList<MessagesEntity>
            private var e: Exception? = null
            override fun doInBackground(vararg params: Void?): String {

                list = mutableListOf()
                val myUid = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(Constants.ACCOUNT_USERID, "")!!
                db.collection(collectionRef)
                    .get()
                    .addOnCompleteListener {task->
                        val it = task.result
                        if (!it.isEmpty) {
                            it.documents.forEach { docSnap: DocumentSnapshot ->
                                val id = docSnap.getString("id")?: ""
                                val from = docSnap.getString("from")?:""
                                val message = docSnap.getString("message")?:""
                                val timestamp = docSnap.getString("timestamp")?:""
                                val isFromUser = from == myUid
                                val item = MessagesEntity(from, timestamp, message, isFromUser)
                                list.add(item)
                            }
                        }
                    }
                    .addOnFailureListener {
                        e = it
                    }

                return ""
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (e != null) {
                    callback.onSuccess(list)
                } else {
                    callback.onException(e!!)
                }
            }

        }
    }


    interface GetChatsCallback {
        fun onSuccess(mutableList: MutableList<ChatEntity>)
        fun onException(e: Exception)
    }

    interface GetMessagesCallback {
        fun onSuccess(mutableList: MutableList<MessagesEntity>)
        fun onException(e: Exception)
    }

    fun save(
        db: FirebaseFirestore,
        messagesEntity: MessagesEntity,
        saver: MessagesEntity.Saver
    ): Boolean {
        val docId = saver.myUid + "#" + saver.uid
        val docCollectionPath = "chats/${docId}/Messages"
        val docRef = db.collection(docCollectionPath).document()

        val savedFirebaseMessage = hashMapOf(
            "id" to docRef.id,
            "from" to saver.myUid,
            "message" to messagesEntity.message,
            "timestamp" to messagesEntity.timestamp,
        )
//        val s = docId.startsWith("myUid",0,false)
        val networkActive = true//DoesNetworkHaveInternet.execute()
        return if (networkActive) {
            docRef.set(savedFirebaseMessage)
            true
        } else {
            false
        }
        //PATH chat[collection]/ [document]`myUid+"#"+uid`/Messages[collection]
        //chat/docId/Messages
    }

    data class SavedFirebaseMessage(
        var primaryKey: Int,
        var from: String,
        var message: Int,
        var timestamp: String
    )

}