/*
 * *
 *  * Created by Youssef Assad on 6/2/18 11:46 AM
 *  * Copyright (c) 2018 . All rights reserved.
 *  * Last modified 6/2/18 11:23 AM
 *
 */

package com.nanosoft.melodies.Activties

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.nanosoft.melodies.R
import kotlinx.android.synthetic.main.activity_about.*


class Activity_About : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean =  when (menuItem.itemId) {
        android.R.id.home -> {  finish()
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }



}
