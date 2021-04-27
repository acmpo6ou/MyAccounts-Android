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

package com.acmpo6ou.myaccounts.database.create_edit_database

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.superclass.CreateEditDatabaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class EditDatabaseFragment : CreateEditDatabaseFragment() {
    override val viewModel: EditDatabaseViewModel by viewModels()
    var databaseIndex by Delegates.notNull<Int>()

    @Inject
    lateinit var app: MyApp

    @Inject
    override lateinit var superActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = EditDatabaseFragmentArgs.fromBundle(it)
            databaseIndex = args.databaseIndex
            viewModel.databaseIndex = databaseIndex
        }

        initModel()
        initForm()
    }

    override fun initForm() {
        super.initForm()
        // Set app bar title to `Edit <database name>`
        val dbName = app.databases[databaseIndex].name
        val appTitle = myContext.resources.getString(R.string.edit_db, dbName)
        superActivity.supportActionBar?.title = appTitle

        // set name and password fields to data of database being edited
        val database = app.databases[databaseIndex]
        b.databaseName.setText(database.name)
        b.databasePassword.setText(database.password)
        b.databaseRepeatPassword.setText(database.password)

        // change text of apply button from `Create` to `Save`
        b.applyButton.text = myContext.resources.getString(R.string.save)
    }
}
