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

package com.acmpo6ou.myaccounts.account.accounts_list

import com.acmpo6ou.myaccounts.core.superclass.ListFragmentI
import com.acmpo6ou.myaccounts.core.superclass.ListPresenter
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.DbMap

interface AccountsFragmentI : ListFragmentI {
    val presenter: AccountsListPresenterI

    fun navigateToDisplay(name: String)
    fun navigateToEdit(name: String)
    fun confirmDelete(account: Account)
}

interface AccountsListPresenterI : ListPresenter {
    val accounts: DbMap
    val accountsList: List<Account>

    fun displayAccount(account: Account)
    fun editAccount(account: Account)
    fun deleteSelected(account: Account)
    fun deleteAccount(account: Account)
}
