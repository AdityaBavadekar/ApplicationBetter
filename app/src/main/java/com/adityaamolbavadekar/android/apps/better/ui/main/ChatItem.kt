package com.adityaamolbavadekar.android.apps.better.ui.main

data class ChatItem(
    var toName: String?,
    var toPhoneNumber: String,
    var lastMessage: String,
    var messagesDocPath: String,
    var messagesDocName: String,
    var toProfileDocPath: String,
    var toProfileDocLink: String?,
    var lastMessageOn: String,
    var lastMessageOnMonth: String,
    var lastMessageOnDate: String,
    val chatType: ChatTypes
) {

    enum class ChatTypes {
        TWO_PEOPLE, COMMUNITY_GROUP, ONLY_SELF;

        override fun toString(): String {
            return super.name
        }

    }


}
