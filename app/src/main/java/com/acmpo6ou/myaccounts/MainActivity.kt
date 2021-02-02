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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.SuperActivity
import com.acmpo6ou.myaccounts.core.loadSettings
import com.acmpo6ou.myaccounts.database.MainActivityInter
import com.acmpo6ou.myaccounts.database.MainPresenter
import com.acmpo6ou.myaccounts.database.MainPresenterInter
import com.acmpo6ou.myaccounts.databinding.ActivityMainBinding
import com.acmpo6ou.myaccounts.ui.database.DatabaseFragment

class MainActivity : SuperActivity(), MainActivityInter {
    val IMPORT_RC = 202

    override lateinit var b: ActivityMainBinding
    override lateinit var presenter: MainPresenterInter
    override val mainFragmentId = R.id.databaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyAccounts_NoActionBar)
        super.onCreate(savedInstanceState)
        loadSettings(this)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        ACCOUNTS_DIR = getExternalFilesDir(null)!!.path + "/"
        myContext = this
        app = applicationContext as MyApp
        app.res = resources

        // setup presenter and action bar
        presenter = MainPresenter(this)
        setSupportActionBar(b.appbar.toolbar)

        checkPermissions()
    }

    /**
     * This method checks if storage permission is granted and if not - requests it.
     */
    private fun checkPermissions() {
        val isGranted = checkCallingOrSelfPermission(permission.WRITE_EXTERNAL_STORAGE)
        if(isGranted != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE), 300)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.import_database){
            presenter.importSelected()
        }
        else{
            super.onNavigationItemSelected(item)
        }

        // close drawer when any item is selected
        b.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    /**
     * Displays snackbar to tell user that there are no updates available.
     */
    override fun noUpdates(){
        // get view binding of DatabaseFragment because we need to show snackbar in
        // coordinator layout. MainActivity doesn't have one but DatabaseFragment does.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val databaseFragment =
                navHostFragment?.childFragmentManager?.fragments?.get(0) as DatabaseFragment
        val coordinatorLayout = databaseFragment.b.coordinatorLayout
        super.noUpdates(coordinatorLayout)
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
}