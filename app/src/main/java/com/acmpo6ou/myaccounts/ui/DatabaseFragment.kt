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

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_database_list.*

/**
 * A fragment representing a list of Databases.
 */
class DatabaseFragment: Fragment(), DatabaseFragmentInter {
    override lateinit var ACCOUNTS_DIR: String
    val EXPORT_RC = 101

    override lateinit var adapter: DatabasesAdapter
    override lateinit var presenter: DatabasesPresenterInter

    var databases: MutableList<Database>
        get() = app.databases
        set(value){
            app.databases = value
        }

    override lateinit var myContext: Context
    override lateinit var app: MyApp

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // saving context to use it later
        myContext = context
        app = context.applicationContext as MyApp
        databases = app.databases
        ACCOUNTS_DIR = context.getExternalFilesDir(null)!!.path + "/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initializing recycler's adapter and presenter
        adapter = DatabasesAdapter(this)
        presenter = DatabasesPresenter(this)

        // add some databases for testing
        databases = mutableListOf(
                Database("main"), // locked
                Database("test", password = "123") // opened
        )
        checkListPlaceholder()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_database_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // when clicking on (+) FAB navigate to CreateDatabaseFragment
        addDatabase.setOnClickListener{
            view.findNavController().navigate(R.id.actionCreateDatabase)
        }

        // initializing recycler
        databasesList.layoutManager = LinearLayoutManager(myContext)
        databasesList.adapter = adapter
    }

    /**
     * Used to display export dialog where user can chose location where to export database.
     *
     * Starts intent with export request code. Shows dialog to chose location using Storage
     * Access framework.
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
     * `Yes` button).
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
     * @param[i] - database index
     */
    override fun confirmDelete(i: Int) {
        // get name of the database to delete
        val name = databases[i].name
        val message = resources.getString(R.string.confirm_delete, name)
        confirmDialog(message) { presenter.deleteDatabase(i) }
    }

    /**
     * Displays a dialog for user to confirm closing of database.
     *
     * If user is choosing No – we will do nothing, if Yes – close database.
     * @param[i] - database index
     */
    override fun confirmClose(i: Int) {
        // get name of the database to close
        val name = databases[i].name
        val message = resources.getString(R.string.confirm_close, name)
        confirmDialog(message) { presenter.closeDatabase(i) }
    }

    /**
     * Navigates to EditDatabaseFragment passing database index.
     *
     * @param[i] index of database we want to edit.
     */
    override fun navigateToEdit(i: Int) {
        val action = DatabaseFragmentDirections.actionEditDatabase(i)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Navigates to OpenDatabaseFragment passing database index.
     *
     * @param[i] index of database we want to open.
     */
    override fun navigateToOpen(i: Int) {
        val action = DatabaseFragmentDirections.actionOpenDatabase(i)
        view?.findNavController()?.navigate(action)
    }

    /**
     * Used to display a snackbar with success message.
     */
    override fun showSuccess() {
        Snackbar.make(
            databaseCoordinator,
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
        errorDialog(myContext, title, details)
    }

    /**
     * Used to start AccountsActivity for given database.
     *
     * @param[index] index of database for which we want to start AccountsActivity.
     */
    override fun startDatabase(index: Int) {
        val intent = Intent(myContext, AccountsActivity::class.java)
        intent.putExtra("databaseIndex", index)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // if activity was canceled don't do anything
        if (resultCode != Activity.RESULT_OK){
            return
        }

        if(requestCode == EXPORT_RC) {
            presenter.exportDatabase(data?.data!!)
        }
    }

    /**
     * This method rerenders list of databases after any database have changed.
     *
     * @param[i] index of database that have changed.
     */
    override fun notifyChanged(i: Int) {
        adapter.notifyItemChanged(i)
        adapter.notifyItemRangeChanged(i, 1)
        checkListPlaceholder()
    }

    /**
     * This method rerenders list of databases after any database have been deleted.
     *
     * @param[i] index of database that have been deleted.
     */
    override fun notifyRemoved(i: Int) {
        adapter.notifyItemRemoved(i)
        adapter.notifyItemRangeRemoved(i, 1)
        checkListPlaceholder()
    }

    /**
     * This method decides whether to show recycler view placeholder (tip that is shown when
     * recycler is empty).
     *
     * If there are items in the list it hides placeholder, if there aren't it displays
     * the placeholder.
     */
    fun checkListPlaceholder(){
        if (databases.size == 0) {
            databasesList.visibility = View.GONE
            no_databases.visibility = View.VISIBLE
        }
        else{
            databasesList.visibility = View.VISIBLE
            no_databases.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DatabaseFragment()
    }
}