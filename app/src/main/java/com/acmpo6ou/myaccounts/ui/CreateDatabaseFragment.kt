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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.databinding.CreateEditDatabaseFragmentBinding

class CreateDatabaseFragment : Fragment() {
    companion object {
        fun newInstance() = CreateDatabaseFragment()
    }

    lateinit var viewModel: CreateDatabaseViewModel
    lateinit var app: MyApp
    lateinit var myContext: Context

    private val nameErrorObserver = Observer<Boolean>{
        if(it){
            val msg = myContext.resources.getString(R.string.empty_name)
            b.parentName.error = msg
        }
        else{
            b.parentName.error = null
        }
    }

    var binding: CreateEditDatabaseFragmentBinding? = null
    val b: CreateEditDatabaseFragmentBinding get() = binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = CreateEditDatabaseFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // save context and app
        myContext = requireContext()
        app = context.applicationContext as MyApp
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateDatabaseViewModel::class.java)
        initModel()

        // when database name is change validate it using model to display error in case
        // such name already exists or the name is empty
        b.databaseName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s:CharSequence, start:Int,
                                       before:Int, count:Int) {
                viewModel.validateName(s.toString())
            }
        })
    }


    /**
     * This method initializes view model providing all needed resources.
     */
    private fun initModel() {
        // init observers
        viewModel.apply {
            viewLifecycleOwner.let {
                emptyNameErr_.observe(it, nameErrorObserver)
            }
        }

        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        viewModel.initialize(app, SRC_DIR)
    }
}