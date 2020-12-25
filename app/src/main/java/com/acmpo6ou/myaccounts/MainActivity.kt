/*
 * Copyright (c) 2020. Kolvakh Bohdan
 * This file is part of MyAccounts.
 *
 * MyAccounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyAccounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.acmpo6ou.myaccounts

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.acmpo6ou.myaccounts.core.MainPresenterInter
import com.acmpo6ou.myaccounts.core.errorDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_database_list.*


class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener, MainActivityInter {

    val IMPORT_RC = 202
    override lateinit var ACCOUNTS_DIR: String
    override lateinit var myContext: Context
    override lateinit var app: MyApp

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var presenter: MainPresenterInter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ACCOUNTS_DIR = getExternalFilesDir(null)!!.path + "/"
        myContext = this
        app = applicationContext as MyApp

        // setup presenter and appbar
        presenter = MainPresenter(this)
        setSupportActionBar(toolbar)

        // setup navigation controller
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // setup navigation view
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)

        // unlock drawer layout when we navigate back from AboutFragment, SettingsFragment etc
        navController.addOnDestinationChangedListener{ _: NavController, navDestination: NavDestination, _: Bundle? ->
            val destinations = listOf(
                    R.id.settingsFragment,
                    R.id.changelogFragment,
                    R.id.aboutFragment,
            )
            if(navDestination.id !in destinations){
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }

        // check permissions
        checkPermissions()
    }

    private fun checkPermissions() {
        val isGranted = checkCallingOrSelfPermission(permission.WRITE_EXTERNAL_STORAGE)
        if(isGranted != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE), 300)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.import_database -> presenter.importSelected()
            R.id.check_for_updates -> presenter.checkUpdatesSelected()
            R.id.changelog -> presenter.navigateToChangelog()
            R.id.settings -> presenter.navigateToSettings()
            R.id.about -> presenter.navigateToAbout()
        }

        // close drawer when any item is selected
        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onBackPressed() {
        // close navigation drawer when Back button is pressed and if it is opened
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    /**
     * Displays snackbar to tell user that there are no updates available.
     */
    override fun noUpdates(){
        Snackbar.make(
                databaseCoordinator,
                R.string.no_updates,
                Snackbar.LENGTH_LONG
        )
            .setAction("HIDE"){}
            .show()
    }

    override fun showError(title: String, details: String) {
        errorDialog(myContext, title, details)
    }

    /**
     * Navigates to given destination.
     *
     * @param[id] id of destination action.
     */
    override fun navigateTo(id: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.navigate(id)

        // drawer should be locked because we can't navigate from AboutFragment,
        // SettingsFragment etc. to anywere
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun startUpdatesActivity() {

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?){
        super.onActivityResult(requestCode, resultCode, resultData)
        // if activity was canceled don't do anything
        if (resultCode != Activity.RESULT_OK){
            return
        }

        if(requestCode == IMPORT_RC) {
            val locationUri = resultData?.data!!
            presenter.checkTarFile(locationUri)
        }
    }

    /**
     * Used to display import dialog where user can chose database that he wants to import.
     *
     * Starts intent with import request code. Shows dialog to chose location using Storage
     * Access framework.
     */
    override fun importDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/x-tar"
        }
        startActivityForResult(intent, IMPORT_RC)
    }

    /**
     * This method will automatically hide the keyboard when any TextView is losing focus.
     *
     * Note: this method is completely copied from StackOverflow.
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            /**
             * It gets into the above IF-BLOCK if anywhere the screen is touched.
             */
            val v: View? = currentFocus
            if (v is EditText) {
                /**
                 * Now, it gets into the above IF-BLOCK if an EditText is already in focus, and you tap somewhere else
                 * to take the focus away from that particular EditText. It could have 2 cases after tapping:
                 * 1. No EditText has focus
                 * 2. Focus is just shifted to the other EditText
                 */
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}