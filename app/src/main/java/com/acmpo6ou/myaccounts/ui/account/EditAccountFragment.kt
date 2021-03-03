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

package com.acmpo6ou.myaccounts.ui.account

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.superclass.CreateEditAccountFragment
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

class EditAccountFragment : CreateEditAccountFragment() {
    override lateinit var viewModel: EditAccountViewModel
    private val accountsActivity get() = activity as? AccountsActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditAccountViewModel::class.java)

        val accounts = accountsActivity?.database?.data
        val accountName = arguments?.getString("accountName")

        if (accounts != null && accountName != null) {
            viewModel.initialize(app, accounts, accountName)
            initModel()
            initForm()
            setAccount(accounts, accountName)
        }
    }

    /**
     * Fills all form fields with data from provided account.
     * @param[accounts] database map of accounts.
     * @param[accountName] name of account to retrieve it from [accounts] map.
     */
    fun setAccount(accounts: DbMap, accountName: String) {
        val account: Account = accounts[accountName]!!

        b.accountName.setText(account.accountName)
        b.accountUsername.setText(account.username)
        b.accountEmail.setText(account.email)
        b.accountPassword.setText(account.password)
        b.accountRepeatPassword.setText(account.password)
        b.birthDate.text = account.date
        b.accountComment.setText(account.comment)
    }

    override fun initForm() {
        super.initForm()

        // change text of apply button from `Create` to `Save`
        b.applyButton.text = myContext.resources.getString(R.string.save)
    }
}