package com.nanosoft.melodies.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import com.google.android.gms.internal.mp
import com.nanosoft.melodies.Database.DBHelper
import com.nanosoft.melodies.Models.MusicFile
import java.io.IOException


class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        dbHelper = DBHelper.getInstance(context)

        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            Log.d(TAG, "PhoneStateReceiver**Call State=$state")
            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                Log.d(TAG, "PhoneStateReceiver**Idle")

                if(isPlaying)
                    stopRingBack(true)

            } else if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                // Incoming call
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d(TAG, "PhoneStateReceiver**Incoming call $incomingNumber")
                if (!killCall(context)) { // Using the method defined earlier
                    Log.d(TAG, "PhoneStateReceiver **Unable to kill incoming call")
                }
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                Log.d(TAG, "PhoneStateReceiver **OffHook")

                playRingBack()
            }
        } else if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            // Outgoing call
            val outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.d(TAG, "PhoneStateReceiver **Outgoing call $outgoingNumber")

            if(isPlaying)
                stopRingBack(true)
//            setResultData(null); // Kills the outgoing call
        } else {
            Log.d(TAG, "PhoneStateReceiver **unexpected intent.action=" + intent.action)
        }
    }

    fun playRingBack(){

        stopRingBack(false)

        if(selectedSongs == null)
            selectedSongs = dbHelper?.GetSongAsRingBack()

        Log.d(TAG, "Size : " + selectedSongs!!.size)
        Log.d(TAG, "Current Index : " + currentRingBackIndex)

        currentRingBackIndex = (currentRingBackIndex + 1) % selectedSongs!!.size

        try {
            val currentPlaySong = selectedSongs?.get(currentRingBackIndex)

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setDataSource(currentPlaySong?.musicPath)
            mediaPlayer?.prepare()
            mediaPlayer?.seekTo(currentPlaySong?.startTime!!)
            mediaPlayer?.start()

            val handler = Handler()
            val delay = currentPlaySong!!.endTime!!.minus(currentPlaySong!!.startTime!!)
            handler.postDelayed(Runnable { if(isPlaying) playRingBack() }, ( delay * 1000).toLong())
            isPlaying = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRingBack(finalStop : Boolean){
        if(mediaPlayer != null){
            isPlaying = false
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        if(finalStop){
            currentRingBackIndex = -1
            selectedSongs = null
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
        var isPlaying = false
        var mediaPlayer : MediaPlayer? = null
        var dbHelper : DBHelper? = null
        var currentRingBackIndex = -1
        var selectedSongs : ArrayList<MusicFile>? = null

    }
}