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

package com.acmpo6ou.myaccounts.account.superclass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditFragment
import com.acmpo6ou.myaccounts.databinding.CreateEditAccountFragmentBinding
import com.acmpo6ou.myaccounts.ui.account.CreateAccountViewModel

abstract class CreateEditAccountFragment : CreateEditFragment() {
    abstract override val viewModel: CreateAccountViewModel

    override val applyButton get() = b.applyButton
    override val buttonGenerate get() = b.accountGenerate

    override val nameField get() = b.accountName
    override val passwordField get() = b.accountPassword
    override val repeatPasswordField get() = b.accountRepeatPassword

    override val parentName get() = b.parentName
    override val parentPassword get() = b.parentPassword

    private var binding: CreateEditAccountFragmentBinding? = null
    val b: CreateEditAccountFragmentBinding get() = binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = CreateEditAccountFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = requireContext()
        app = context.applicationContext as MyApp
    }

    /**
     * Used to initialize all fields and buttons of the create_edit_account form.
     */
    override fun initForm() {
        super.initForm()

        // call applyPressed when clicking on the apply button
        applyButton.setOnClickListener {
            viewModel.applyPressed(
                    b.accountName.text.toString(),
                    b.accountUsername.text.toString(),
                    b.accountEmail.text.toString(),
                    b.accountPassword.text.toString(),
                    b.birthDate.text.toString(),
                    b.accountComment.text.toString())
        }
    }
}