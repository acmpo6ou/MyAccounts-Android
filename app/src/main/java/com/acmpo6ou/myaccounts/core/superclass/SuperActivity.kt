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
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.UpdatesActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

abstract class SuperActivity : AppCompatActivity(), SuperActivityI {
    @Inject
    override lateinit var app: MyApp

    @Inject
    override lateinit var prefs: SharedPreferences

    @Inject
    @ActivityContext
    override lateinit var myContext: Context
    override val activity get() = myContext as Activity

    abstract val b: ViewBinding
    abstract val navView: NavigationView
    abstract val drawerLayout: DrawerLayout

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    abstract val confirmGoingBackMsg: Int
    abstract val presenter: SuperPresenterI

    abstract val mainFragmentId: Int
    override val mainFragment: ListFragment
        get() {
            return supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
                ?.childFragmentManager
                ?.fragments
                ?.first() as ListFragment
        }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onStart() {
        super.onStart()
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
        navController.addOnDestinationChangedListener {
            _: NavController, navDestination: NavDestination, _: Bundle? ->
            if (navDestination.id == mainFragmentId) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    /**
     * Starts UpdatesActivity passing [version] as extra.
     * @param[version] update version.
     */
    override fun startUpdatesActivity(version: String) {
        val intent = Intent(this, UpdatesActivity::class.java)
        intent.putExtra("version", version)
        startActivity(intent)
    }

    /**
     * Displays a snackbar about updates.
     *
     * @param[isAutoCheck] if true do not display the snackbar because we should not
     * display it when auto checking for updates.
     */
    fun updatesSnackbar(message: Int, isAutoCheck: Boolean) {
        if (isAutoCheck) return

        Snackbar.make(
            mainFragment.b.coordinatorLayout,
            message, Snackbar.LENGTH_LONG
        )
            .setAction("HIDE") {}
            .show()
    }

    // updates snackbars
    override fun noUpdates(isAutoCheck: Boolean) =
        updatesSnackbar(R.string.no_updates, isAutoCheck)

    override fun updatesCheckFailed(isAutoCheck: Boolean) =
        updatesSnackbar(R.string.updates_check_failed, isAutoCheck)

    override fun noInternetConnection(isAutoCheck: Boolean) =
        updatesSnackbar(R.string.no_internet_connection, isAutoCheck)

    /**
     * Obtains version name and sets it in navigation header.
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
    override fun confirmBack() {
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (navController.currentDestination?.id == mainFragmentId) {
            presenter.backPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun goBack() = super.onBackPressed()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.check_for_updates -> presenter.checkUpdatesSelected()
            R.id.changelog -> navController.navigate(R.id.actionChangelog)
            R.id.settings -> navController.navigate(R.id.actionSettings)
            R.id.about -> navController.navigate(R.id.actionAbout)
        }
        return false
    }

    /**
     * Automatically hides the keyboard when any TextView is losing focus.
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
     * Displays error dialog.
     *
     * @param[title] dialog title.
     * @param[details] details about the error.
     */
    override fun showError(title: String, details: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setIcon(R.drawable.ic_error)
            .setNeutralButton("Ok") { _: DialogInterface, _: Int -> }
            .setMessage(details)
            .show()
    }
}
