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

package com.acmpo6ou.myaccounts.core

import android.annotation.SuppressLint
import android.util.Base64.DEFAULT
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kamranzafar.jtar.TarEntry
import org.kamranzafar.jtar.TarOutputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

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

/**
 * Represents database of Accounts.
 *
 * @param[name] name of the database.
 * @param[password] password of the database.
 * @param[data] map of account names to corresponding Account instances.
 * @param[salt] 16 purely random bits needed for encryption and decryption of database.
 * @param[index] Databases are stored in a list, [index] is the index of the given Database.
 * It is used to be able to pass Database index between activities back and forth while not
 * loosing index.
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
@Serializable
data class Database(val name: String,
                    var password: String? = null,
                    val salt: ByteArray? = null,
                    var data: Map<String, Account> = emptyMap(),
                    var index: Int? = null){
    @Transient
    var isOpen: Boolean = false
        get() = password != null
        private set
}

/**
 * Class that contains various functions related to database operations such as encrypting,
 * decrypting, deleting and creating databases.
 *
 * @param[ACCOUNTS_DIR] path to directory that contains src folder.
 * Default is /storage/emulated/0/MyAccounts/
 */
class DatabasesModel(private val ACCOUNTS_DIR: String): DatabasesModelInter{
    // path to directory that contains databases
    private val SRC_DIR = "$ACCOUNTS_DIR/src/"

    /**
     * This method generates purely random salt for encryption.
     *
     * @return salt for encryption.
     */
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    /**
     * This method creates fernet key given password and salt.
     *
     * @param[password] key password.
     * @param[salt] salt for key.
     * @return created fernet key.
     */
    @SuppressLint("NewApi")
    fun deriveKey(password: String, salt: ByteArray): Key {
        val iterations = 100000
        val derivedKeyLength = 256
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = secretKeyFactory.generateSecret(spec).encoded

        // there are some differences in API versions:
        // Base64 from java.util is supported only from API 26 but we can use it in tests
        // without mocking
        // there is also Base64 from android.util which supported by all versions but
        // doesn't work in tests
        var strKey: String
        try{
            // our min sdk version is 25, so here we first try to use Base64 from java.utils
            // (this will work for android 8 or later and also during testing)
            strKey = Base64.getUrlEncoder().encodeToString(key)
        }catch (e: Error){
            // however it will not work on android 7, so here we use Base64 from android.util
            println(e.stackTraceToString())
            strKey = android.util.Base64.encodeToString(key, DEFAULT)
        }
        return Key(strKey)
    }

    /**
     * Method used to serialize database map to json string.
     *
     * @param[data] map to serialize.
     * @return when [data] is empty returns empty string, when [data] is not empty –
     * serialized json string.
     */
    override fun dumps(data: Map<String, Account>): String{
        var json = ""
        if (data.isNotEmpty()){
            json = Json.encodeToString(data)
        }
        return json
    }

    /**
     * Used to deserialize json string to database map.
     *
     * @param[jsonStr] json string to deserialize.
     * @return when [jsonStr] is empty returns empty map, when it's not empty –
     * deserialized database map.
     */
    fun loads(jsonStr: String): Map<String, Account>{
        var map = mapOf<String, Account>()
        if (jsonStr.isNotEmpty()){
            map = Json.decodeFromString(jsonStr)
        }
        return map
    }

    /**
     * This method is for database serialization and encryption.
     *
     * @param[database] Database instance to encrypt.
     * @return encrypted json string.
     */
    fun encryptDatabase(database: Database): String{
        val key = deriveKey(database.password!!, database.salt!!)
        val data = dumps(database.data)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    /**
     * Creates .db and .bin files for database given Database instance.
     *
     * @param[database] Database instance from which database name, password and salt are
     * extracted for database files creation.
     */
    fun createDatabase(database: Database) {
        val name = database.name

        // create salt file
        val saltFile = File("$SRC_DIR$name.bin")
        saltFile.createNewFile()
        saltFile.writeBytes(database.salt!!)

        // create database file
        val databaseFile = File("$SRC_DIR$name.db")
        databaseFile.createNewFile()

        // encrypt and write database to .db file
        val token = encryptDatabase(database)
        databaseFile.writeText(token)
    }

    /**
     * This method deletes .db and .bin files of database given its name.
     *
     * @param[name] name of database to delete.
     */
    override fun deleteDatabase(name: String){
        val binFile = File("$SRC_DIR/$name.bin")
        binFile.delete()

        val dbFile = File("$SRC_DIR/$name.db")
        dbFile.delete()
    }

    /**
     * Used to decrypt and deserialize encrypted json string to a database map.
     *
     * @param[jsonString] encrypted json string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @return decrypted database map.
     */
    fun decryptDatabase(jsonString: String, password: String, salt: ByteArray):
            Map<String, Account> {
        // get key and validator
        val key = deriveKey(password, salt)
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }

        // decrypt and deserialize string
        val token = Token.fromString(jsonString)
        val decrypted = token.validateAndDecrypt(key, validator)
        return loads(decrypted)
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
    override fun openDatabase(database: Database): Database {
        val jsonStr = File("$SRC_DIR/${database.name}.db").readText()
        val data = decryptDatabase(jsonStr, database.password!!, database.salt!!)
        database.data = data
        return database
    }

    /**
     * This method simply deletes old database (which is determined by [oldName]) and
     * creates new one using [database], to more specifically say: it replaces old database
     * with a new one.
     *
     * @param[oldName] name of the old database that is to be replaced.
     * @param[database] new Database to be created, replacing the old one.
     */
    fun saveDatabase(oldName: String, database: Database){
        deleteDatabase(oldName)
        createDatabase(database)
    }

    /**
     * Used to get a list of Database instances – databases that reside in SRC_DIR directory.
     *
     * @return list of databases that are found in src directory.
     */
    override fun getDatabases(): MutableList<Database> {
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
     * @param[destination] path to folder where we want to export database.
     */
    override fun exportDatabase(name: String, destination: String) {
        // create tar file
        val tarFile = FileOutputStream("$destination$name.tar")
        val outStream = TarOutputStream(BufferedOutputStream(tarFile))

        // salt and database files to compress to a tar file
        val dbFiles = listOf(
            File("$SRC_DIR$name.db"),
            File("$SRC_DIR$name.bin"),
        )

        // each file is added to tar file
        for(f in dbFiles){
            val entry = TarEntry(f, "src/${f.name}")
            outStream.putNextEntry(entry)
            outStream.write(f.readBytes())
        }
        outStream.flush()
        outStream.close()
    }

    // NOTE: 2 next methods are needed because of testable architecture, even though
    // they both contain only one line of code
    /**
     * Used to serialise Database instance.
     *
     * @param[database] Database instance to serialise.
     * @return serialised json string.
     */
    override fun dumpDatabase(database: Database): String{
        return Json.encodeToString(database)
    }


    /**
     * Used to deserialise database json string to Database instance.
     *
     * @param[databaseJson] database json string to deserialise.
     * @return deserialised Database instance.
     */
    override fun loadDatabase(databaseJson: String): Database{
        return Json.decodeFromString(databaseJson)
    }
}
