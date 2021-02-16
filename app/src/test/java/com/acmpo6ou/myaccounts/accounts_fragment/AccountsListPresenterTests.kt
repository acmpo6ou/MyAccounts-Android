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

package com.acmpo6ou.myaccounts.accounts_fragment

import com.acmpo6ou.myaccounts.account.AccountsFragmentInter
import com.acmpo6ou.myaccounts.account.AccountsListPresenter
import com.acmpo6ou.myaccounts.getAccount
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test

class AccountsListPresenterTests {
    lateinit var view: AccountsFragmentInter
    lateinit var presenter: AccountsListPresenter

    @Before
    fun setup(){
        view = mock()
        doAnswer { throw ClassCastException() }.whenever(view).accountsActivity

        presenter = AccountsListPresenter(view)
        presenter.accounts = getDatabaseMap()
    }

    @Test
    fun `displayAccount should call view navigateToDisplay`(){
        presenter.displayAccount(0)
        verify(view).navigateToDisplay(getAccount())
    }
}