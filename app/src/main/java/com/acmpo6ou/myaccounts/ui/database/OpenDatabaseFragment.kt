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

package com.acmpo6ou.myaccounts.ui.database

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.superclass.ViewModelFragment
import com.acmpo6ou.myaccounts.databinding.OpenDatabaseFragmentBinding

class OpenDatabaseFragment: ViewModelFragment() {
    override lateinit var viewModel: OpenDatabaseViewModel
    var args: OpenDatabaseFragmentArgs? = null

    lateinit var app: MyApp
    lateinit var myContext: Context
    override val mainActivity get() = myContext as MainActivity

    var binding: OpenDatabaseFragmentBinding? = null
    val b: OpenDatabaseFragmentBinding get() = binding!!

    // This observer sets app bar title to `Open <database name>`.
    private val titleObserver = Observer<String>{
        mainActivity.supportActionBar?.title = it
    }

    // This observer displays and hides error tip of password field
    private val passwordObserver = Observer<Boolean>{
        if(it) {
            val passwordError = myContext.resources.getString(R.string.password_error)
            b.parentPassword.error = passwordError
        }
        else{
            b.parentPassword.error = null
        }
    }

    // This observer displays error dialog when user tries to open corrupted database.
    private val corruptedObserver = Observer<Boolean> {
        if(!it) return@Observer

        val index = args!!.databaseIndex
        val dbName = app.databases[index].name

        val errorTitle = myContext.resources.getString(R.string.open_error)
        val errorMsg = myContext.resources.getString(R.string.corrupted_db, dbName)
        mainActivity.showError(errorTitle, errorMsg)
    }

    // This observer starts AccountsActivity when corresponding Database is opened.
    private val openedObserver = Observer<Boolean> {
        if(it) startDatabase(args!!.databaseIndex)
    }

    // This observer hides/displays loading progress bar of `Open database` button
    private val loadingObserver = Observer<Boolean> {
        if(it) {
            b.progressLoading.visibility = View.VISIBLE
            b.openDatabase.isEnabled = false
        }
        else{
            b.progressLoading.visibility = View.GONE
            b.openDatabase.isEnabled = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = requireContext()
        app = context.applicationContext as MyApp

        arguments?.let {
            args = OpenDatabaseFragmentArgs.fromBundle(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = OpenDatabaseFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // set focus on password field and display keyboard
        b.databasePassword.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(b.databasePassword, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OpenDatabaseViewModel::class.java)
        initModel()

        // open database when `Open database` button is pressed
        b.openDatabase.setOnClickListener{
            viewModel.startPasswordCheck(b.databasePassword.text.toString())
        }

        // open database when Enter is pressed in password field
        b.databasePassword.setOnEditorActionListener{
            _: TextView, action: Int, keyEvent: KeyEvent? ->

            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER ||
                action == EditorInfo.IME_ACTION_DONE ||
                action == EditorInfo.IME_ACTION_NEXT) {
                b.openDatabase.performClick()
            }
            false
        }
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    override fun initModel() {
        super.initModel()
        // init observers
        viewModel.apply {
            viewLifecycleOwner.let {
                _title.observe(it, titleObserver)
                _incorrectPassword.observe(it, passwordObserver)
                _corrupted.observe(it, corruptedObserver)
                _opened.observe(it, openedObserver)
                _loading.observe(it, loadingObserver)
            }
        }

        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        val titleStart = myContext.resources.getString(R.string.open_db)
        args?.let{
            viewModel.initialize(app, SRC_DIR, titleStart, it.databaseIndex)
        }
    }

    /**
     * Used to start AccountsActivity for given database.
     * @param[index] index of database for which we want to start AccountsActivity.
     */
    override fun startDatabase(index: Int) {
        super.startDatabase(index)

        // navigate back to DatabaseFragment
        mainActivity.findNavController(R.id.nav_host_fragment).navigateUp()
    }
}