package com.adityaamolbavadekar.android.apps.better.ui.main

data class BetterAccount(
    var docIdName: String,
    var phoneNumber: String
)

class AccountStamps() {
    val creationDate = "CreationDate"
    val userId = "UserId"
    val sysTime = "SystemTime"
    val phone = "Phone"
    val name = "Name"
    val about = "About"
}
