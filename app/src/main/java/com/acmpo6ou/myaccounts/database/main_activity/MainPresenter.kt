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

package com.acmpo6ou.myaccounts.database.main_activity

import android.content.SharedPreferences
import android.net.Uri
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import com.acmpo6ou.myaccounts.database.databases_list.Database
import dagger.Lazy
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

@ActivityScoped
open class MainPresenter @Inject constructor(
    private val activity: Lazy<MainActivityI>,
    var model: MainModelI,
    private val app: MyApp,
    private val prefs: SharedPreferences,
) : SuperPresenter(), MainPresenterI {

    var databases by app::databases
    override val view: MainActivityI get() = activity.get()

    init {
        // This methods are called on app startup
        fixSrcFolder()
        autocheckForUpdates()
    }

    /**
     * Checks for updates if it's time to.
     */
    fun autocheckForUpdates() {
        if (isTimeToUpdate()) checkUpdatesSelected(true)
    }

    /**
     * Creates src folder if it doesn't exist.
     */
    fun fixSrcFolder() = File(app.SRC_DIR).mkdirs()

    /**
     * This method checks whether it's time to check for updates.
     * Application should check for updates only once a day.
     *
     * @return boolean value indicating whether it is time to check for updates.
     */
    override fun isTimeToUpdate(): Boolean {
        // get last time we checked for updates
        val lastCheck = LocalDate.ofEpochDay(
            prefs.getLong("last_update_check", LocalDate.MIN.toEpochDay())
        )

        // now last time when we checked for updates is today
        prefs.edit()
            .putLong("last_update_check", LocalDate.now().toEpochDay())
            .apply()
        return lastCheck < LocalDate.now()
    }

    /**
     * Called when user clicks `Import database` in navigation drawer.
     */
    override fun importSelected() = view.importDialog()

    /**
     * Checks given .dba file on validity.
     *
     * It checks whether the file has appropriate size.
     * @param[location] uri containing .dba file that we need to check.
     */
    override fun checkDbaFile(location: Uri) {
        val fileSize = model.getSize(location)

        if (fileSize < 116) {
            val errorTitle = app.res.getString(R.string.import_error_title)
            val importSizeMsg = app.res.getString(R.string.import_dba_size, fileSize)
            view.showError(errorTitle, importSizeMsg)
        } else {
            importDatabase(location)
        }
    }

    /**
     * Calls model.importDatabase() handling all errors.
     *
     * After import, it adds database to the list which then sorts and
     * notifies about changes.
     * If there are any errors during this process it displays error dialog.
     */
    open fun importDatabase(location: Uri) {
        var errorDetails: String? = null

        try {
            // add Database and sort the databases list
            val name = model.importDatabase(location)
            val db = Database(name)
            databases.add(db)
            databases.sortBy { it.name }

            // notify about changes in the list
            val i = databases.indexOf(db)
            view.notifyChanged(i)
        } catch (e: FileAlreadyExistsException) {
            e.printStackTrace()
            errorDetails = app.res.getString(R.string.db_exists)
        } catch (e: IOException) {
            e.printStackTrace()
            errorDetails = app.res.getString(R.string.io_error)
        } catch (e: Exception) {
            e.printStackTrace()
            errorDetails = e.toString()
        }

        // if there are any errors display error dialog
        if (errorDetails != null) {
            val errorTitle = app.res.getString(R.string.import_error_title)
            view.showError(errorTitle, errorDetails)
        }
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
        val openedDatabases = databases.filter { it.isOpen }
        val unsavedDatabases = openedDatabases.filter { !model.isDatabaseSaved(it) }

        if (unsavedDatabases.isNotEmpty()) {
            view.confirmBack()
        } else if (openedDatabases.isNotEmpty() &&
            view.lastBackPressTime < System.currentTimeMillis() - 4000
        ) {
            view.lastBackPressTime = System.currentTimeMillis()
            view.showExitTip()
        } else {
            view.goBack()
        }
    }

    /**
     * Called when user chose `Save` in confirm going back dialog.
     * Saves all unsaved databases.
     */
    override fun saveSelected() =
        databases
            .filter { it.isOpen }
            .filter { !model.isDatabaseSaved(it) }
            .forEach { model.saveDatabase(it.name, it) }
}
