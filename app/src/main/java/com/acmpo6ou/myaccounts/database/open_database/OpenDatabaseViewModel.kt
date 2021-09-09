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

package com.acmpo6ou.myaccounts.database.open_database

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.ErrorViewModel
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.macasaet.fernet.TokenValidationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@HiltViewModel
open class OpenDatabaseViewModel(
    override val app: MyApp,
    private val defaultDispatcher: CoroutineDispatcher,
    private val uiDispatcher: CoroutineDispatcher,
) : ViewModel(), ErrorViewModel, DatabaseUtils {

    @Inject
    constructor(app: MyApp) : this(app, Dispatchers.Default, Dispatchers.Main)
    var coroutineJob: Job? = null

    override val errorMsg = MutableLiveData<String>()
    val incorrectPassword = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val corrupted = MutableLiveData<Boolean>()
    val opened = MutableLiveData<Boolean>()

    /**
     * This method launches verifyPassword coroutine only if it wasn't already launched and
     * password is not empty.
     * @param[password] password needed by verifyPassword coroutine.
     */
    open fun startPasswordCheck(password: String, databaseIndex: Int) {
        if ((coroutineJob == null || !coroutineJob!!.isActive) &&
            password.isNotEmpty()
        ) {
            coroutineJob = viewModelScope.launch(uiDispatcher) {
                verifyPassword(password, databaseIndex)
            }
        }
    }

    /**
     * Tries to open database using given password and handling all errors.
     *
     * If there is TokenValidationException sets incorrectPassword to true, this will
     * lead to error message displaying near the password field.
     *
     * If there is JsonDecodingException sets corrupted to true, this will lead to
     * displaying error dialog saying that the database is corrupted.
     *
     * @param[password] password for the database.
     */
    open suspend fun verifyPassword(password: String, databaseIndex: Int) {
        try {
            // show loading because decrypting database takes time
            loading.value = true

            val database = app.databases[databaseIndex].copy()
            val salt = ByteArray(16)
            File("${app.SRC_DIR}/${database.name}.dba").inputStream().use {
                it.read(salt)
            }

            database.password = password
            database.salt = salt

            // save deserialized database
            app.databases[databaseIndex] = openDatabaseAsync(database).await()

            // set opened to true to notify fragment about successful
            // database deserialization
            opened.value = true
            incorrectPassword.value = false
        } catch (e: TokenValidationException) {
            e.printStackTrace()
            incorrectPassword.value = true
            loading.value = false

            // remove cached key to avoid memory leak, because we don't need to cache
            // keys generated from incorrect passwords
            app.keyCache.remove(password)
        } catch (e: Exception) {
            e.printStackTrace()
            // Here we catch Exception because JsonDecodingException is internal.
            // Then we verify that this is the JsonDecodingException by looking at the
            // error message
            if ("JsonDecodingException" in e.toString()) {
                corrupted.value = true
                incorrectPassword.value = false
            } else {
                // notify about error and hide loading progress bar
                errorMsg.value = e.toString()
                loading.value = false
            }
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
            openDatabase(database)
        }
}
