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

package com.acmpo6ou.myaccounts.database.open_database

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.utils.startDatabaseUtil
import com.acmpo6ou.myaccounts.core.superclass.ErrorFragment
import com.acmpo6ou.myaccounts.databinding.OpenDatabaseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@AndroidEntryPoint
class OpenDatabaseFragment : Fragment(), ErrorFragment {
    override val viewModel: OpenDatabaseViewModel by viewModels()
    var databaseIndex = 999

    @ActivityContext
    @Inject
    lateinit var myContext: Context

    @Inject
    lateinit var app: MyApp

    @Inject
    override lateinit var superActivity: MainActivity
    override lateinit var lifecycle: LifecycleOwner

    var binding: OpenDatabaseFragmentBinding? = null
    val b: OpenDatabaseFragmentBinding get() = binding!!

    // Displays error dialog when user tries to open corrupted database
    private val corruptedObserver = Observer<Boolean> {
        val errorTitle = myContext.resources.getString(R.string.open_error)
        val dbName = app.databases[databaseIndex].name
        val errorMsg = myContext.resources.getString(R.string.corrupted_db, dbName)
        superActivity.showError(errorTitle, errorMsg)
    }

    // Displays and hides error tip of password field
    private val passwordObserver = Observer<Boolean> {
        if (it) {
            val passwordError = myContext.resources.getString(R.string.password_error)
            b.parentPassword.error = passwordError
        } else {
            b.parentPassword.error = null
        }
    }

    // Hides/displays loading progress bar of `Open database` button
    private val loadingObserver = Observer<Boolean> {
        if (it) {
            b.progressLoading.visibility = View.VISIBLE
            b.openDatabase.isEnabled = false
        } else {
            b.progressLoading.visibility = View.GONE
            b.openDatabase.isEnabled = true
        }
    }

    // Starts AccountsActivity when corresponding Database is opened
    private val openedObserver = Observer<Boolean> {
        startDatabase()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            val args = OpenDatabaseFragmentArgs.fromBundle(it)
            databaseIndex = args.databaseIndex
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = OpenDatabaseFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle = viewLifecycleOwner
        initModel()

        // Set app bar title to `Open <database name>`
        val dbName = app.databases[databaseIndex].name
        val appTitle = myContext.resources.getString(R.string.open_db, dbName)
        superActivity.supportActionBar?.title = appTitle

        // open database when `Open database` button is pressed
        b.openDatabase.setOnClickListener {
            viewModel.startPasswordCheck(b.databasePassword.text.toString(), databaseIndex)
        }

        // open database when Enter is pressed in password field
        b.databasePassword.setOnEditorActionListener {
            _: TextView, action: Int, keyEvent: KeyEvent? ->

            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER ||
                action == EditorInfo.IME_ACTION_DONE ||
                action == EditorInfo.IME_ACTION_NEXT
            ) {
                b.openDatabase.performClick()
            }
            false
        }

        // listener to hide the password error when password changes, for user to be able
        // to hide/display the password using eye button
        b.databasePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                b.parentPassword.error = null
            }
        })

        // set focus on password field and display keyboard
        b.databasePassword.requestFocus()
        val imm = superActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(b.databasePassword, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun initModel() {
        super.initModel()

        // init observers
        viewModel.apply {
            viewLifecycleOwner.let {
                incorrectPassword.observe(it, passwordObserver)
                corrupted.observe(it, corruptedObserver)
                opened.observe(it, openedObserver)
                loading.observe(it, loadingObserver)
            }
        }
    }

    private fun startDatabase() {
        startDatabaseUtil(databaseIndex, myContext)
        // navigate back to DatabasesFragment
        superActivity.findNavController(R.id.nav_host_fragment).navigateUp()
    }
}
