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

package com.acmpo6ou.myaccounts.account.create_edit_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.acmpo6ou.myaccounts.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountFragment : CreateEditAccountFragment() {
    override val viewModel: EditAccountViewModel by viewModels()
    lateinit var accountName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountName = arguments?.getString("accountName")!!

        initModel()
        initAdapter()
        initForm()
    }

    override fun initModel() {
        super.initModel()
        viewModel.initialize(accountName)
    }

    override fun initForm() {
        super.initForm()
        // change text of apply button from `Create` to `Save`
        b.applyButton.text = myContext.resources.getString(R.string.save)

        // fill all form fields with data of account being edited
        val accounts = superActivity.database.data
        val account = accounts[accountName]!!

        b.accountName.setText(account.accountName)
        b.accountUsername.setText(account.username)
        b.accountEmail.setText(account.email)
        b.accountPassword.setText(account.password)
        b.accountRepeatPassword.setText(account.password)
        b.birthDate.text = account.date
        b.accountComment.setText(account.comment)
    }
}
