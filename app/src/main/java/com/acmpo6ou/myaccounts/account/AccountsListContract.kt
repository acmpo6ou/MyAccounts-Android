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

package com.acmpo6ou.myaccounts.account

import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.core.superclass.ListFragmentInter
import com.acmpo6ou.myaccounts.core.superclass.ListPresenter
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

interface AccountsFragmentInter : ListFragmentInter{
    val presenter: AccountsListPresenterInter
    val accountsActivity: AccountsActivity

    fun navigateToDisplay(account: Account)
    fun navigateToEdit(account: Account)
}

interface AccountsListPresenterInter : ListPresenter {
    var accounts: DbMap
    val accountsList: List<Account>

    fun displayAccount(i: Int)
    fun editAccount(i: Int)
    fun deleteAccount(i: Int)
}