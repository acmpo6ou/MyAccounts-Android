/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.database.databases_list

import android.net.Uri
import com.acmpo6ou.myaccounts.MyApp
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Represents account, it stores all account data such as name, password, email, etc.
 *
 * Note: the [copyEmail] property is not used by MyAccounts but it's used by PyAccounts,
 * so we have to list it here.
 */
@Serializable
data class Account(
    @SerialName("account")
    val accountName: String,
    @SerialName("name")
    val username: String,
    val email: String,
    val password: String,
    val date: String,
    val comment: String,
    @SerialName("copy_email")
    val copyEmail: Boolean = true,
    @SerialName("attach_files")
    var attachedFiles: MutableMap<String, String> = mutableMapOf()
)

typealias DbMap = MutableMap<String, Account>
typealias DbList = MutableList<Database>

/**
 * Represents database of Accounts.
 *
 * @param[name] name of the database.
 * @param[password] password of the database.
 * @param[salt] 16 purely random bits needed for encryption and decryption of database.
 * @param[data] map of account names to corresponding Account instances.
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
data class Database(
    var name: String,
    var password: String? = null,
    var salt: ByteArray? = null,
    var data: DbMap = mutableMapOf()
) {
    val isOpen get() = password != null
}

/**
 * Contains various methods related to database operations such as encrypting,
 * decrypting, deleting and creating databases.
 */
@FragmentScoped
class DatabasesModel @Inject constructor(
    override val app: MyApp
) : DatabasesModelI {

    /**
     * Returns a list of Database instances – databases that reside in src directory.
     */
    override fun getDatabases(): DbList {
        val databases = mutableListOf<Database>()
        // src folder where all database files are stored
        val src = File(app.SRC_DIR)

        // walk through all files in src directory, for each file whose extension is .dba
        // add corresponding Database instance to [databases] list passing through
        // as a parameter name of that file without .dba extension
        src.list()?.forEach {
            if (it.endsWith(".dba")) {
                val name = it.removeSuffix(".dba")
                val database = Database(name)
                databases.add(database)
            }
        }
        databases.sortBy { it.name }
        return databases
    }

    /**
     * Exports database to given destination.
     *
     * @param[name] name of the database to export.
     * @param[destinationUri] uri with path to folder where we want to export database.
     */
    override fun exportDatabase(name: String, destinationUri: Uri) {
        val descriptor = app.contentResolver.openFileDescriptor(destinationUri, "w")
        val destination = FileOutputStream(descriptor?.fileDescriptor)

        val dbFile = File("${app.SRC_DIR}$name.dba").readBytes()
        destination.write(dbFile)
    }
}
