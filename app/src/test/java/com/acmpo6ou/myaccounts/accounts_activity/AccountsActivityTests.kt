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

package com.acmpo6ou.myaccounts.accounts_activity

import androidx.core.view.GravityCompat
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.AccountsPresenterInter
import com.acmpo6ou.myaccounts.selectNavigationItem
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test

class AccountsActivityTests {
    private lateinit var activity: AccountsActivity
    private lateinit var presenter: AccountsPresenterInter

    @Before
    fun setup(){
        activity = AccountsActivity()
        presenter = mock()
        activity.presenter = presenter
    }

    @Test
    fun `'Save' should call presenter saveSelected`(){
        selectNavigationItem(R.id.save_database, activity)
        verify(presenter).saveSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `navigation drawer should be closed when any of it's items is selected`(){
        activity.drawerLayout = mock()
        selectNavigationItem(R.id.save_database, activity)
        verify(activity.drawerLayout).closeDrawer(GravityCompat.START)
    }
}