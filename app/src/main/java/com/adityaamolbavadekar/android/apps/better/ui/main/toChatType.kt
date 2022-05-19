package com.adityaamolbavadekar.android.apps.better.ui.main

fun String.toChatType(): ChatItem.ChatTypes {
    return ChatItem.ChatTypes.valueOf(this)
}