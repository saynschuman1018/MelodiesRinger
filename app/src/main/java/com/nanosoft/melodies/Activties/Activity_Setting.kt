package com.nanosoft.melodies.Activties

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.nanosoft.melodies.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class Activity_Setting : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

}