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

package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.core.CreateEditViewModel
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.createDatabaseUtil
import kotlinx.coroutines.async

open class CreateDatabaseViewModel : CreateEditViewModel() {
    open fun createDatabaseAsync(database: Database) =
        viewModelScope.async(defaultDispatcher){
            createDatabaseUtil(database, SRC_DIR, app)
        }

    /**
     * This method creates database using [createDatabaseAsync] and given [name] and [password].
     *
     * Once the database is created it is added to the list.
     * If any error occurred it sets errorMsg to error message.
     * @param[name] name for the database.
     * @param[password] password for the database.
     */
    override suspend fun createDatabase(name: String, password: String){
        try {
            // display loading progress bar
            loading = true

            // create database
            val salt = generateSalt()
            val database = Database(name, password, salt)
            createDatabaseAsync(database).await()

            // add it to the list, sort the list and notify about creation
            databases.add(database)
            databases.sortBy { it.name }
            created = true
        }
        catch (e: Exception){
            // notify about error and hide loading progress bar
            errorMsg = e.toString()
            loading = false
            e.printStackTrace()
        }
    }
}
