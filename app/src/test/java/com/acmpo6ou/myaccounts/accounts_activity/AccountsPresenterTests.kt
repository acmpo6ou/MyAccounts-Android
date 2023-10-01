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

package com.acmpo6ou.myaccounts.accounts_activity

import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsPresenter
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.ListFragmentI
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class AccountsPresenterTests {
    lateinit var presenter: AccountsPresenter
    lateinit var spyPresenter: AccountsPresenter

    lateinit var view: AccountsActivityI
    lateinit var mockFragment: ListFragmentI

    val db = Database("main")
    lateinit var app: MyApp

    @Before
    fun setup() {
        app = MyApplication()
        mockFragment = mock()

        view = mock {
            on { database } doReturn db
            on { mainFragment } doReturn mockFragment
        }

        presenter = AccountsPresenter({ view }, app)
        spyPresenter = spy(presenter)
        doNothing().whenever(spyPresenter).saveDatabase(db.name, db)
    }

    @Test
    fun `saveSelected should call showSuccess`() {
        spyPresenter.saveSelected()
        verify(mockFragment).showSuccess()
    }

    @Test
    fun `saveSelected should call saveDatabase when database isn't saved`() {
        doReturn(false).whenever(spyPresenter).isDatabaseSaved(db)
        spyPresenter.saveSelected()
        verify(spyPresenter).saveDatabase(db.name, db)
    }

    @Test
    fun `saveSelected should not call saveDatabase when database is already saved`() {
        doReturn(true).whenever(spyPresenter).isDatabaseSaved(db)
        spyPresenter.saveSelected()
        verify(spyPresenter, never()).saveDatabase(db.name, db)
    }

    @Test
    fun `backPressed should call view confirmBack when database isn't saved`() {
        doReturn(false).whenever(spyPresenter).isDatabaseSaved(db)
        spyPresenter.backPressed()

        verify(view).confirmBack()
        verify(view, never()).goBack()
    }

    @Test
    fun `backPressed should call view goBack when database is saved`() {
        doReturn(true).whenever(spyPresenter).isDatabaseSaved(db)
        spyPresenter.backPressed()

        verify(view).goBack()
        verify(view, never()).confirmBack()
    }
}
