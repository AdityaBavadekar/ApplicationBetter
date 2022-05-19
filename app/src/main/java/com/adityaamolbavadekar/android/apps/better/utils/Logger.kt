package com.adityaamolbavadekar.android.apps.better.utils

import java.text.SimpleDateFormat
import java.util.*

class Logger {

    private var mLOGS: MutableList<BetterLog> = mutableListOf()

    data class BetterLog(var msg: String, var type: String, var stamp: String) {
        override fun toString(): String {
            return "!_${msg}_!_${stamp}_${type}_"
        }
    }

    init {
    }

    companion object {
        const val VERBOSE = "VERBOSE"
        const val DEBUG = "DEBUG"
        const val INFO = "INFO"
        const val WARN = "WARN"
        const val ERROR = "ERROR"
    }

    fun i(msg: String) {
        val stamp = SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val l = BetterLog(msg, INFO, stamp)
        mLOGS.add(l)
    }

    fun d(msg: String) {
        val stamp = SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val l = BetterLog(msg, DEBUG, stamp)
        mLOGS.add(l)
    }

    fun v(msg: String) {
        val stamp = SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val l = BetterLog(msg, VERBOSE, stamp)
        mLOGS.add(l)
    }

    fun w(msg: String) {
        val stamp = SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val l = BetterLog(msg, WARN, stamp)
        mLOGS.add(l)
    }

    fun e(msg: String) {
        val stamp = SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val l = BetterLog(msg, ERROR, stamp)
        mLOGS.add(l)
    }

    fun get(): String {
        return mLOGS.toString()
    }

}