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
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.utils.combineWith

/**
 * Super class for all view models that create/edit items.
 */
abstract class CreateEditViewModel : NameErrorModel(), ErrorViewModel {
    val finished = MutableLiveData<Boolean>()

    val emptyPassErr = MutableLiveData(true)
    val diffPassErr = MutableLiveData(false)

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
