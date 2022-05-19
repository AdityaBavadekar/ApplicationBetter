package com.adityaamolbavadekar.android.apps.better.utils

data class MessageObject(
    var from: String = "",//SENDER UID
    var id: String = "",//DOCUMENT ID
    var message: String = "",
    var timestamp: String = "",//System.currentTimeInMillis()
)
