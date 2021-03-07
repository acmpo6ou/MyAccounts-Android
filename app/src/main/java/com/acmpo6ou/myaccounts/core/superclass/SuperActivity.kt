/*
 * Copyright (c) 2020-2021. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.core.superclass

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
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
import androidx.viewbinding.ViewBinding
import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.getProperty
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

/**
 * Super class for MainActivity and AccountsActivity.
 */
abstract class SuperActivity : AppCompatActivity(), SuperActivityInter {
    override lateinit var ACCOUNTS_DIR: String
    override lateinit var myContext: Context
    override lateinit var app: MyApp
    override val activity get() = myContext as Activity

    abstract val b: ViewBinding
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    abstract val confirmGoingBackMsg: Int
    abstract val presenter: SuperPresenterInter

    abstract val mainFragmentId: Int
    override val mainFragment: ListFragment get(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0) as ListFragment
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onStart() {
        super.onStart()
        navView = getProperty(b, "navView")
        drawerLayout = getProperty(b, "drawerLayout")
        setAppVersion()

        // setup navigation controller
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // setup navigation view
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        // navigation drawer should be unlocked only on mainFragment and locked
        // everywhere else
        navController.addOnDestinationChangedListener{
            _: NavController, navDestination: NavDestination, _: Bundle? ->
            if(navDestination.id == mainFragmentId){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            else{
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    /**
     * Displays snackbar to tell user that there are no updates available.
     */
    override fun noUpdates(){
        Snackbar.make(mainFragment.b.coordinatorLayout,
                R.string.no_updates,
                Snackbar.LENGTH_LONG)
                .setAction("HIDE"){}
                .show()
    }

    override fun updatesCheckFailed() {
    }

    /**
     * This method obtains version name and sets it in navigation header.
     */
    private fun setAppVersion() {
        val version = BuildConfig.VERSION_NAME
        val header = navView.getHeaderView(0)
        val versionString = header.findViewById<TextView>(R.id.versionString)
        versionString.text = version
    }

    /**
     * Displays confirmation dialog asking user to confirm does he really wan't to go back
     * with unsaved changes.
     */
    override fun confirmBack(){
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.go_back_title)
                .setMessage(confirmGoingBackMsg)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    super.onBackPressed()
                }
                .setNegativeButton(R.string.save) { _: DialogInterface, _: Int ->
                    presenter.saveSelected()
                    super.onBackPressed()
                }
                .setNeutralButton(R.string.cancel) { _: DialogInterface, _: Int -> }
                .show()
    }

    override fun onBackPressed() {
        // close navigation drawer when Back button is pressed and if it is opened
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else if (navController.currentDestination?.id == mainFragmentId){
            presenter.backPressed()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun goBack() = super.onBackPressed()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.check_for_updates -> presenter.checkUpdatesSelected()
            R.id.changelog -> presenter.navigateToChangelog()
            R.id.settings -> presenter.navigateToSettings()
            R.id.about -> presenter.navigateToAbout()
        }
        return false
    }

    /**
     * Navigates to given destination.
     * @param[id] id of destination action.
     */
    override fun navigateTo(id: Int) {
        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.navigate(id)
    }

    override fun startUpdatesActivity() {
    }

    /**
     * This method will automatically hide the keyboard when any TextView is losing focus.
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

    /**
     * Used to display dialog saying that the error has occurred.
     * @param[title] title of error dialog.
     * @param[details] details about the error.
     */
    override fun showError(title: String, details: String) {
        MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setIcon(R.drawable.ic_error)
                .setNeutralButton("Ok"){ _: DialogInterface, _: Int -> }
                .setMessage(details)
                .show()
    }
}