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

package com.acmpo6ou.myaccounts.account.accounts_list

import android.os.Bundle
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragmentDirections.actionDisplayAccount
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragmentDirections.actionEditAccount
import com.acmpo6ou.myaccounts.core.superclass.ListFragment

/**
 * Fragment representing a list of Accounts.
 */
class AccountsFragment : ListFragment(), AccountsFragmentI {
    override lateinit var adapter: AccountsAdapter
    override lateinit var presenter: AccountsListPresenterI

    override val accountsActivity get() = activity as? AccountsActivityI
    override val items get() = presenter.accountsList
    override val actionCreateItem = R.id.actionCreateAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AccountsAdapter(this)
        presenter = AccountsListPresenter(this)
    }

    /**
     * Navigates to DisplayAccountFragment passing account name.
     * @param[name] name of account we want to display.
     */
    override fun navigateToDisplay(name: String) {
        val action = actionDisplayAccount(name)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Navigates to EditAccountFragment passing account name.
     * @param[name] name of account we want to edit.
     */
    override fun navigateToEdit(name: String) {
        val action = actionEditAccount(name)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Displays a dialog for user to confirm deletion of account.
     *
     * If user is choosing No – we will do nothing, if Yes – delete account.
     * @param[i] - account index.
     */
    override fun confirmDelete(i: Int) {
        val name = presenter.accountsList[i].accountName
        val message = resources.getString(R.string.confirm_account_delete, name)
        confirmDialog(message) { presenter.deleteAccount(i) }
    }
}
