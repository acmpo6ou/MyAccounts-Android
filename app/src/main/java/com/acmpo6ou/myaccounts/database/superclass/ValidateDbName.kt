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

package com.acmpo6ou.myaccounts.database.superclass

import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.MyApp

interface ValidateDbName {
    val app: MyApp
    val databaseIndex: Int

    val emptyNameErr: MutableLiveData<Boolean>
    val existsNameErr: MutableLiveData<Boolean>

    fun fixName(name: String): String
    fun superValidateName(name: String)

    /**
     * Validates given name, checks whether it's not empty and whether database
     * with such name already exists, but it's okay if name doesn't change through editing.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If database with such name already exists [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    fun validateName(name: String) {
        val oldName = app.databases[databaseIndex].name
        val newName = fixName(name)

        // it's okay if name didn't change through editing
        if (oldName == newName) {
            existsNameErr.value = false
            emptyNameErr.value = false
        } else {
            superValidateName(newName)
        }
    }
}
