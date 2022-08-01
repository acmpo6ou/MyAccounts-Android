/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

import android.net.Uri
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import dagger.Lazy
import dagger.hilt.android.scopes.FragmentScoped
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@FragmentScoped
open class DatabasesPresenter @Inject constructor(
    private val fragment: Lazy<DatabasesFragmentI>,
    private val model: DatabasesModelI,
    private val app: MyApp,
) : DatabasesPresenterI {

    val view: DatabasesFragmentI get() = fragment.get()
    override var databases by app::databases

    // used by exportDatabase to determine what database to export.
    var databaseToExport: Database? = null

    init {
        databases = model.getDatabases()
    }

    override fun exportSelected(database: Database) {
        databaseToExport = database
        view.exportDialog(database)
    }

    override fun exportDatabase(locationUri: Uri) {
        var errorDetails: String? = null

        try {
            databaseToExport?.let {
                model.exportDatabase(it.name, locationUri)
            }
            // if there are no errors display snackbar about success
            view.showSuccess()
        }
        // handle all possible errors
        catch (e: FileNotFoundException) {
            e.printStackTrace()
            errorDetails = app.res.getString(R.string.export_file_not_found_details)
        } catch (e: IOException) {
            e.printStackTrace()
            errorDetails = app.res.getString(R.string.io_error)
        } catch (e: Exception) {
            e.printStackTrace()
            errorDetails = e.toString()
        }

        // if there are any errors errorDetails will be filled with appropriate details string
        // if so, display error dialog
        if (errorDetails != null) {
            val errorTitle = app.res.getString(R.string.export_error_title)
            view.showError(errorTitle, errorDetails)
        }
    }

    override fun deleteSelected(database: Database) =
        view.confirmDelete(database)

    override fun deleteDatabase(database: Database) {
        try {
            // remove cryptography key of database from cache
            app.keyCache.remove(database.password)

            // remove database from disk
            model.deleteDatabase(database.name)

            val i = databases.indexOf(database)
            databases.remove(database)
            view.notifyRemoved(i)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorTitle = app.res.getString(R.string.delete_error_title)
            view.showError(errorTitle, e.toString())
        }
    }

    override fun editSelected(database: Database) {
        val index = databases.indexOf(database)
        if (database.isOpen)
            view.navigateToEdit(index)
        else
            view.navigateToRename(index)
    }

    override fun closeSelected(database: Database) {
        if (model.isDatabaseSaved(database))
            closeDatabase(database)
        else
            view.confirmClose(database)
    }

    /**
     * Resets database password effectively "closing" the database.
     * It also removes database's cryptography key from cache.
     */
    override fun closeDatabase(database: Database) {
        app.keyCache.remove(database.password)
        database.password = null
        val i = databases.indexOf(database)
        view.notifyChanged(i)
    }

    /**
     * Called when user selects item in database list.
     *
     * If selected database is closed should navigate to OpenDatabaseFragment, if it
     * is opened - start AccountsActivity.
     */
    override fun openDatabase(database: Database) {
        val index = databases.indexOf(database)
        if (database.isOpen)
            view.startDatabase(index)
        else
            view.navigateToOpen(index)
    }
}
