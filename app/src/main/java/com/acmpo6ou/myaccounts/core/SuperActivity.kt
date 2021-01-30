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

package com.acmpo6ou.myaccounts.core

import android.os.Bundle
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
import com.acmpo6ou.myaccounts.R
import com.google.android.material.navigation.NavigationView

abstract class SuperActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    abstract val b: ViewBinding
    private val navView: NavigationView get() = getProperty(b, "navView")
    private val drawerLayout: DrawerLayout get() = getProperty(b, "drawerLayout")

    lateinit var appBarConfiguration: AppBarConfiguration
    abstract val mainFragment: Int

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        setAppVersion()

        // setup navigation controller
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // setup navigation view
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        // navigation drawer should be unlocked only on mainFragment and locked
        // everywhere else
        navController.addOnDestinationChangedListener{
            _: NavController, navDestination: NavDestination, _: Bundle? ->
            if(navDestination.id == mainFragment){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            else{
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
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

    override fun onBackPressed() {
        // close navigation drawer when Back button is pressed and if it is opened
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }
}