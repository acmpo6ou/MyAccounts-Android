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

package com.acmpo6ou.myaccounts.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.R
import kotlinx.coroutines.launch
import java.security.SecureRandom

abstract class CreateEditViewModel: SuperViewModel() {
    val emptyNameErr_ = MutableLiveData(true)
    val existsNameErr_ = MutableLiveData(false)

    val emptyPassErr_ = MutableLiveData(true)
    val diffPassErr_ = MutableLiveData(false)

    var emptyNameErr: Boolean
        get() = emptyNameErr_.value!!
        set(value) {emptyNameErr_.value = value}
    var existsNameErr: Boolean
        get() = existsNameErr_.value!!
        set(value) {existsNameErr_.value = value}

    var diffPassErr: Boolean
        get() = diffPassErr_.value!!
        set(value) {diffPassErr_.value = value}
    var emptyPassErr: Boolean
        get() = emptyPassErr_.value!!
        set(value) {emptyPassErr_.value = value}

    val created_ = MutableLiveData<Boolean>()
    var created
        get() = created_.value!!
        set(value) {created_.value = value}

    /**
     * This LiveData property provides error message according
     * to emptyNameErr_ and existsNameErr_ live data values.
     */
    val nameErrors = emptyNameErr_.combineWith(existsNameErr_) {
        empty: Boolean?, exists: Boolean? ->

        var msg: String? = null
        if(empty!!){
            msg = app.resources.getString(R.string.empty_name)
        }
        else if(exists!!){
            msg = app.resources.getString(R.string.db_exists)
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
            msg = app.resources.getString(R.string.empty_password)
        }
        else if(different!!){
            msg = app.resources.getString(R.string.diff_passwords)
        }
        return@combineWith msg
    }

    /**
     * This LiveData property used to decide whether `Create` button should be enabled
     * or not. If there are any errors it should be disabled, if there are no errors - enabled.
     */
    val createEnabled = nameErrors.combineWith(passwordErrors) {
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
        val supported = (('A'..'Z') + ('a'..'z') + ('0'..'9'))
                .joinToString("") + ".-_()"
        return name.filter { it in supported }
    }

    /**
     * This method validates given passwords.
     *
     * If passwords don't match [diffPassErr] is true.
     * If passwords are empty [emptyPassErr] is true.
     * @param[pass1] first password.
     * @param[pass2] second password.
     */
    open fun validatePasswords(pass1: String, pass2: String){
        diffPassErr = pass1 != pass2
        emptyPassErr = pass1.isEmpty()
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
     * Called when user presses `Create` button.
     * Launches createDatabase only if it's not already launched.
     */
    open fun createPressed(name: String, password: String){
        if(coroutineJob == null || !coroutineJob!!.isActive){
            coroutineJob = viewModelScope.launch(uiDispatcher) {
                createDatabase(name, password)
            }
        }
    }

    abstract suspend fun createDatabase(name: String, password: String)
}

/**
 * Helper extension function to combine 2 LiveData properties into one.
 * Note: it's completely copied from StackOverflow.
 */
fun <T, K, R> LiveData<T>.combineWith(
        liveData: LiveData<K>,
        block: (T?, K?) -> R): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}
