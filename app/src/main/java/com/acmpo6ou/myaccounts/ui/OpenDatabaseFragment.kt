/*
 * Copyright (c) 2020. Kolvakh Bohdan
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
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.databinding.OpenDatabaseFragmentBinding

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

    override fun onStart(){
        super.onStart()
        // save arguments and context
        arguments?.let {
            args = OpenDatabaseFragmentArgs.fromBundle(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = requireContext()
        app = context?.applicationContext as MyApp
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
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    fun initModel() {
        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        val OPEN_DB = myContext.resources.getString(R.string.open_db)
        args?.let{
            viewModel.initialize(app, it.databaseIndex, SRC_DIR, OPEN_DB)
        }
    }

}