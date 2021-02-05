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

package com.acmpo6ou.myaccounts.core

import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.DbMap
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Provides some helper methods to work with databases.
 */
interface DatabaseUtils {
    val SRC_DIR: String

    /**
     * Used to deserialize json string to database map.
     *
     * @param[jsonStr] json string to deserialize.
     * @return when [jsonStr] is empty returns empty map, when it's not empty –
     * deserialized database map.
     */
    fun loads(jsonStr: String): DbMap {
        var map = mapOf<String, Account>()
        if (jsonStr.isNotEmpty()) {
            map = Json.decodeFromString(jsonStr)
        }
        return map
    }

    /**
     * Method used to serialize database map to json string.
     *
     * @param[data] map to serialize.
     * @return when [data] is empty returns empty string, when [data] is not empty –
     * serialized json string.
     */
    fun dumps(data: DbMap): String {
        var json = ""
        if (data.isNotEmpty()) {
            json = Json.encodeToString(data)
        }
        return json
    }

    /**
     * This method creates fernet key given password and salt.
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
     * Used to decrypt and deserialize encrypted json string to a database map.
     *
     * @param[jsonString] encrypted json string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @param[app] application instance containing cache of cryptography keys.
     * @return decrypted database map.
     */
    fun decryptDatabase(jsonString: String, password: String, salt: ByteArray, app: MyApp): DbMap {
        // Get key from cache if it's there, if not add the key to cache.
        // This is needed because generating cryptography key using deriveKey involves
        // 100 000 iterations which takes a long time, so the keys have to be cached and
        // generated only if they are not in the cache
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
     * This method is for database serialization and encryption.
     *
     * @param[db] Database instance to encrypt.
     * @param[app] application instance containing cache of cryptography keys.
     * @return encrypted json string.
     */
    fun encryptDatabase(db: Database, app: MyApp): String {
        val key = app.keyCache.getOrPut(db.password!!) { deriveKey(db.password!!, db.salt!!) }
        val data = dumps(db.data)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    /**
     * Used to open databases by given Database instance.
     *
     * In particular opening database means reading content of corresponding .db file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @param[app] application instance containing cache of cryptography keys.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    fun openDatabase(database: Database, app: MyApp): Database {
        val jsonStr = File("$SRC_DIR/${database.name}.db").readText()
        val data = decryptDatabase(jsonStr, database.password!!, database.salt!!, app)
        database.data = data
        return database
    }

    /**
     * Creates .db and .bin files for database given Database instance.
     *
     * @param[database] Database instance from which database name, password and salt are
     * extracted for database files creation.
     * @param[app] application instance containing cache of cryptography keys.
     */
    fun createDatabase(database: Database, app: MyApp) {
        val name = database.name

        // create salt file
        val saltFile = File("$SRC_DIR/$name.bin")
        saltFile.createNewFile()
        saltFile.writeBytes(database.salt!!)

        // create database file
        val databaseFile = File("$SRC_DIR/$name.db")
        databaseFile.createNewFile()

        // encrypt and write database to .db file
        val token = encryptDatabase(database, app)
        databaseFile.writeText(token)
    }

    /**
     * This method deletes .db and .bin files of database given its name.
     * @param[name] name of database to delete.
     */
    fun deleteDatabase(name: String) {
        val binFile = File("$SRC_DIR/$name.bin")
        binFile.delete()

        val dbFile = File("$SRC_DIR/$name.db")
        dbFile.delete()
    }
}