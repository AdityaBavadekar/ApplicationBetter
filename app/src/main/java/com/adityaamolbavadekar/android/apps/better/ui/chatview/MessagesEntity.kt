package com.adityaamolbavadekar.android.apps.better.ui.chatview

data class MessagesEntity(
    var from: String,
    var timestamp: String,
    var message: String,
    var isFromUser:Boolean
//    var readMark:Boolean
){
    data class Saver(var myUid:String,var uid :String){
        override fun toString(): String {
            return "${myUid}#${uid}"
        }
    }
}
