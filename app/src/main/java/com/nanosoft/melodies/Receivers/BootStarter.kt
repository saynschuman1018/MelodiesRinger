package com.nanosoft.melodies.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager


class BootStarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.registerReceiver(PhoneStateReceiver(), IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
    }
}