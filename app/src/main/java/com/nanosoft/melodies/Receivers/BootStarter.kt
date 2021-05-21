package com.nanosoft.melodies.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import com.nanosoft.melodies.Utils.Constants
import com.nanosoft.melodies.Utils.SharedPref


class BootStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val sharedPref = SharedPref(context)
        if(sharedPref.LoadBoolean(Constants.ENABLE_RINGER, false) == true)
            context.registerReceiver(PhoneStateReceiver(), IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
    }
}