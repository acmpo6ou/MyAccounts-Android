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

package com.acmpo6ou.myaccounts.ui.database

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.superclass.SuperViewModel
import com.macasaet.fernet.TokenValidationException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

open class OpenDatabaseViewModel : SuperViewModel() {
    val _incorrectPassword = MutableLiveData(false)
    val _corrupted = MutableLiveData(false)
    val _opened = MutableLiveData(false)

    val incorrectPassword get() = _incorrectPassword.value!!
    val corrupted get() = _corrupted.value!!
    val opened get() = _opened.value!!

    /**
     * This method launches verifyPassword coroutine only if it wasn't already launched and
     * password is not empty.
     * @param[password] password needed by verifyPassword coroutine.
     */
    open fun startPasswordCheck(password: String){
        if((coroutineJob == null || !coroutineJob!!.isActive) &&
            password.isNotEmpty()) {
            coroutineJob = viewModelScope.launch(uiDispatcher) {
                verifyPassword(password)
            }
        }
    }

    /**
     * Tries to open database using given password and handling all errors.
     *
     * If there is TokenValidationException then set incorrectPassword to true, this will
     * lead to error message displaying near the password field.
     *
     * If there is JsonDecodingException then set corrupted to true, this will lead to
     * displaying error dialog saying that the database is corrupted.
     * @param[password] password for the database.
     */
    open suspend fun verifyPassword(password: String) {
        try {
            // show loading because decrypting database takes time
            _loading.value = true

            val database = databases[databaseIndex].copy()
            val salt = File("$SRC_DIR/${database.name}.bin").readBytes()

            database.password = password
            database.salt = salt

            // save deserialized database
            databases[databaseIndex] = openDatabaseAsync(database).await()

            // set opened to true to notify fragment about successful
            // database deserialization
            _opened.value = true
            _incorrectPassword.value = false
        }
        catch (e: TokenValidationException){
            _incorrectPassword.value = true
            _loading.value = false
            e.printStackTrace()

            // remove cached key to avoid memory leak, because we don't need to cache
            // keys generated from incorrect passwords
            app.keyCache.remove(password)
        }
        catch (e: Exception){
            // Here we catch Exception because JsonDecodingException is private.
            // Then we verify that this is the JsonDecodingException by looking at the
            // error message
            if ("JsonDecodingException" in e.toString()){
                _incorrectPassword.value = false
                _corrupted.value = true
            }
            else{
                // notify about error and hide loading progress bar
                errorMsg = e.toString()
                loading = false
            }
            e.printStackTrace()
        }
    }

    /**
     * Used to open databases by given Database instance.
     *
     * In particular opening database means reading content of corresponding .db file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * Note: this function is asynchronous because opening a database involves generating
     * cryptography key which takes a long time and would freeze the ui.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    open fun openDatabaseAsync(database: Database) =
    viewModelScope.async(defaultDispatcher) {
        openDatabase(database, app)
    }
}