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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.account.superclass.CreateEditAccountFragment

class CreateAccountFragment : CreateEditAccountFragment() {
    override lateinit var viewModel: CreateAccountViewModel
    private val accountsActivity get() = activity as? AccountsActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)
        accountsActivity?.database?.data?.let {
            viewModel.initialize(app, it)
            initModel()
            initAdapter()
        }
        initForm()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // set focus on account name field and display keyboard
        b.accountName.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(b.accountName, InputMethodManager.SHOW_IMPLICIT)
    }
}
