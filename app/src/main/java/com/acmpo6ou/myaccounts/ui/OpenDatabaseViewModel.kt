/*
 * Copyright (c) 2020. Kolvakh Bohdan
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
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.openDatabaseUtil
import com.macasaet.fernet.TokenValidationException
import java.io.File

open class OpenDatabaseViewModel : ViewModel() {
    private var databaseIndex: Int = 0
    lateinit var app: MyApp
    lateinit var SRC_DIR: String

    var databases: MutableList<Database>
        get() = app.databases
        set(value) {
            app.databases = value
        }

    private val title = MutableLiveData<String>()
    private val incorrectPassword = MutableLiveData(false)
    private val corrupted = MutableLiveData(false)
    private val opened = MutableLiveData(false)

    fun getTitle() = title.value!!
    fun isIncorrectPassword() = incorrectPassword.value!!
    fun isCorrupted() = corrupted.value!!
    fun isOpened() = opened.value!!

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app], [SRC_DIR] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[databaseIndex] index of database that we want to open.
     * @param[SRC_DIR] path to src directory that contains databases.
     */
    fun setDatabase(app: MyApp, databaseIndex: Int, SRC_DIR: String) {
        this.app = app
        this.SRC_DIR = SRC_DIR
        this.databaseIndex = databaseIndex

        val name = databases[databaseIndex].name
        title.value = "Open $name"
    }

    /**
     * Tries to open database using given password handling all errors.
     *
     * If there is TokenValidationException then set incorrectPassword to true, this will
     * lead to error message displaying near the password field.
     *
     * If there is JsonDecodingException then set corrupted to true, this will lead to
     * displaying error dialog saying that the database is corrupted.
     * @param[password] password for the database.
     */
    fun verifyPassword(password: String) {
        try {
            var database = databases[databaseIndex].copy()
            // get salt
            val bin = File("$SRC_DIR/${database.name}.bin").readText()
            val salt = bin.toByteArray()

            // set password and salt
            database.password = password
            database.salt = salt

            // save deserialized database
            database = openDatabase(database)
            databases[databaseIndex] = database

            // set opened to true to notify fragment about successful
            // database deserialization
            opened.value = true
        }
        catch (e: TokenValidationException){
            incorrectPassword.value = true
        }
        catch (e: Exception){
            // here we catch Exception because JsonDecodingException is private
            // then we verify that this is the JsonDecodingException by looking at error
            // message
            if ("JsonDecodingException" in e.toString()){
                corrupted.value = true
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
     * @param[database] Database instance with password, name and salt to open database.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    open fun openDatabase(database: Database): Database {
        return openDatabaseUtil(database, SRC_DIR)
    }
}