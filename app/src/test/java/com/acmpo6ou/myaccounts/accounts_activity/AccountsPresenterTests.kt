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

import com.acmpo6ou.myaccounts.account.AccountsPresenter
import com.acmpo6ou.myaccounts.core.SuperActivityInter
import org.junit.Before
import org.junit.Test

class AccountsPresenterTests {
    lateinit var presenter: AccountsPresenter
    lateinit var view: SuperActivityInter

    @Before
    fun setup(){
        presenter = AccountsPresenter(view)
    }

    @Test
    fun `saveSelected should call saveDatabase when isDatabaseSaved returns false`(){

    }
}