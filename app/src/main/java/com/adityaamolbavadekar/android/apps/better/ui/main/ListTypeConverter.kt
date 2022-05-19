package com.adityaamolbavadekar.android.apps.better.ui.main

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ListTypeConverter {

    @TypeConverter
    fun fromString(data: String): ArrayList<String> {
        val type = object : TypeToken<ArrayList<String>>() {}.type

        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun toString(data: ArrayList<String>): String {
        return Gson().toJson(data)
    }
}