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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.acmpo6ou.myaccounts.core.MainPresenterInter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_database_list.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener, MainActivityInter {

    val IMPORT_RC = 202
    override lateinit var ACCOUNTS_DIR: String

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var presenter: MainPresenterInter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ACCOUNTS_DIR = resources.getString(R.string.accounts_dir)

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
        ).show()
    }

    override fun showError(title: String, details: String) {

    }

    override fun startUpdatesActivity() {

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?){
        super.onActivityResult(requestCode, resultCode, resultData)
        // if activity was canceled don't do anything
        if (resultCode != Activity.RESULT_OK){
            return
        }

        when(requestCode) {
            IMPORT_RC -> presenter.checkTarFile(resultData?.data.toString())
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
}