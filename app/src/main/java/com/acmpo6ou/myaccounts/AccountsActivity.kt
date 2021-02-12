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
import androidx.preference.PreferenceManager
import com.acmpo6ou.myaccounts.account.AccountsActivityInter
import com.acmpo6ou.myaccounts.account.AccountsPresenter
import com.acmpo6ou.myaccounts.account.AccountsPresenterInter
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.SuperActivity
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.databinding.ActivityAccountsBinding
import com.acmpo6ou.myaccounts.ui.account.AccountsFragment

class AccountsActivity : SuperActivity(), AccountsActivityInter {

    override lateinit var b: ActivityAccountsBinding
    override lateinit var prefs: SharedPreferences
    override val mainFragmentId = R.id.accountsFragment

    override lateinit var presenter: AccountsPresenterInter
    override lateinit var database: Database

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

        val index = intent.extras!!.getInt("databaseIndex")
        database = app.databases[index]
    }

    /**
     * Displays snackbar to tell user that there are no updates available.
     */
    override fun noUpdates(){
        // get view binding of AccountFragment because we need to show snackbar in
        // coordinator layout. AccountsActivity doesn't have one but AccountFragment does.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val accountsFragment =
                navHostFragment?.childFragmentManager?.fragments?.get(0) as AccountsFragment
        val coordinatorLayout = accountsFragment.b.coordinatorLayout
        super.noUpdates(coordinatorLayout)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.save_database){
            presenter.saveSelected()
        }
        else{
            super.onNavigationItemSelected(item)
        }

        // close drawer when any item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}