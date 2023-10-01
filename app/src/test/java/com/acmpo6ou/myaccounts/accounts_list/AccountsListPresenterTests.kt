/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.accounts_list

import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragmentI
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListPresenter
import com.acmpo6ou.myaccounts.copy
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.databaseMap
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class AccountsListPresenterTests {
    lateinit var view: AccountsFragmentI
    lateinit var presenter: AccountsListPresenter

    @Before
    fun setup() {
        val mockActivity: AccountsActivityI = mock {
            on { database } doReturn Database(
                "",
                data = databaseMap.copy()
            )
        }
        view = mock()
        presenter = AccountsListPresenter({ view }, mockActivity)
    }

    @Test
    fun `displayAccount should call view navigateToDisplay`() {
        presenter.displayAccount(account)
        verify(view).navigateToDisplay(account.accountName)
    }

    @Test
    fun `editAccount should call view navigateToEdit`() {
        presenter.editAccount(account)
        verify(view).navigateToEdit(account.accountName)
    }

    @Test
    fun `deleteSelected should call view confirmDelete`() {
        presenter.deleteSelected(account)
        verify(view).confirmDelete(account)
    }

    @Test
    fun `deleteAccount should remove account from 'accounts' map`() {
        presenter.deleteAccount(account)
        assertFalse(account in presenter.accountsList)
    }

    @Test
    fun `deleteAccount should call view notifyRemoved`() {
        presenter.deleteAccount(account)
        verify(view).notifyRemoved(0)
    }
}
