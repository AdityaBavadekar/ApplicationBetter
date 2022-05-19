package com.adityaamolbavadekar.android.apps.better.utils

data class CommunityMessageObject(
    var fromName: String = "",//SENDER Name
    var fromPhoneNumber: String = "",//SENDER PhoneNumber
    var from: String = "",//SENDER UID
    var id: String = "",//DOCUMENT ID
    var message: String = "",
    var timestamp: String = "",//System.currentTimeInMillis()
)
