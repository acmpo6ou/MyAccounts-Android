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

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.superclass.CreateEditFragment

class EditDatabaseFragment : CreateEditFragment() {
    companion object {
        fun newInstance() = EditDatabaseFragment()
    }
    override lateinit var viewModel: EditDatabaseViewModel
    var args: EditDatabaseFragmentArgs? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditDatabaseViewModel::class.java)
        initModel()
        initForm()
    }

    /**
     * Used to initialize all fields and buttons of the create_edit_database form.
     */
    override fun initForm() {
        super.initForm()
        // set name and password fields to data of database being edited
        args?.let {
            val database = app.databases[it.databaseIndex]
            b.databaseName.setText(database.name)
            b.databasePassword.setText(database.password)
            b.databaseRepeatPassword.setText(database.password)
        }

        // change text of apply button from `Create` to `Save`
        b.applyButton.text = myContext.resources.getString(R.string.save)
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    override fun initModel() {
        super.initModel()
        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        val titleStart = myContext.resources.getString(R.string.edit_db)

        arguments?.let{
            args = EditDatabaseFragmentArgs.fromBundle(it)
            val databaseIndex = args!!.databaseIndex
            viewModel.initialize(app, SRC_DIR, titleStart, databaseIndex)
        }
    }
}