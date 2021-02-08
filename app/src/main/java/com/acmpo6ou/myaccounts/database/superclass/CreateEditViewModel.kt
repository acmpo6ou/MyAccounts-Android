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

package com.acmpo6ou.myaccounts.database.superclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.combineWith
import kotlinx.coroutines.launch
import java.security.SecureRandom

abstract class CreateEditViewModel: SuperViewModel() {
    private val emptyNameErr_ = MutableLiveData(true)
    private val existsNameErr_ = MutableLiveData(false)

    private val emptyPassErr_ = MutableLiveData(true)
    private val diffPassErr_ = MutableLiveData(false)

    var emptyNameErr
        get() = emptyNameErr_.value!!
        set(value) {emptyNameErr_.value = value}
    var existsNameErr
        get() = existsNameErr_.value!!
        set(value) {existsNameErr_.value = value}

    var diffPassErr
        get() = diffPassErr_.value!!
        set(value) {diffPassErr_.value = value}
    var emptyPassErr
        get() = emptyPassErr_.value!!
        set(value) {emptyPassErr_.value = value}

    val _finished = MutableLiveData<Boolean>()
    var finished
        get() = _finished.value!!
        set(value) {_finished.value = value}

    /**
     * This LiveData property provides error message according
     * to emptyNameErr_ and existsNameErr_ live data values.
     */
    val nameErrors = emptyNameErr_.combineWith(existsNameErr_) {
        empty: Boolean?, exists: Boolean? ->

        var msg: String? = null
        if(empty!!){
            msg = app.res.getString(R.string.empty_name)
        }
        else if(exists!!){
            msg = app.res.getString(R.string.db_exists)
        }
        return@combineWith msg
    }

    /**
     * This LiveData property provides error message according
     * to emptyPassErr_ and diffPassErr_ live data values.
     */
    val passwordErrors = emptyPassErr_.combineWith(diffPassErr_) {
        empty: Boolean?, different: Boolean? ->

        var msg: String? = null
        if(empty!!){
            msg = app.res.getString(R.string.empty_password)
        }
        else if(different!!){
            msg = app.res.getString(R.string.diff_passwords)
        }
        return@combineWith msg
    }

    /**
     * This LiveData property used to decide whether `Create` button should be enabled
     * or not. If there are any errors it should be disabled, if there are no - enabled.
     */
    val applyEnabled = nameErrors.combineWith(passwordErrors) {
        nameErr: String?, passwordErr: String? ->
        nameErr == null && passwordErr == null
    }

    /**
     * This method removes all unsupported characters from given name.
     *
     * Supported characters are lower and upper ASCII letters, digits and .-_()
     * @param[name] name to clean.
     * @return cleaned from unsupported characters name.
     */
    fun fixName(name: String): String{
        val supported =
                ( ('A'..'Z') + ('a'..'z') + ('0'..'9') ).joinToString("") + ".-_()"
        return name.filter { it in supported }
    }

    /**
     * This method validates given name, checks whether it's not empty and whether database
     * with such name already exists.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If database with such name already exists [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    open fun validateName(name: String){
        val cleanedName = fixName(name)
        emptyNameErr = cleanedName.isEmpty()
        existsNameErr = cleanedName in databases.map { it.name }
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
    open fun validatePasswords(pass1: String, pass2: String){
        emptyPassErr = pass1.isEmpty()
        diffPassErr = pass1 != pass2
    }

    /**
     * This method generates purely random salt for encryption.
     * @return salt for encryption.
     */
    open fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    /**
     * Called when user presses `Create` or `Save` button.
     * Launches [apply] only if it's not already launched.
     */
    open fun applyPressed(name: String, password: String){
        if(coroutineJob == null || !coroutineJob!!.isActive){
            coroutineJob = viewModelScope.launch(uiDispatcher) {
                apply(name, password)
            }
        }
    }

    abstract suspend fun apply(name: String, password: String)
}
