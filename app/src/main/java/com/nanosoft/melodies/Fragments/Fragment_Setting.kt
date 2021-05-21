package com.nanosoft.melodies.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.nanosoft.melodies.Models.IntroItem
import com.nanosoft.melodies.R
import com.nanosoft.melodies.Utils.Constants
import com.nanosoft.melodies.Utils.SharedPref
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class Fragment_Setting : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view :View = inflater!!.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences = SharedPref(activity)
        val controlRinger = activity.findViewById<Switch>(R.id.controlRinger)

        view.controlRinger.isChecked = sharedPreferences.LoadBoolean(Constants.ENABLE_RINGER, false)!!

        view.controlRinger.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                sharedPreferences.SaveBoolean(Constants.ENABLE_RINGER, true)
            } else {
                sharedPreferences.SaveBoolean(Constants.ENABLE_RINGER, false)
            }
        }

        return view;
    }
}