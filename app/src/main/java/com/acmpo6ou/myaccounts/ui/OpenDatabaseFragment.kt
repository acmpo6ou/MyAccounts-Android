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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.errorDialog
import com.acmpo6ou.myaccounts.core.startDatabaseUtil
import com.acmpo6ou.myaccounts.databinding.OpenDatabaseFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OpenDatabaseFragment : Fragment() {

    companion object {
        fun newInstance() = OpenDatabaseFragment()
    }

    lateinit var viewModel: OpenDatabaseViewModel
    var args: OpenDatabaseFragmentArgs? = null
    lateinit var myContext: Context
    lateinit var app: MyApp

    var binding: OpenDatabaseFragmentBinding? = null
    val b: OpenDatabaseFragmentBinding get() = binding!!

    /**
     * This observer sets app bar title to something like `Open <database name>`.
     */
    private val titleObserver = Observer<String>{
        val mainActivity = activity as MainActivity
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
     * This observer displays error dialog when database user tries to open is corrupted.
     */
    private val corruptedObserver = Observer<Boolean> {
        if(it){
            val index = args!!.databaseIndex
            val dbName = app.databases[index].name

            val errorTitle = myContext.resources.getString(R.string.open_error)
            val errorMsg = myContext.resources.getString(R.string.corrupted_db, dbName)
            errorDialog(myContext, errorTitle, errorMsg)
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
        savedInstanceState: Bundle?): View? {
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
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                viewModel.verifyPassword(b.databasePassword.text.toString())
            }
        }
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    private fun initModel() {
        // init observers
        viewModel.title.observe(viewLifecycleOwner, titleObserver)
        viewModel.incorrectPassword.observe(viewLifecycleOwner, passwordObserver)
        viewModel.corrupted.observe(viewLifecycleOwner, corruptedObserver)
        viewModel.opened.observe(viewLifecycleOwner, openedObserver)

        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        val OPEN_DB = myContext.resources.getString(R.string.open_db)
        args?.let{
            viewModel.initialize(app, it.databaseIndex, SRC_DIR, OPEN_DB)
        }
    }

    /**
     * Used to start AccountsActivity for given database.
     *
     * @param[index] index of database for which we want to start AccountsActivity.
     */
    fun startDatabase(index: Int) = startDatabaseUtil(index, this)
}