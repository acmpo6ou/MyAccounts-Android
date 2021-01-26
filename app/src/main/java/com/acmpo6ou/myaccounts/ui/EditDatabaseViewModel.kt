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
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.CreateEditViewModel
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.createDatabaseUtil
import com.acmpo6ou.myaccounts.core.deleteDatabaseUtil
import kotlinx.coroutines.async

open class EditDatabaseViewModel : CreateEditViewModel() {
    /**
     * This method simply deletes old database (which is determined by [oldName]) and
     * creates new one using [database], to more specifically say: it replaces old database
     * with a new one.
     *
     * @param[oldName] name of the old database that is to be replaced.
     * @param[database] new Database to be created, replacing the old one.
     */
    fun saveDatabase(oldName: String, database: Database, app: MyApp) =
    viewModelScope.async (defaultDispatcher) {
        deleteDatabaseUtil(oldName, SRC_DIR)
        createDatabaseUtil(database, SRC_DIR, app)
    }

    override suspend fun apply(name: String, password: String) {
        TODO("Not yet implemented")
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
        // it's okay if name didn't change through editing
        val dbName = databases[databaseIndex].name
        if(dbName == name){
            existsNameErr = false
        }
        else{
            super.validateName(name)
        }
    }
}