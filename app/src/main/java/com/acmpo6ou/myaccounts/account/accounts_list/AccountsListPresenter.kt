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

import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.DbMap
import dagger.Lazy
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
open class AccountsListPresenter @Inject constructor(
    private val fragment: Lazy<AccountsFragmentI>,
    private val accountsActivity: AccountsActivityI,
) : AccountsListPresenterI {

    override val accounts: DbMap get() = accountsActivity.database.data
    val view: AccountsFragmentI get() = fragment.get()

    // [accounts] is a map of [String] to [Account], but AccountsAdapter
    // will typically work with indexes rather then Strings, so here we have a dynamic
    // property to convert [accounts] to sorted by account names list
    override val accountsList: List<Account>
        get() = accounts.values.toList().sortedBy { it.accountName }

    override fun displayAccount(account: Account) =
        view.navigateToDisplay(account.accountName)

    override fun editAccount(account: Account) =
        view.navigateToEdit(account.accountName)

    override fun deleteSelected(account: Account) =
        view.confirmDelete(account)

    override fun deleteAccount(account: Account) {
        val index = accountsList.indexOf(account)
        accounts.remove(account.accountName)
        view.notifyRemoved(index)
    }
}
