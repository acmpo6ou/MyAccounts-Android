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

package com.acmpo6ou.myaccounts.database

import android.content.ContentResolver
import android.net.Uri
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.createDatabaseUtil
import com.acmpo6ou.myaccounts.core.deleteDatabaseUtil
import com.acmpo6ou.myaccounts.core.openDatabaseUtil
import kotlinx.serialization.Serializable
import org.kamranzafar.jtar.TarEntry
import org.kamranzafar.jtar.TarOutputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Represents Account, it stores all account data such
 * as name, password, email, etc.
 *
 * @param[account] account name.
 * @param[name] account username.
 * @param[email] account email.
 * @param[password] account password.
 * @param[date] account date of birth.
 * @param[comment] account comment.
 */
@Serializable
data class Account(
        val account: String,
        val name: String,
        val email: String,
        val password: String,
        val date: String,
        val comment: String,
)

typealias DbMap = Map<String, Account>
typealias DbList = MutableList<Database>

/**
 * Represents database of Accounts.
 *
 * @param[name] name of the database.
 * @param[password] password of the database.
 * @param[data] map of account names to corresponding Account instances.
 * @param[salt] 16 purely random bits needed for encryption and decryption of database.
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
data class Database(val name: String,
                    var password: String? = null,
                    var salt: ByteArray? = null,
                    var data: DbMap = emptyMap()){
    var isOpen: Boolean = false
        get() = password != null
        private set
}

/**
 * Class that contains various functions related to database operations such as encrypting,
 * decrypting, deleting and creating databases.
 *
 * @param[ACCOUNTS_DIR] path to directory that contains src folder.
 */
class DatabasesModel(private val ACCOUNTS_DIR: String,
                     private val contentResolver: ContentResolver): DatabasesModelInter {
    // path to directory that contains databases
    override val SRC_DIR = "$ACCOUNTS_DIR/src/"

    /**
     * This method deletes .db and .bin files of database given its name.
     *
     * @param[name] name of database to delete.
     */
    override fun deleteDatabase(name: String) = deleteDatabaseUtil(name, SRC_DIR)

    /**
     * Used to open databases by given Database instance.
     *
     * In particular opening database means reading content of corresponding .db file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @param[app] application instance containing cache of cryptography keys used to open
     * database.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    override fun openDatabase(database: Database, app: MyApp): Database {
        return openDatabaseUtil(database, SRC_DIR, app)
    }

    /**
     * Creates .db and .bin files for database given Database instance.
     *
     * @param[database] Database instance from which database name, password and salt are
     * extracted for database files creation.
     */
    fun createDatabase(database: Database, app: MyApp) =
            createDatabaseUtil(database, SRC_DIR, app)

    /**
     * Used to get a list of Database instances – databases that reside in SRC_DIR directory.
     *
     * @return list of databases that are found in src directory.
     */
    override fun getDatabases(): DbList {
        val databases = mutableListOf<Database>()
        // src folder where all database files are stored
        val src = File(SRC_DIR)

        // walk through all files in src directory, for each whose extension is .db
        // add corresponding Database instance to databases list passing through as a parameter
        // name of that file without .db extension
        src.list()?.forEach {
            if(it.endsWith(".db")){
                val name = it.removeSuffix(".db")
                val database = Database(name)
                databases.add(database)
            }
        }
        databases.sortBy { it.name }
        return databases
    }

    /**
     * Used to export database as tar file to given destination.
     *
     * Tar file structure:
     * ( where main is database name )
     * src
     * ├── main.bin – salt file.
     * └── main.db  – encrypted database file.
     * @param[name] name of the database to export.
     * @param[destinationUri] uri with path to folder where we want to export database.
     */
    override fun exportDatabase(name: String, destinationUri: Uri) {
        // get tar file
        val descriptor = contentResolver.openFileDescriptor(destinationUri, "w")
        val destination = FileOutputStream(descriptor?.fileDescriptor)

        // create tar file
        val outStream = TarOutputStream(BufferedOutputStream(destination))

        // salt and database files to compress to a tar file
        val dbFiles = listOf(
            File("$SRC_DIR$name.db"),
            File("$SRC_DIR$name.bin"),
        )

        // each file is added to tar file
        for(f in dbFiles){
            // check if .db or .bin file exists
            if(!f.exists()){
                throw FileNotFoundException(f.name)
            }

            val entry = TarEntry(f, "src/${f.name}")
            outStream.putNextEntry(entry)
            outStream.write(f.readBytes())
        }
        outStream.flush()
        outStream.close()
    }
}
