/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.SuperFragment
import com.acmpo6ou.myaccounts.databinding.OpenDatabaseFragmentBinding

class OpenDatabaseFragment: SuperFragment() {
    companion object {
        fun newInstance() = OpenDatabaseFragment()
    }

    lateinit var viewModel: OpenDatabaseViewModel
    var args: OpenDatabaseFragmentArgs? = null
    lateinit var myContext: Context
    lateinit var app: MyApp

    private val mainActivity get() = myContext as MainActivity

    var binding: OpenDatabaseFragmentBinding? = null
    val b: OpenDatabaseFragmentBinding get() = binding!!

    /**
     * This observer sets app bar title to `Open <database name>`.
     */
    private val titleObserver = Observer<String>{
        mainActivity.supportActionBar?.title = it
    }

    /**
     * This observer displays and hides error tip of password field depending on
     * state of incorrectPassword live data value of view model.
     */
    private val passwordObserver = Observer<Boolean>{
        if(it) {
            // display password error tip when incorrectPassword is true
            val passwordError = myContext.resources.getString(R.string.password_error)
            b.parentPassword.error = passwordError
        }
        else{
            // clear password error tip when incorrectPassword is false
            b.parentPassword.error = null
        }
    }

    /**
     * This observer displays error dialog when user tries to open corrupted database.
     */
    private val corruptedObserver = Observer<Boolean> {
        if(it){
            val index = args!!.databaseIndex
            val dbName = app.databases[index].name

            val errorTitle = myContext.resources.getString(R.string.open_error)
            val errorMsg = myContext.resources.getString(R.string.corrupted_db, dbName)
            mainActivity.showError(errorTitle, errorMsg)
        }
    }

    /**
     * This observer starts AccountsActivity when corresponding Database is opened.
     */
    private val openedObserver = Observer<Boolean> {
        if(it){
            startDatabase(args!!.databaseIndex)
        }
    }

    /**
     * This observer hides/displays loading progress bar of `Open database` button
     * depending on `loading` live data of view model.
     */
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

        // save arguments, context and app
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OpenDatabaseViewModel::class.java)
        initModel()

        b.openDatabase.setOnClickListener{
            viewModel.startPasswordCheck(b.databasePassword.text.toString())
        }
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    private fun initModel() {
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
        val mainActivity = myContext as AppCompatActivity
        mainActivity.findNavController(R.id.nav_host_fragment).navigateUp()
    }
}