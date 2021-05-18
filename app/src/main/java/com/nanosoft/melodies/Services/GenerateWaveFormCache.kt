/*
 * *
 *  * Created by Youssef Assad on 6/2/18 11:17 AM
 *  * Copyright (c) 2018 . All rights reserved.
 *  * Last modified 6/2/18 11:04 AM
 *
 */

package com.nanosoft.melodies.Services


import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.nanosoft.melodies.Database.DBHelper
import com.nanosoft.melodies.R
import com.nanosoft.melodies.SoundEditor.CheapSoundFile
import com.nanosoft.melodies.UILapplication
import com.nanosoft.melodies.Utils.MediaStoreFetcher
import java.io.File

class GenerateWaveFormCache : IntentService("GenerateWaveFormCache") {
    override fun onHandleIntent(intent: Intent?) {
        MediaStoreFetcher(this)
        val dbHelper = DBHelper.getInstance(this)
        var mItems = dbHelper.musicFromDB
        for (i in mItems.indices) {
            loadfile(mItems[i].musicPath)
        }

        Log.e("GenerateWaveFormCache","started")
    }


    fun loadfile(FilePath: String?) {



        val WaveFormFile = File(UILapplication.instance.cacheDir, FilePath?.hashCode().toString() )

        val mMusicFile = File(FilePath!!)
//        val externalRootDir = UILapplication.getMusicCache() + FilePath.hashCode()
//        val file = File(externalRootDir)
        if (WaveFormFile.exists() || WaveFormFile.length() > 0) {
//            Log.e("WaveFormFile generate" , " Found")
            return
        } else {
            try {
                val mSoundFile = CheapSoundFile.create(mMusicFile.absolutePath, null)
                if (mSoundFile != null) {
                    mSoundFile.computeDoublesForAllZoomLevels()
                    mSoundFile.SaveCache(FilePath)
//                    Log.e("WaveFormFile generate" , " not Found")
                }
            } catch (e: Exception) {
                Log.e(GenerateWaveFormCache.Companion.TAG, getString(R.string.e_error_loading) + e)
                return
            }

        }


    }

    companion object {
        private val TAG = "GenerateWaveFormCache"
    }


}
