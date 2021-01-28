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

package com.acmpo6ou.myaccounts

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.acmpo6ou.myaccounts.core.MainPresenterInter
import com.acmpo6ou.myaccounts.core.loadSettings
import com.acmpo6ou.myaccounts.databinding.ActivityMainBinding
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener, MainActivityInter {

    val IMPORT_RC = 202
    override lateinit var ACCOUNTS_DIR: String
    override lateinit var myContext: Context
    override lateinit var app: MyApp

    lateinit var b: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var presenter: MainPresenterInter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyAccounts_NoActionBar)
        super.onCreate(savedInstanceState)
        loadSettings(this)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        ACCOUNTS_DIR = getExternalFilesDir(null)!!.path + "/"
        myContext = this
        app = applicationContext as MyApp

        // setup presenter and action bar
        presenter = MainPresenter(this)
        setSupportActionBar(b.appbar.toolbar)

        // setup navigation controller
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, b.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // setup navigation view
        b.navView.setupWithNavController(navController)
        b.navView.setNavigationItemSelectedListener(this)

        // navigation drawer should be unlocked only on DatabaseFragment and locked
        // everywhere else
        navController.addOnDestinationChangedListener{ _: NavController, navDestination: NavDestination, _: Bundle? ->
            if(navDestination.id == R.id.databaseFragment){
                b.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            else{
                b.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        checkPermissions()
        setAppVersion()
    }

    /**
     * This method obtains version name and sets it in navigation header.
     */
    private fun setAppVersion() {
        val version = BuildConfig.VERSION_NAME
        val header = b.navView.getHeaderView(0)
        val versionString = header.findViewById<TextView>(R.id.versionString)
        versionString.text = version
    }

    /**
     * This method checks if permission WRITE_EXTERNAL_STORAGE is granted and requests it if
     * it's not.
     */
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
        b.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onBackPressed() {
        // close navigation drawer when Back button is pressed and if it is opened
        if(b.drawerLayout.isDrawerOpen(GravityCompat.START)){
            b.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    /**
     * Displays snackbar to tell user that there are no updates available.
     */
    override fun noUpdates(){
        // get view binding of DatabaseFragment because we need to show snackbar in
        // coordinator layout. MainActivity doesn't have one but DatabaseFragment has
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val databaseFragment =
                navHostFragment?.childFragmentManager?.fragments?.get(0) as DatabaseFragment
        val databaseBinding = databaseFragment.b

        Snackbar.make(databaseBinding.databaseCoordinator,
                R.string.no_updates,
                Snackbar.LENGTH_LONG)
            .setAction("HIDE"){}
            .show()
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
     * Starts intent with [IMPORT_RC] request code.
     * Shows dialog to chose location using Storage Access framework.
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
     * This method calls notifyChanged on DatabaseFragment to rerender the list.
     * @param[i] index of Database that were added to databases list.
     */
    override fun notifyChanged(i: Int){
        // get DatabaseFragment as it contains the list of databases and is responsible
        // for rerendering
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val databaseFragment =
                navHostFragment?.childFragmentManager?.fragments?.get(0) as DatabaseFragment
        databaseFragment.notifyChanged(i)
    }

    /**
     * Used to display dialog saying that the error occurred.
     *
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