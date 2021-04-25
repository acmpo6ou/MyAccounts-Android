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

package com.acmpo6ou.myaccounts.core.superclass

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.utils.GenPassDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Super class for all fragments that edit/create items.
 */
abstract class CreateEditFragment : Fragment() {
    abstract val nameField: TextInputEditText
    abstract val passwordField: TextInputEditText
    abstract val repeatPasswordField: TextInputEditText

    abstract val parentName: TextInputLayout
    abstract val parentPassword: TextInputLayout

    abstract val buttonGenerate: Button
    abstract val applyButton: Button

    lateinit var app: MyApp
    lateinit var myContext: Context
    abstract val viewModel: CreateEditViewModel
    private val superActivity get() = myContext as SuperActivity

    // Hides/displays name error tip
    private val nameErrorObserver = Observer<String?> {
        parentName.error = it
    }

    // Hides/displays password error tip
    private val passwordErrorObserver = Observer<String?> {
        parentPassword.error = it
    }

    // if there are any errors (about name or password) apply button is disabled,
    // otherwise enabled
    private val applyEnabledObserver = Observer<Boolean> {
        applyButton.isEnabled = it
    }

    // This observer invoked when database/account is successfully created/edited.
    // It navigates back to the main fragment.
    private val finishedObserver = Observer<Boolean> {
        superActivity.findNavController(R.id.nav_host_fragment).navigateUp()
    }

    /**
     * Used to initialize all fields and buttons of the form.
     */
    open fun initForm() {
        // when name is changed validate it using model to display error in case
        // such name already exists or the name is empty
        nameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                viewModel.validateName(s.toString())
        })

        // text changed listener for password fields to validate them and display error
        // in case of problems
        val passwordListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                viewModel.validatePasswords(
                    passwordField.text.toString(),
                    repeatPasswordField.text.toString()
                )
        }
        passwordField.addTextChangedListener(passwordListener)
        repeatPasswordField.addTextChangedListener(passwordListener)

        // display generate password dialog when `Generate` button is pressed
        buttonGenerate.setOnClickListener {
            GenPassDialog(superActivity, passwordField, repeatPasswordField)
        }
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    open fun initModel() {
        // init observers
        viewModel.apply {
            viewLifecycleOwner.let {
                nameErrors.observe(it, nameErrorObserver)
                passwordErrors.observe(it, passwordErrorObserver)
                finished.observe(it, finishedObserver)
                applyEnabled.observe(it, applyEnabledObserver)
            }
        }
    }
}
