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

package com.acmpo6ou.myaccounts.database.create_edit_database

import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.superclass.CreateEditDatabaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
open class EditDatabaseViewModel(
    override val app: MyApp,
    private val defaultDispatcher: CoroutineDispatcher,
    override val uiDispatcher: CoroutineDispatcher,
) : CreateEditDatabaseModel() {

    @Inject
    constructor(app: MyApp) : this(app, Dispatchers.Default, Dispatchers.Main)

    open fun saveDatabaseAsync(oldName: String, database: Database) =
        viewModelScope.async(defaultDispatcher) {
            saveDatabase(oldName, database)
        }

    /**
     * This method saves new Database using [saveDatabaseAsync].
     * If any error occurred it sets errorMsg to error message.
     *
     * @param[name] name for the database.
     * @param[password] password for the database.
     */
    override suspend fun apply(name: String, password: String) {
        try {
            // display loading progress bar
            loading.value = true

            // save database
            val cleanedName = fixName(name)
            val oldDatabase = app.databases[databaseIndex]
            val newDatabase = Database(
                cleanedName, password,
                oldDatabase.salt, oldDatabase.data
            )
            saveDatabaseAsync(oldDatabase.name, newDatabase).await()

            // if password has changed remove old cryptography key from cache
            if (oldDatabase.password != password) {
                app.keyCache.remove(oldDatabase.password)
            }

            // add database to the list, sort the list and notify about creation
            app.databases[databaseIndex] = newDatabase
            app.databases.sortBy { it.name }
            finished.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            // notify about error and hide loading progress bar
            errorMsg.value = e.toString()
            loading.value = false
        }
    }

    /**
     * This method validates given name, checks whether it's not empty and whether database
     * with such name already exists, but it's okay if name doesn't change through editing.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If database with such name already exists [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    override fun validateName(name: String) {
        val oldName = app.databases[databaseIndex].name
        val newName = fixName(name)

        // it's okay if name didn't change through editing
        if (oldName == newName) {
            existsNameErr.value = false
            emptyNameErr.value = false
        } else {
            super.validateName(newName)
        }
    }
}
