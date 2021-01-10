/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.core

import android.net.Uri
import com.acmpo6ou.myaccounts.R
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Contains various methods for business logic of DatabaseFragment.
 */
open class DatabasesPresenter(private val view: DatabaseFragmentInter)
    : DatabasesPresenterInter {
    var model: DatabasesModelInter = DatabasesModel(view.ACCOUNTS_DIR, view.myContext.contentResolver)
    var exportIndex: Int? = null

    override var databases: MutableList<Database>
        get() = view.app.databases
        set(value) {
            view.app.databases = value
        }

    init {
        databases = model.getDatabases()
    }

    /**
     * Called when user selects `Export` in database item popup menu.
     *
     * Should save [i] in [exportIndex] as it will be used by exportDatabase to determine
     * what database to export.
     * Also it calls exportDialog to display export dialog where user can chose export
     * location.
     * @param[i] index of database we want to export.
     */
    override fun exportSelected(i: Int) {
        exportIndex = i
        view.exportDialog(i)
    }

    /**
     * Used to export database to user defined location.
     *
     * Calls model.exportDatabase() in try-catch block handling all errors. When error
     * occurred calls view.showError() passing through appropriate error details to display
     * dialog about error.
     * If there are no errors - displays snackbar with success message.
     */
    override fun exportDatabase(locationUri: Uri) {
        val resources = view.myContext?.resources
        var errorDetails = ""

        try {
            // export database
            exportIndex?.let {
                val name = databases[it].name
                model.exportDatabase(name, locationUri)
            }
            // if there are no errors display snackbar about success
            view.showSuccess()
        }
        // handle all possible errors
        catch (e: FileNotFoundException){
            errorDetails = resources.getString(R.string.export_file_not_found_details)
            e.printStackTrace()
        }
        catch (e: IOException){
            errorDetails = resources.getString(R.string.io_error)
            e.printStackTrace()
        }
        catch (e: Exception){
            errorDetails = "${e.javaClass.name} ${e.message}"
            e.printStackTrace()
        }

        // if there are any errors errorDetails will be filled with appropriate details string
        // if so, display error dialog
        if(errorDetails.isNotEmpty()){
            val errorTitle = resources.getString(R.string.export_error_title)
            view.showError(errorTitle, errorDetails)
        }
    }

    /**
     * Called when user selects `Delete` in database item popup menu.
     *
     * Calls confirmDelete to display a dialog about confirmation of database deletion.
     * @param[i] index of database we want to delete.
     */
    override fun deleteSelected(i: Int) {
        view.confirmDelete(i)
    }

    /**
     * Calls model.deleteDatabase() in try-catch block handling all errors.
     *
     * @param[i] database index.
     */
    override fun deleteDatabase(i: Int) {
        try {
            model.deleteDatabase(databases[i].name)
            databases.removeAt(i)
            view.notifyRemoved(i)
        }
        catch (e: Exception){
            val errorTitle = view.myContext.resources
                    .getString(R.string.delete_error_title)
            val errorDetails = "${e.javaClass.name} ${e.message}"
            view.showError(errorTitle, errorDetails)
            e.printStackTrace()
        }
    }

    /**
     * Called when user selects `Close` in database item popup menu.
     *
     * Checks whether database we want to close is saved, if it is –
     * calls closeDatabase to close the database, if it's not – calls confirmClose to ask
     * user for confirmation.
     * @param[i] index of database we want to close.
     */
    override fun closeSelected(i: Int) {
        if(isDatabaseSaved(i)) {
            closeDatabase(i)
        }
        else{
            view.confirmClose(i)
        }
    }

    /**
     * Called when user selects `Edit` in database item popup menu.
     *
     * Using navigateToEdit navigates to EditDatabaseFragment passing through database index.
     * @param[i] index of database we want to edit.
     */
    override fun editSelected(i: Int) {
        view.navigateToEdit(i)
    }

    /**
     * Used to reset database password in this way 'closing' it.
     *
     * @param[i] - database index.
     */
    override fun closeDatabase(i: Int) {
        databases[i].password = null
        view.notifyChanged(i)
    }

    /**
     * Called when user selects item in database list.
     *
     * If selected database is closed should navigate to OpenDatabaseFragment, if it
     * is open - call view.startDatabase passing through database index.
     */
    override fun openDatabase(i: Int) {
        if(databases[i].isOpen){
            view.startDatabase(i)
        }
        else {
            view.navigateToOpen(i)
        }
    }

    /**
     * Checks whether given database is saved i.e. the database data that is on the disk
     * is same as the data that is in memory.
     *
     * This method is needed when we want to close database, using isDatabaseSaved we
     * can determine whether it's better to show confirmation dialog about
     * unsaved data to user or not.
     * @param[i] index of database we want to check.
     */
    override fun isDatabaseSaved(i: Int): Boolean{
        val actualDatabase = databases[i]
        val diskDatabase: Database?
        try {
            diskDatabase = model.openDatabase(actualDatabase)
        }
        catch (e: FileNotFoundException){
            // if database on disk doesn't exist then it definitely
            // differs from the one in memory
            e.printStackTrace()
            return false
        }
        return actualDatabase.data == diskDatabase.data
    }
}