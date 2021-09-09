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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.acmpo6ou.myaccounts.core.superclass.SuperActivity
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainPresenterI
import com.acmpo6ou.myaccounts.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : SuperActivity(), MainActivityI {
    @Inject
    override lateinit var presenter: MainPresenterI

    override lateinit var b: ActivityMainBinding
    override lateinit var navView: NavigationView
    override lateinit var drawerLayout: DrawerLayout

    override val mainFragmentId = R.id.databasesFragment
    override val confirmGoingBackMsg = R.string.confirm_exit
    override var lastBackPressTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyAccounts_NoActionBar)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        loadSettings()
        app.res = resources

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        navView = b.navView
        drawerLayout = b.drawerLayout

        setSupportActionBar(b.appbar.toolbar)
        checkStoragePermission()
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    private fun checkStoragePermission() {
        val isGranted = checkCallingOrSelfPermission(permission.WRITE_EXTERNAL_STORAGE)
        if (isGranted != PackageManager.PERMISSION_GRANTED)
            permissionLauncher.launch(permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.import_database) {
            presenter.importSelected()
        } else {
            super.onNavigationItemSelected(item)
        }

        // close drawer when any item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    private val importLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { presenter.checkDbaFile(it) }
            }
        }

    /**
     * Displays import dialog where user can choose database that he wants to import.
     */
    override fun importDialog() =
        with(Intent(Intent.ACTION_OPEN_DOCUMENT)) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            importLauncher.launch(this)
        }

    override fun showExitTip() {
        Snackbar.make(
            mainFragment.b.coordinatorLayout,
            R.string.exit_tip,
            4000
        )
            .setAction("HIDE") {}
            .show()
    }

    /**
     * This method calls notifyChanged on DatabasesFragment to rerender the list.
     * @param[i] index of Database that were added to databases list.
     */
    override fun notifyChanged(i: Int) {
        val databasesFragment = mainFragment as DatabasesFragmentI
        databasesFragment.notifyChanged(i)
    }
}
