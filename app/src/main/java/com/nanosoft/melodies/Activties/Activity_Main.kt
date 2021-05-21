/*
 * *
 *  * Created by Youssef Assad on 6/2/18 11:17 AM
 *  * Copyright (c) 2018 . All rights reserved.
 *  * Last modified 6/2/18 11:04 AM
 *
 */

package com.nanosoft.melodies.Activties

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nanosoft.melodies.Adapters.Adapter_Menu
import com.nanosoft.melodies.Fragments.Fragment_Selection
import com.nanosoft.melodies.Fragments.Fragment_Setting
import com.nanosoft.melodies.R
import com.nanosoft.melodies.Utils.Constants
import com.nanosoft.melodies.Utils.SharedPref
import kotlinx.android.synthetic.main.activity_main.*


class Activity_Main : AppCompatActivity(), Adapter_Menu.ListenerOnMenuItemClick, DrawerLayout.DrawerListener, SearchView.OnQueryTextListener {


    private val REQUEST_INVITE = 0

    private var AnimateCounter = 1
    private var mSearchView: SearchView? = null
    var fragment: Fragment_Selection? = null
    private var doubleBackToExitPressedOnce = false
    private var mSharedPref: SharedPref? = null
    private var mAuth: FirebaseAuth? = null

    private var MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.app_decorview_color))
        setContentView(R.layout.activity_main)

        requestAppPermissions()

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_drawer_recycler_view.layoutManager = LinearLayoutManager(this)
        val adapter_menu = Adapter_Menu(this, this)
        nav_drawer_recycler_view.adapter = adapter_menu
        drawer_layout.addDrawerListener(this)



        if (Build.VERSION.SDK_INT < 21) {
            val drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_1)
            nav_drawer_recycler_view.background = drawable
        }

        Handler().postDelayed({
            com.nanosoft.melodies.Utils.MediaStoreFetcher(this)
            ChangeFragment(null)
        }, 200);



        mSharedPref = SharedPref(this)
        mSharedPref?.SaveBoolean(Constants.FIRST_TIME, false)


        RunBackGroundServices()
    }


    fun requestAppPermissions() {

        // Phone permissin to replace outgoing call
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Log.e("Permission", "hasWritePermissions" + hasWritePermissions())
        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }
    }

    private fun hasReadPermissions():Boolean {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    fun hasWritePermissions():Boolean {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }




    fun RunBackGroundServices(){
        Handler().postDelayed({

            val msgIntent = Intent(this, com.nanosoft.melodies.Services.GenerateWaveFormCache::class.java)
            startService(msgIntent)
        }, 10000)
    }


    private fun StartAnimation() {

        val drawable: AnimatedVectorDrawableCompat?
        when (AnimateCounter) {
            1 -> drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_1)
            2 -> drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_2)
            3 -> drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_3)
            4 -> drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_4)
            5 -> {
                drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_5)
                AnimateCounter = 0
            }
            else -> drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animate_wave_1)
        }
        AnimateCounter++
        WaveContainer.background = drawable
        assert(drawable != null)
        drawable!!.start()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                finish()
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.Exist_Toast), Toast.LENGTH_SHORT).show()

            // reset after 2 secs to false
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun Item(IconRes: Int) {
        drawer_layout.closeDrawer(GravityCompat.START)
        when (IconRes) {
            R.drawable.ic_menu_home -> ChangeFragment(null)
            R.drawable.ic_menu_settings -> ChangeFragment(true)
//            R.drawable.ic_menu_history -> ChangeFragment(true)
//            R.drawable.ic_menu_gopro -> shareTextUrl()
//            R.drawable.ic_menu_sharing -> shareTextUrl()
//            R.drawable.ic_menu_suggestion -> shareTextUrl()
//            R.drawable.ic_menu_about -> startActivity(Intent(this, Activity_About::class.java))
            else -> startActivity(Intent(this, Activity_About::class.java))
        }
    }


    private fun shareTextUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.Google_Play_Link)))
        startActivity(browserIntent)
    }


//    private fun ChangeFragment(History: Boolean?) {
//        fragment = Fragment_Selection.newInstance(History)
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
//        if( History == true ) toolbar.setTitle(this.resources.getString(R.string.menu_history)) else  toolbar.setTitle(this.resources.getString(R.string.menu_home))
//    }

    public fun ChangeFragment(History: Boolean?) {
        if(History == true) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Fragment_Setting()).commit()
        } else
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Fragment_Selection.newInstance(History)).commit()

        toolbar.setTitle(this.resources.getString(R.string.app_name))
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    override fun onDrawerOpened(drawerView: View) = StartAnimation()

    override fun onDrawerClosed(drawerView: View) {}

    override fun onDrawerStateChanged(newState: Int) {}

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false; }

    override fun onQueryTextChange(newText: String?): Boolean {
        fragment?.SearchQuery(newText)
        return false
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.select_options, menu)
        mSearchView = MenuItemCompat.getActionView(menu.findItem(R.id.menu_search)) as SearchView

        // Hide search bar
        menu.findItem(R.id.menu_search).isVisible = false

        mSearchView!!.setIconifiedByDefault(false)
        mSearchView!!.isIconified = false
        mSearchView!!.clearFocus()
        mSearchView!!.setOnQueryTextListener(this)

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                fragment?.RestoreToDefault()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean = true
        })

        return true
    }





    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 0..permissions.size-1){
            val permission = permissions[i]
            val grantResult = grantResults[i]
            Log.e("Permission " + permission, "Value" + grantResult);
        }
    }



}
