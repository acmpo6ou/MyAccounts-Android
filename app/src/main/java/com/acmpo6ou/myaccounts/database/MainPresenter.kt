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

package com.acmpo6ou.myaccounts.database

import android.net.Uri
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import java.io.File
import java.io.IOException
import java.time.LocalDate

open class MainPresenter(override var view: MainActivityInter) : SuperPresenter(),
                                                                 MainPresenterInter {
    var model: MainModelInter = MainModel(view.ACCOUNTS_DIR,
                                          view.myContext.contentResolver)
    var databases: DbList
        get() = view.app.databases
        set(value) {
            view.app.databases = value
        }

    init{
        // This methods are called on app startup
        fixSrcFolder()
        autocheckForUpdates()
    }

    /**
     * This method is called on app startup, it checks for updates if it's time to.
     */
    fun autocheckForUpdates() {
        if(isTimeToUpdate()) checkUpdatesSelected()
    }

    /**
     * This method is called on app startup, if src folder doesn't exist method will create it.
     */
    fun fixSrcFolder(){
        val srcDir = File("${view.ACCOUNTS_DIR}/src")
        if(!srcDir.exists()) srcDir.mkdirs()
    }

    /**
     * This method is called when user clicks `Import database` in navigation drawer.
     */
    override fun importSelected() = view.importDialog()

    /**
     * This method checks given tar file on validity.
     *
     * It checks whether the tar file has appropriate files, does it have appropriate
     * number of them and so on.
     * @param[location] uri containing tar file that we need to check.
     */
    override fun checkTarFile(location: Uri) {
        // get everything we need (file names and sizes etc.)
        val resources = view.myContext?.resources
        var errorDetails = ""

        val fileCount = model.countFiles(location)
        val fileNames = model.getNames(location)
        val fileSizes = model.getSizes(location)

        // check that there are only 2 files
        if(fileCount != 2) {
            errorDetails = resources.getString(R.string.import_2_files)
        }
        // check that files have the same name
        else if(fileNames[0] != fileNames[1]){
            errorDetails = resources.getString(
                    R.string.import_diff_names, fileNames[0], fileNames[1])
        }
        // check that .bin file has exactly 16 bytes of salt in it
        else if(fileSizes[1] != 16){
            errorDetails = resources.getString(R.string.import_bin_size, fileSizes[1])
        }
        // check that .db file has at least 100 bytes in it
        else if(fileSizes[0] < 100){
            errorDetails = resources.getString(R.string.import_db_size, fileSizes[0])
        }
        // if there are no errors - call importDatabase
        else{
            importDatabase(location)
        }

        // if there are any errors display error dialog
        if(errorDetails.isNotEmpty()){
            val errorTitle = resources.getString(R.string.import_error_title)
            view.showError(errorTitle, errorDetails)
        }
    }

    /**
     * This method calls model.importDatabase() handling all errors.
     *
     * After import, it adds database to the list which then sorts and
     * notifies about changes.
     * If there are any errors during this process it displays error dialog.
     */
    open fun importDatabase(location: Uri){
        val resources = view.myContext?.resources
        var errorDetails = ""

        try {
            // add Database and sort the databases list
            val name = model.importDatabase(location)
            val db = Database(name)
            databases.add(db)
            databases.sortBy { it.name }

            // notify about changes in the list
            val i = databases.indexOf(db)
            view.notifyChanged(i)
        }
        catch (e: FileAlreadyExistsException){
            errorDetails = resources.getString(R.string.db_exists)
            e.printStackTrace()
        }
        catch (e: IOException){
            errorDetails = resources.getString(R.string.io_error)
            e.printStackTrace()
        }
        catch (e: Exception){
            errorDetails = e.toString()
            e.printStackTrace()
        }

        // if there are any errors display error dialog
        if(errorDetails.isNotEmpty()){
            val errorTitle = resources.getString(R.string.import_error_title)
            view.showError(errorTitle, errorDetails)
        }
    }

    /**
     * This method checks whether it's time to check for updates.
     * Application should check for updates only once a day.
     *
     * @return boolean value indicating whether it is time to check for updates.
     */
    override fun isTimeToUpdate(): Boolean {
        if (view.app.testing) return false // do not check for updates during testing

        // get last time we checked for updates
        val lastCheck = LocalDate.ofEpochDay(
            view.prefs.getLong("last_update_check", LocalDate.MIN.toEpochDay()))

        // now last time when we checked for updates is today
        view.prefs.edit()
                .putLong("last_update_check", LocalDate.now().toEpochDay())
                .apply()
        return lastCheck < LocalDate.now()
    }

    /**
     * Called when user presses the back button.
     *
     * Here we decide whether to show a confirmation dialog or snackbar about unsaved
     * changes or not.
     * If all databases are closed – just go back (exiting the app), if there are opened
     * databases that are saved – display snackbar, if there are opened databases that aren't
     * saved - display confirmation dialog.
     */
    override fun backPressed() {
        val openedDatabases = databases.filter{ it.isOpen }
        val unsavedDatabases = openedDatabases.filter { !model.isDatabaseSaved(it, view.app) }

        if (unsavedDatabases.isNotEmpty()) {
            view.confirmBack()
        }
        else if (openedDatabases.isNotEmpty() &&
                 view.lastBackPressTime < System.currentTimeMillis() - 4000) {
            view.lastBackPressTime = System.currentTimeMillis()
            view.showExitTip()
        }
        else {
            view.goBack()
        }
    }

    /**
     * Called when user chose `Save` in confirm going back dialog.
     * Saves all unsaved databases.
     */
    override fun saveSelected() {
        databases.filter { it.isOpen }
                 .filter { !model.isDatabaseSaved(it, view.app) }
                 .forEach { model.saveDatabase(it.name, it, view.app) }
    }
}