package com.adityaamolbavadekar.android.apps.better

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.hypertrack.hyperlog.HyperLog
import com.parse.Parse
import org.acra.BuildConfig
import org.acra.ReportField
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        HyperLog.initialize(this)
        HyperLog.setLogLevel(Log.INFO)
        HyperLog.i(this.javaClass.name.toString(),"HyperLogInitialised")

        Parse.initialize(
            Parse.Configuration.Builder(this)
            .applicationId(getString(R.string.back4app_app_id))
            .clientKey(getString(R.string.back4app_client_key))
            .server(getString(R.string.back4app_server_url))
            .build()
        );
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        initAcra {
            this.reportFormat= StringFormat.KEY_VALUE_LIST
            this.buildConfigClass = BuildConfig::class.java
            this.alsoReportToAndroidFramework = true
            this.setReportField(ReportField.APPLICATION_LOG,true)
            this.


            mailSender {
                this.mailTo = "adityarbavadekar@gmail.com"
                this.body = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH).format(Date())
                this.subject = "Better Data"
                this.reportFileName = "BetterCrash.txt"
                this.enabled = true
                this.build()
            }

            toast {
                this.text = "Sorry crash occurred"
                this.length = Toast.LENGTH_SHORT
                this.enabled =true
            }
        }
    }

    companion object{
        val TAG = this.toString()
    }

}