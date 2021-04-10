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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.acmpo6ou.myaccounts.account.AccountsActivityI
import com.acmpo6ou.myaccounts.account.AccountsPresenter
import com.acmpo6ou.myaccounts.account.AccountsPresenterI
import com.acmpo6ou.myaccounts.core.superclass.SuperActivity
import com.acmpo6ou.myaccounts.databinding.ActivityAccountsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class AccountsActivity : SuperActivity(), AccountsActivityI {
    override lateinit var b: ActivityAccountsBinding
    override lateinit var prefs: SharedPreferences
    override lateinit var presenter: AccountsPresenterI

    override val mainFragmentId = R.id.accountsFragment
    override val confirmGoingBackMsg = R.string.confirm_going_back

    var index = 0
    override var database
        get() = app.databases[index]
        set(value) {
            app.databases[index] = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = applicationContext as MyApp
        myContext = this
        presenter = AccountsPresenter(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        loadSettings()

        b = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.appbar.toolbar)

        intent.extras?.let {
            index = it.getInt("databaseIndex")
        }
        supportActionBar?.title = database.name

        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener {
            _: NavController, navDestination: NavDestination, _: Bundle? ->
            // change app bar title back to database name after navigating to main fragment
            if (navDestination.id == mainFragmentId) {
                supportActionBar?.title = database.name
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_database) {
            presenter.saveSelected()
        } else {
            super.onNavigationItemSelected(item)
        }

        // close drawer when any item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}
