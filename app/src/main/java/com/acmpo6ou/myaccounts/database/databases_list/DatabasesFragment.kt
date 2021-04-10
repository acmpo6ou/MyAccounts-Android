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

package com.acmpo6ou.myaccounts.database.databases_list

import android.app.Activity
import android.content.Intent
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.ListFragment
import com.acmpo6ou.myaccounts.core.utils.startDatabaseUtil
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionEditDatabase
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionOpenDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment representing a list of Databases.
 */
@AndroidEntryPoint
class DatabasesFragment : ListFragment(), DatabaseFragmentI {
    @Inject
    lateinit var app: MyApp

    @Inject
    override lateinit var adapter: DatabasesAdapter

    @Inject
    override lateinit var presenter: DatabasesPresenterI

    val EXPORT_RC = 101
    override val actionCreateItem = R.id.actionCreateDatabase
    override val items get() = app.databases

    /**
     * Used to display export dialog so that user can choose location where to export database.
     *
     * Starts intent with [EXPORT_RC] request code.
     * Shows dialog to choose location using Storage Access Framework.
     *
     * @param[i] index of database we want to export, used to get database name that will be
     * default in export dialog.
     */
    override fun exportDialog(i: Int) {
        val name = items[i].name
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/x-tar"
            putExtra(Intent.EXTRA_TITLE, "$name.tar")
        }
        startActivityForResult(intent, EXPORT_RC)
    }

    /**
     * Displays a dialog for user to confirm deletion of database.
     *
     * If user is choosing No – we will do nothing, if Yes – delete database.
     * @param[i] - database index.
     */
    override fun confirmDelete(i: Int) {
        val name = items[i].name
        val message = resources.getString(R.string.confirm_delete, name)
        confirmDialog(message) { presenter.deleteDatabase(i) }
    }

    /**
     * Displays a dialog for user to confirm closing of database.
     *
     * If user is choosing No – we will do nothing, if Yes – close database.
     * @param[i] - database index.
     */
    override fun confirmClose(i: Int) {
        val name = items[i].name
        val message = resources.getString(R.string.confirm_close, name)
        confirmDialog(message) { presenter.closeDatabase(i) }
    }

    /**
     * Navigates to EditDatabaseFragment passing database index.
     * @param[i] index of database we want to edit.
     */
    override fun navigateToEdit(i: Int) {
        val action = actionEditDatabase(i)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Navigates to OpenDatabaseFragment passing database index.
     * @param[i] index of database we want to open.
     */
    override fun navigateToOpen(i: Int) {
        val action = actionOpenDatabase(i)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Used to display dialog saying that the error occurred.
     *
     * @param[title] title of error dialog.
     * @param[details] details about the error.
     */
    override fun showError(title: String, details: String) {
        val mainActivity = activity as MainActivity
        mainActivity.showError(title, details)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == EXPORT_RC) presenter.exportDatabase(data?.data!!)
    }

    override fun startDatabase(index: Int) = startDatabaseUtil(index, requireContext())
}
