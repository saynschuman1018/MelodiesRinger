package com.nanosoft.melodies.Services

import android.app.Notification
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.support.annotation.RequiresApi
import android.util.Log
import com.nanosoft.melodies.Database.DBHelper
import com.nanosoft.melodies.Models.MusicFile
import com.nanosoft.melodies.Utils.Constants
import com.nanosoft.melodies.Utils.SharedPref
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationListenerService : NotificationListenerService() {
    private val TAG = this.javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification) {

        dbHelper = DBHelper.getInstance(applicationContext)

        Log.i(TAG, sbn.getNotification().extras.getString(Notification.EXTRA_TEXT))
        val extras: Bundle = sbn.getNotification().extras

        if ("Ongoing call" == extras.getString(Notification.EXTRA_TEXT)) {
            Log.d(TAG, "Ongoing Call")

            if(isPlaying)
                stopRingBack(true)

        } else if ("Dialling" == extras.getString(Notification.EXTRA_TEXT) || extras.getString(Notification.EXTRA_TEXT).contains("Набор номера")) {
            Log.d(TAG, "Dialing")

            val sharedPref = SharedPref(applicationContext)
            if(sharedPref.LoadBoolean(Constants.ENABLE_RINGER, false) == true){
                mContext = applicationContext
                initStreamVolumn()
                playRingBack()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "********** onNotificationRemoved")

        if(isPlaying)
            stopRingBack(true)

//        Log.i(TAG, "ID :" + sbn.getId().toString() + "\t" + sbn.getNotification().tickerText.toString() + "\t" + sbn.getPackageName())
    }

    fun playRingBack(){

        stopRingBack(false)

        if(selectedSongs == null)
            selectedSongs = dbHelper?.GetSongAsRingBack()

        if(selectedSongs!!.size == 0)
            return

        Log.d(TAG, "Size : " + selectedSongs!!.size)
        Log.d(TAG, "Current Index : " + currentRingBackIndex)

        currentRingBackIndex = (currentRingBackIndex + 1) % selectedSongs!!.size

        try {
            val currentPlaySong = selectedSongs?.get(currentRingBackIndex)

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setDataSource(currentPlaySong?.musicPath)
            mediaPlayer?.prepare()
            Log.d(TAG, "Start time " + currentPlaySong?.startTime!! )
            mediaPlayer?.seekTo(currentPlaySong?.startTime!! * 1000)
            mediaPlayer?.start()

            val handler = Handler()
            val delay = currentPlaySong!!.endTime!!.minus(currentPlaySong!!.startTime!!)
            handler.postDelayed(Runnable { if (isPlaying) playRingBack() }, (delay * 1000).toLong())
            isPlaying = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRingBack(finalStop: Boolean){
        if(mediaPlayer != null){
            isPlaying = false
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        if(finalStop){
            currentRingBackIndex = -1
            selectedSongs = null
            restoreStreamVolumn()
        }
    }


    fun initStreamVolumn(){
        val am = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mOldVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        am.setStreamVolume(
            AudioManager.STREAM_SYSTEM,
            0,
            0)
    }

    fun restoreStreamVolumn(){
        val am = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        am.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            mOldVolumn,
            0)
    }


    companion object {
        var TAG = "NotificationCenter"
        var isPlaying = false
        var mediaPlayer : MediaPlayer? = null
        var dbHelper : DBHelper? = null
        var currentRingBackIndex = -1
        var selectedSongs : ArrayList<MusicFile>? = null
        var mContext : Context? = null
        var mOldVolumn : Int = 0
    }
}