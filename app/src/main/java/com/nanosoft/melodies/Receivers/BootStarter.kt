package com.nanosoft.melodies.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nanosoft.melodies.Services.NotificationListenerService
import com.nanosoft.melodies.Utils.Constants
import com.nanosoft.melodies.Utils.SharedPref


class BootStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val sharedPref = SharedPref(context)
        if(sharedPref.LoadBoolean(Constants.ENABLE_RINGER, false) == true){
            val notificationService = Intent(context, NotificationListenerService::class.java)
            context.startService(notificationService)
        }
    }
}