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

import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

open class AccountsListPresenter(val view: AccountsFragmentInter) : AccountsListPresenterInter {
    override val accounts: DbMap get() = view.accountsActivity?.database?.data ?: mutableMapOf()

    // [accounts] is a map of [String] to [Account], but AccountsAdapter
    // will typically work with indexes rather then Strings, so here we have a dynamic
    // property to convert [accounts] to sorted by account names list
    override val accountsList: List<Account>
        get() = accounts.values.toList().sortedBy { it.name }

    /**
     * Called when user selects item in accounts list.
     *
     * Using navigateToDisplay navigates to DisplayAccountFragment passing through
     * account name.
     * @param[i] index of account we want to display.
     */
    override fun displayAccount(i: Int) = view.navigateToDisplay(accountsList[i].account)

    /**
     * Called when user selects `Edit` in account item popup menu.
     *
     * Using navigateToEdit navigates to EditAccountFragment passing through account name.
     * @param[i] index of account we want to edit.
     */
    override fun editAccount(i: Int) = view.navigateToEdit(accountsList[i].account)

    /**
     * Called when user selects `Delete` in account item popup menu.
     *
     * Calls confirmDelete to display a dialog about confirmation of account deletion.
     * @param[i] index of account we want to delete.
     */
    override fun deleteSelected(i: Int) = view.confirmDelete(i)

    /**
     * Removes account from database map by [i] index and notifies about deletion.
     * @param[i] account index.
     */
    override fun deleteAccount(i: Int) {
        accounts.remove(accountsList[i].account)
        view.notifyRemoved(i)
    }
}