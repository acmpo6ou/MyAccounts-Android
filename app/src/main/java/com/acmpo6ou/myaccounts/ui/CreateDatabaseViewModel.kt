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

import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.SuperViewModel

class CreateDatabaseViewModel: SuperViewModel() {
    val _emptyNameErr = MutableLiveData(true)
    val _existsNameErr = MutableLiveData(false)

    val _diffPassErr = MutableLiveData(false)
    val _emptyPassErr = MutableLiveData(false)

    var emptyNameErr: Boolean
        get() = _emptyNameErr.value!!
        set(value) {_emptyNameErr.value = value}
    var existsNameErr: Boolean
        get() = _existsNameErr.value!!
        set(value) {_existsNameErr.value = value}

    var diffPassErr: Boolean
        get() = _diffPassErr.value!!
        set(value) {_diffPassErr.value = value}

    var emptyPassErr: Boolean
        get() = _emptyPassErr.value!!
        set(value) {_emptyPassErr.value = value}

    /**
     * This method removes all unsupported characters from given name.
     *
     * Supported characters are lower and upper ASCII letters, digits and .-_()
     * @param[name] name to clean.
     * @return cleaned from unsupported characters name.
     */
    fun fixName(name: String): String{
        val supported = (('A'..'Z') + ('a'..'z') + ('0'..'9'))
                .joinToString("") + ".-_()"
        return name.filter { it in supported }
    }

    /**
     * This method validates given name, checks whether it's not empty and if database
     * with such name already exists.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If database with such name already exists [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    fun validateName(name: String){
        val cleanedName = fixName(name)
        emptyNameErr = cleanedName.isEmpty()
        existsNameErr = Database(cleanedName) in databases
    }

    /**
     * This method validates given passwords.
     *
     * If passwords don't match [diffPassErr] is true.
     * If passwords are empty [emptyPassErr] is true.
     * @param[pass1] first password.
     * @param[pass2] second password.
     */
    fun validatePasswords(pass1: String, pass2: String){
        diffPassErr = pass1 != pass2
        emptyPassErr = pass1.isEmpty()
    }
}