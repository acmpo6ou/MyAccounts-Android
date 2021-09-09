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

package com.acmpo6ou.myaccounts.core.utils

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.databases_list.DbMap
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Provides some helper methods to work with databases.
 */
interface DatabaseUtils {
    val app: MyApp

    /**
     * Deserializes json string to database map.
     *
     * @param[jsonStr] json string to deserialize.
     * @return when [jsonStr] is empty returns empty map, otherwise – deserialized database map.
     */
    fun loads(jsonStr: String): DbMap {
        var map = mutableMapOf<String, Account>()
        if (jsonStr.isNotEmpty())
            map = Json { ignoreUnknownKeys = true }.decodeFromString(jsonStr)
        return map
    }

    /**
     * Serializes database map to json string.
     *
     * @param[data] map to serialize.
     * @return when [data] is empty returns empty string, otherwise – serialized json string.
     */
    fun dumps(data: DbMap): String {
        var json = ""
        if (data.isNotEmpty()) json = Json.encodeToString(data)
        return json
    }

    /**
     * Creates fernet key given password and salt.
     *
     * @param[password] key password.
     * @param[salt] salt for key.
     * @return created fernet key.
     */
    fun deriveKey(password: String, salt: ByteArray): Key {
        val iterations = 100_000
        val derivedKeyLength = 256

        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        val key = secretKeyFactory.generateSecret(spec).encoded
        val strKey = java.util.Base64.getUrlEncoder().encodeToString(key)
        return Key(strKey)
    }

    /**
     * Decrypts and deserializes given json string to a database map.
     *
     * @param[jsonString] encrypted json string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @return decrypted database map.
     */
    fun decryptDatabase(jsonString: String, password: String, salt: ByteArray): DbMap {
        // Get key from cache if it's there, if not add the key to cache.
        // This is needed because generating cryptography key using deriveKey involves
        // 100 000 iterations which takes a long time, so the keys have to be cached and
        // generated only if they aren't in the cache
        val key = app.keyCache.getOrPut(password) { deriveKey(password, salt) }

        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount =
                Duration.ofSeconds(Instant.MAX.epochSecond)
        }

        // decrypt and deserialize string
        val token = Token.fromString(jsonString)
        val decrypted = token.validateAndDecrypt(key, validator)
        return loads(decrypted)
    }

    /**
     * Serializes and encrypts given database.
     *
     * @param[db] Database instance to encrypt.
     * @return encrypted json string.
     */
    fun encryptDatabase(db: Database): String {
        val key = app.keyCache.getOrPut(db.password!!) { deriveKey(db.password!!, db.salt!!) }
        val data = dumps(db.data)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    /**
     * Checks whether given database is saved i.e. the database data that is on the disk
     * is same as the data that is in memory.
     *
     * This method is needed when we want to close database, using isDatabaseSaved we
     * can determine whether to show confirmation dialog about unsaved data to user or not.
     * @param[database] database we want to check (the one that resides in memory).
     */
    fun isDatabaseSaved(database: Database): Boolean {
        val diskDatabase: Database
        try {
            diskDatabase = openDatabase(database.copy())
        } catch (e: FileNotFoundException) {
            // if database on disk doesn't exist then it definitely
            // differs from the one in memory
            e.printStackTrace()
            return false
        }
        return database.data == diskDatabase.data
    }

    /**
     * Opens database given Database instance.
     *
     * In particular opening database means reading content of corresponding .dba file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @return same Database instance but with `data` property set to deserialized
     * database map.
     */
    fun openDatabase(database: Database): Database {
        val file = File("${app.SRC_DIR}/${database.name}.dba")
        val jsonStr: String

        FileInputStream(file).use {
            it.channel.position(16) // skip 16 bytes of salt
            jsonStr = String(it.readBytes())
        }

        val data = decryptDatabase(jsonStr, database.password!!, database.salt!!)
        database.data = data
        return database
    }

    /**
     * Creates .dba file given Database instance.
     *
     * @param[database] Database instance from which database name, password and salt are
     * extracted for database file creation.
     */
    fun createDatabase(database: Database) {
        val name = database.name
        val file = File("${app.SRC_DIR}/$name.dba")
        file.createNewFile()

        FileOutputStream(file).use {
            it.write(database.salt!!) // write salt

            // encrypt and write database
            val token = encryptDatabase(database)
            it.write(token.toByteArray())
        }
    }

    /**
     * Deletes database .dba file given its name.
     * @param[name] name of database to delete.
     */
    fun deleteDatabase(name: String) {
        File("${app.SRC_DIR}/$name.dba").delete()
    }

    /**
     * Deletes old database (which is determined by [oldName]) and
     * creates new one using [database], to more specifically say: it replaces old database
     * with a new one.
     *
     * @param[oldName] name of the old database that is to be replaced.
     * @param[database] new Database to be created, replacing the old one.
     */
    fun saveDatabase(oldName: String, database: Database) {
        deleteDatabase(oldName)
        createDatabase(database)
    }
}
