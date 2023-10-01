/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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
open class CreateDatabaseViewModel(
    override val app: MyApp,
    private val defaultDispatcher: CoroutineDispatcher,
    override val uiDispatcher: CoroutineDispatcher,
) : CreateEditDatabaseModel() {

    @Inject
    constructor(app: MyApp) : this(app, Dispatchers.Default, Dispatchers.Main)

    open fun createDatabaseAsync(database: Database) =
        viewModelScope.async(defaultDispatcher) {
            createDatabase(database)
        }

    /**
     * Creates database using [createDatabaseAsync], given [name] and [password].
     *
     * Once the database is created it is added to the list.
     * If any error occurred it sets errorMsg to error message.
     *
     * @param[name] database name.
     * @param[password] database password.
     */
    override suspend fun apply(name: String, password: String) {
        try {
            // display loading progress bar
            loading.value = true

            // create database
            val cleanedName = fixName(name)
            val salt = generateSalt()
            val database = Database(cleanedName, password, salt)
            createDatabaseAsync(database).await()

            // add it to the list, sort the list and notify about creation
            app.databases.add(database)
            app.databases.sortBy { it.name }
            finished.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            // notify about error and hide loading progress bar
            errorMsg.value = e.toString()
            loading.value = false
        }
    }
}
