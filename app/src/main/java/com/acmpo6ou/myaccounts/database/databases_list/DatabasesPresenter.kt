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
    var exportIndex: Int? = null
    override var databases by app::databases

    init {
        databases = model.getDatabases()
    }

    /**
     * Called when user selects `Export` in database item popup menu.
     *
     * Should save [i] in [exportIndex] as it will be used by [exportDatabase] to determine
     * what database to export.
     * Also it calls exportDialog to display dialog where user can choose export location.
     * @param[i] index of database we want to export.
     */
    override fun exportSelected(i: Int) {
        exportIndex = i
        view.exportDialog(i)
    }

    /**
     * Used to export database to user defined location.
     *
     * Calls model.exportDatabase() in try-catch block handling all errors.
     * When error occurred calls view.showError() passing through appropriate error
     * details to display dialog about error.
     * If there are no errors - displays snackbar with success message.
     * @param[locationUri] uri containing path where to export database.
     */
    override fun exportDatabase(locationUri: Uri) {
        var errorDetails: String? = null

        try {
            exportIndex?.let {
                val name = databases[it].name
                model.exportDatabase(name, locationUri)
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

    /**
     * Called when user selects `Delete` in database item popup menu.
     *
     * Calls confirmDelete to display a dialog about confirmation of database deletion.
     * @param[i] index of database we want to delete.
     */
    override fun deleteSelected(i: Int) = view.confirmDelete(i)

    /**
     * Calls model.deleteDatabase() in try-catch block handling all errors.
     * @param[i] database index.
     */
    override fun deleteDatabase(i: Int) {
        try {
            // remove cryptography key of database from cache
            app.keyCache.remove(databases[i].password)

            // remove database from disk
            model.deleteDatabase(databases[i].name)

            databases.removeAt(i)
            view.notifyRemoved(i)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorTitle = app.res.getString(R.string.delete_error_title)
            view.showError(errorTitle, e.toString())
        }
    }

    /**
     * Called when user selects `Edit` in database item popup menu.
     *
     * Using navigateToEdit navigates to EditDatabaseFragment passing through database index.
     * @param[i] index of database we want to edit.
     */
    override fun editSelected(i: Int) = view.navigateToEdit(i)

    /**
     * Called when user selects `Close` in database item popup menu.
     *
     * Checks whether database we want to close is saved, if it is –
     * calls closeDatabase to close the database, if it's not – calls confirmClose to ask
     * user for confirmation.
     * @param[i] index of database we want to close.
     */
    override fun closeSelected(i: Int) {
        if (model.isDatabaseSaved(databases[i], app)) {
            closeDatabase(i)
        } else {
            view.confirmClose(i)
        }
    }

    /**
     * Used to reset database password in this way "closing" the database.
     * It also removes cryptography key of database from cache.
     * @param[i] - database index.
     */
    override fun closeDatabase(i: Int) {
        app.keyCache.remove(databases[i].password)
        databases[i].password = null
        view.notifyChanged(i)
    }

    /**
     * Called when user selects item in database list.
     *
     * If selected database is closed should navigate to OpenDatabaseFragment, if it
     * is opened - call view.startDatabase() passing through database index.
     */
    override fun openDatabase(i: Int) {
        if (databases[i].isOpen) {
            view.startDatabase(i)
        } else {
            view.navigateToOpen(i)
        }
    }
}
