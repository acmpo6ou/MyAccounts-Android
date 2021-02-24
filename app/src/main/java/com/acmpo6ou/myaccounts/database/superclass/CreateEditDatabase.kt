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

package com.acmpo6ou.myaccounts.database.superclass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditFragment
import com.acmpo6ou.myaccounts.databinding.CreateEditDatabaseFragmentBinding

/**
 * Super class for CreateDatabaseFragment and EditDatabaseFragment.
 */
abstract class CreateEditDatabase : CreateEditFragment() {
    override val mainActivity get() = myContext as MainActivity
    override val applyButton get() = b.applyButton
    override val buttonGenerate get() = b.databaseGenerate

    override val nameField get() = b.databaseName
    override val passwordField get() = b.databasePassword
    override val repeatPasswordField get() = b.databaseRepeatPassword

    override val parentName get() = b.parentName
    override val parentPassword get() = b.parentPassword


    // Hides/displays loading progress bar of apply button.
    private val loadingObserver = Observer<Boolean> {
        if(it) {
            b.progressLoading.visibility = View.VISIBLE
            b.applyButton.isEnabled = false
        }
        else{
            b.progressLoading.visibility = View.GONE
            b.applyButton.isEnabled = true
        }
    }

    private var binding: CreateEditDatabaseFragmentBinding? = null
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

    /**
     * This method initializes view model providing all needed resources.
     */
    override fun initModel() {
        super.initModel()
        viewModel._loading.observe(viewLifecycleOwner, loadingObserver)

        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        viewModel.initialize(app, SRC_DIR)
    }
}