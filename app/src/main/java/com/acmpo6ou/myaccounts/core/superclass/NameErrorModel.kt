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

package com.acmpo6ou.myaccounts.core.superclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.utils.combineWith

abstract class NameErrorModel : ViewModel() {
    abstract val itemNames: List<String>
    abstract val app: MyApp

    val emptyNameErr = MutableLiveData(true)
    val existsNameErr = MutableLiveData(false)

    /**
     * This LiveData property provides error message according
     * to emptyNameErr and existsNameErr live data values.
     */
    val nameErrors = emptyNameErr.combineWith(existsNameErr) {
        empty: Boolean?, exists: Boolean? ->

        var msg: String? = null
        if (empty!!) {
            msg = app.res.getString(R.string.name_empty)
        } else if (exists!!) {
            msg = app.res.getString(R.string.name_exists)
        }
        return@combineWith msg
    }

    /**
     * By default there will be no logic to clean the name, subclasses can override fixName
     * if they need to clean [name].
     */
    open fun fixName(name: String) = name

    /**
     * Validates given name, checks whether it's not empty and whether
     * it's taken yet.
     * @param[name] name to validate.
     */
    open fun validateName(name: String) {
        val cleanedName = fixName(name)
        emptyNameErr.value = cleanedName.isEmpty()
        existsNameErr.value = cleanedName in itemNames
    }
}
