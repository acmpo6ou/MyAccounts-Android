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

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.ListFragment
import com.acmpo6ou.myaccounts.database.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.database.DatabasesPresenter
import com.acmpo6ou.myaccounts.database.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.database.superclass.SuperFragment
import com.acmpo6ou.myaccounts.databinding.FragmentDatabaseListBinding
import com.acmpo6ou.myaccounts.ui.database.DatabaseFragmentDirections.actionEditDatabase
import com.acmpo6ou.myaccounts.ui.database.DatabaseFragmentDirections.actionOpenDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment representing a list of Databases.
 */
class DatabaseFragment: ListFragment(), DatabaseFragmentInter {
    override lateinit var ACCOUNTS_DIR: String
    val EXPORT_RC = 101

    override lateinit var adapter: DatabasesAdapter
    override lateinit var presenter: DatabasesPresenterInter

    var databases
        get() = app.databases
        set(value){
            app.databases = value
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
        app = context.applicationContext as MyApp
        ACCOUNTS_DIR = context.getExternalFilesDir(null)!!.path + "/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = DatabasesAdapter(this)
        presenter = DatabasesPresenter(this)
    }

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
        val name = databases[i].name
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/x-tar"
            putExtra(Intent.EXTRA_TITLE, "$name.tar")
        }
        startActivityForResult(intent, EXPORT_RC)
    }

    /**
     * Used to build and display confirmation dialog.
     *
     * @param[message] message to describe what we asking user to confirm.
     * @param[positiveAction] function to invoke when user confirms an action (i.e. presses
     * the `Yes` button).
     */
    private inline fun confirmDialog(message: String, crossinline positiveAction: ()->Unit) {
        MaterialAlertDialogBuilder(myContext)
                .setTitle(R.string.warning)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                .setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                    positiveAction()
                }
                .show()
    }

    /**
     * Displays a dialog for user to confirm deletion of database.
     *
     * If user is choosing No – we will do nothing, if Yes – delete database.
     * @param[i] - database index.
     */
    override fun confirmDelete(i: Int) {
        val name = databases[i].name
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
        val name = databases[i].name
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
     * Used to display a snackbar with success message.
     */
    override fun showSuccess() {
        Snackbar.make(b.coordinatorLayout,
                      R.string.success_message,
                      Snackbar.LENGTH_LONG)
                     .setAction("HIDE"){}
                     .show()
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
        // do nothing if activity was canceled
        if (resultCode != Activity.RESULT_OK) return

        if(requestCode == EXPORT_RC) presenter.exportDatabase(data?.data!!)
    }

    /**
     * This method rerenders list of databases after any database have changed.
     * @param[i] index of database that have changed.
     */
    override fun notifyChanged(i: Int) {
        adapter.notifyItemChanged(i)
        adapter.notifyItemRangeChanged(i, 1)
        checkListPlaceholder()
    }

    /**
     * This method rerenders list of databases after any database have been deleted.
     * @param[i] index of database that have been deleted.
     */
    override fun notifyRemoved(i: Int) {
        adapter.notifyItemRemoved(i)
        adapter.notifyItemRangeRemoved(i, 1)
        checkListPlaceholder()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DatabaseFragment()
    }
}