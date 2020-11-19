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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.DatabasesAdapterInter
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_database_list.*

/**
 * A fragment representing a list of Databases.
 */
class DatabaseFragment() : Fragment(), DatabaseFragmentInter {

    val EXPORT_RC = 101
    val layoutManager = LinearLayoutManager(context)
    override lateinit var adapter: DatabasesAdapterInter
    lateinit var presenter: DatabasesPresenterInter
    lateinit var myContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = DatabasesAdapter()
        presenter = DatabasesPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_database_list, container, false)

        // Set the adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // when clicking on (+) FAB navigate to CreateDatabaseFragment
        addDatabase.setOnClickListener{
            view.findNavController().navigate(R.id.createDatabaseFragment)
        }
    }

    /**
     * Used to display export dialog where user can chose location where to export database.
     *
     * Starts intent with export request code. Shows dialog to chose location using Storage
     * Access framework.
     * @param[name] name of the database to export, used as default name of exported tar file.
     */
    override fun exportDialog(name: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/x-tar"
        intent.putExtra(Intent.EXTRA_TITLE, "$name.tar")
        startActivityForResult(intent, EXPORT_RC)
    }

    override fun confirmDelete(name: String) {
         MaterialAlertDialogBuilder(myContext)
                .setTitle(R.string.warning)
                .setMessage("Are you sure you want to delete database $name?")
                .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                .setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                    presenter.deleteDatabase(name)
                }
                .show()
    }

    /**
     * Used to display a snackbar with success message.
     */
    override fun showSuccess() {
        Snackbar.make(
            databaseCoordinator,
            R.string.success_message,
            Snackbar.LENGTH_LONG).show()
    }

    override fun showError(details: String) {
        MaterialAlertDialogBuilder(myContext)
                .setTitle(R.string.error)
                .setNeutralButton("Ok"){ _: DialogInterface, _: Int -> }
                .setMessage(details)
                .show()
    }

    /**
     * Handles various dialog results.
     *
     * @param[requestCode] the code of operation being handled.
     * @param[resultCode] represents whether the operation was actually performed or canceled.
     * @param[data] data needed for operation handling.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // if activity was canceled don't do anything
        if (resultCode != Activity.RESULT_OK){
            return
        }

        when(requestCode) {
            // if the result is from export database dialog call appropriate method
            EXPORT_RC -> presenter.exportDatabase(data?.data.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DatabaseFragment()
    }
}