package com.nanosoft.melodies.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.telephony.TelephonyManager
import android.util.Log
import java.io.IOException
import java.lang.Exception

class PhoneStateReceiver : BroadcastReceiver() {
    var isCalling = false
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            Log.d(TAG, "PhoneStateReceiver**Call State=$state")
            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                Log.d(TAG, "PhoneStateReceiver**Idle")
            } else if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                // Incoming call
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d(TAG, "PhoneStateReceiver**Incoming call $incomingNumber")
                if (!killCall(context)) { // Using the method defined earlier
                    Log.d(TAG, "PhoneStateReceiver **Unable to kill incoming call")
                }
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                Log.d(TAG, "PhoneStateReceiver **OffHook")
                val mp = MediaPlayer()
                try {
                    mp.setDataSource("/sdcard/MelodiesRinger/Baarishein-Atif-Aslam.mp3")
                    mp.prepare()
                    mp.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            // Outgoing call
            val outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.d(TAG, "PhoneStateReceiver **Outgoing call $outgoingNumber")

//            setResultData(null); // Kills the outgoing call
        } else {
            Log.d(TAG, "PhoneStateReceiver **unexpected intent.action=" + intent.action)
        }
    }

    fun killCall(context: Context): Boolean {
        try {
            // Get the boring old TelephonyManager
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            // Get the getITelephony() method
            val classTelephony = Class.forName(telephonyManager.javaClass.name)
            val methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony")

            // Ignore that the method is supposed to be private
            methodGetITelephony.isAccessible = true

            // Invoke getITelephony() to get the ITelephony interface
            val telephonyInterface = methodGetITelephony.invoke(telephonyManager)

            // Get the endCall method from ITelephony
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.name)
            val methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall")

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface)
        } catch (ex: Exception) { // Many things can go wrong with reflection calls
            Log.d(TAG, "PhoneStateReceiver **$ex")
            return false
        }
        return true
    }

    companion object {
        var TAG = "PhoneStateReceiver"
    }
}