/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.database.databases_list

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.ListFragment
import com.acmpo6ou.myaccounts.core.utils.startDatabaseUtil
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.*
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DatabasesFragment : ListFragment(), DatabasesFragmentI {
    @Inject
    lateinit var app: MyApp

    @Inject
    override lateinit var adapter: DatabasesAdapter

    @Inject
    override lateinit var presenter: DatabasesPresenterI

    @Inject
    lateinit var mainActivity: MainActivityI

    override val items get() = app.databases
    override val actionCreateItem = R.id.actionCreateDatabase

    private val exportLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                result.data?.data?.let {
                    presenter.exportDatabase(it)
                }
        }

    override fun exportDialog(database: Database) {
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "${database.name}.dba")
            exportLauncher.launch(this)
        }
    }

    override fun confirmDelete(database: Database) {
        val message =
            resources.getString(R.string.confirm_delete, database.name)
        confirmDialog(message) { presenter.deleteDatabase(database) }
    }

    override fun confirmClose(database: Database) {
        val message =
            resources.getString(R.string.confirm_close, database.name)
        confirmDialog(message) { presenter.closeDatabase(database) }
    }

    override fun navigateToEdit(index: Int) {
        val action = actionEditDatabase(index)
        view?.findNavController()?.navigate(action)
    }

    override fun navigateToRename(index: Int) {
        val action = actionRenameDatabase(index)
        view?.findNavController()?.navigate(action)
    }

    override fun navigateToOpen(index: Int) {
        val action = actionOpenDatabase(index)
        view?.findNavController()?.navigate(action)
    }

    override fun showError(title: String, details: String) =
        mainActivity.showError(title, details)
    override fun startDatabase(index: Int) =
        startDatabaseUtil(index, requireContext())
}
