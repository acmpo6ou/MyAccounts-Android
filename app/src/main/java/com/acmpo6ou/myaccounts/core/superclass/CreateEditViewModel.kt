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

package com.acmpo6ou.myaccounts.core.superclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.utils.combineWith

/**
 * Super class for all view models that create/edit items.
 */
abstract class CreateEditViewModel : ViewModel(), ErrorViewModel {
    abstract val itemNames: List<String>
    abstract val app: MyApp
    val finished = MutableLiveData<Boolean>()

    val emptyNameErr = MutableLiveData(true)
    val existsNameErr = MutableLiveData(false)

    val emptyPassErr = MutableLiveData(true)
    val diffPassErr = MutableLiveData(false)

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
     * This LiveData property provides error message according
     * to emptyPassErr and diffPassErr live data values.
     */
    val passwordErrors = emptyPassErr.combineWith(diffPassErr) {
        empty: Boolean?, different: Boolean? ->

        var msg: String? = null
        if (empty!!) {
            msg = app.res.getString(R.string.empty_password)
        } else if (different!!) {
            msg = app.res.getString(R.string.diff_passwords)
        }
        return@combineWith msg
    }

    /**
     * This LiveData property used to decide whether apply button should be enabled
     * or not. If there are any errors it should be disabled, if there are no - enabled.
     */
    val applyEnabled = nameErrors.combineWith(passwordErrors) {
        nameErr: String?, passwordErr: String? ->
        nameErr == null && passwordErr == null
    }

    /**
     * By default there will be no logic to clean the name, subclasses can override fixName
     * if they need to clean [name].
     */
    open fun fixName(name: String): String = name

    /**
     * This method validates given name, checks whether it's not empty and whether
     * it's taken yet.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If name is taken [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    open fun validateName(name: String) {
        val cleanedName = fixName(name)
        emptyNameErr.value = cleanedName.isEmpty()
        existsNameErr.value = cleanedName in itemNames
    }

    /**
     * This method validates given passwords.
     *
     * If passwords are empty [emptyPassErr] is true.
     * If passwords don't match [diffPassErr] is true.
     *
     * @param[pass1] first password.
     * @param[pass2] second password.
     */
    open fun validatePasswords(pass1: String, pass2: String) {
        emptyPassErr.value = pass1.isEmpty()
        diffPassErr.value = pass1 != pass2
    }
}
