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

import android.os.Bundle
import android.view.MenuItem
import com.acmpo6ou.myaccounts.core.SuperActivity
import com.acmpo6ou.myaccounts.core.loadSettings
import com.acmpo6ou.myaccounts.databinding.ActivityAccountsBinding

class AccountsActivity : SuperActivity() {
    override lateinit var b: ActivityAccountsBinding
    override val mainFragment = R.id.accountsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettings(this)

        b = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.appbar.toolbar)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}