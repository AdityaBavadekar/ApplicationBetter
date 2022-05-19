package com.adityaamolbavadekar.android.apps.better

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.adityaamolbavadekar.android.apps.better.ui.SaveAsDialogActivity
import com.adityaamolbavadekar.android.apps.better.ui.splash.SplashActivity

object Shortcuts {

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun setUp(context: Context) {
        val sm = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

        val intents = arrayOf(
            Intent(Intent.ACTION_VIEW, null, context, SplashActivity::class.java),
            Intent(Intent.ACTION_VIEW, null, context, SaveAsDialogActivity::class.java)
        )
        val shortcutSaveAs = ShortcutInfo.Builder(context, shortcut_saveas_id)
            .setShortLabel("Quick notes save")
            .setLongLabel("Save or create a new note")
            .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_save))
            .setIntents(intents)
            .build()

        sm.dynamicShortcuts = listOf(shortcutSaveAs)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createShortcutPins(context: Context){
        val sm = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        if (sm.isRequestPinShortcutSupported){
            val pinSaveAs = ShortcutInfo.Builder(context, shortcut_saveas_id).build()
            val callbackIntent = sm.createShortcutResultIntent(pinSaveAs)
            val successIntent = PendingIntent.getBroadcast(context,0,callbackIntent,0)
            sm.requestPinShortcut(pinSaveAs,successIntent.intentSender)
        }

    }


    const val shortcut_settings_id = "settings"
    const val shortcut_saveas_id = "saveas"

}