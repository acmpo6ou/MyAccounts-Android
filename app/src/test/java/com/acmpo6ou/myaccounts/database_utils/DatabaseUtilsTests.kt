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

package com.acmpo6ou.myaccounts.database_utils

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.*
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.acmpo6ou.myaccounts.str
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount

class DatabaseUtilsTests: ModelTest() {
    var app = MyApp()

    /**
     * This helper method encrypts given map using [password] and [salt].
     *
     * @param[map] database map to encrypt.
     * @return encrypted json string of database map.
     */
    private fun encryptStr(map: DbMap): String{
        val key = deriveKeyUtil(password, salt)
        val data = dumpsUtil(map)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    /**
     * This method decrypts given string.
     *
     * @param[string] string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @return decrypted string.
     */
    private fun decryptStr(string: String, password: String, salt: ByteArray): String{
        val key = deriveKeyUtil(password, salt)
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }
        val token = Token.fromString(string)
        return token.validateAndDecrypt(key, validator)
    }

    @Test
    fun `openDatabaseUtil should return Database instance with non empty data property`(){
        // here we copy `main` database to the fake file system so that we can open it later
        copyDatabase("main")

        // create corresponding Database instance
        val db = Database("main", password, salt)

        val actualDatabase = openDatabaseUtil(db, SRC_DIR, app)
        val expectedDatabase = Database("main", password, salt, getDatabaseMap())

        assertEquals(expectedDatabase, actualDatabase)
    }

    @Test
    fun `decryptDatabaseUtil should return decrypted and deserialized map given string`(){
        // encrypt database
        val expectedMap = getDatabaseMap()
        val encryptedJson = encryptStr(expectedMap)

        val map = decryptDatabaseUtil(encryptedJson, password, salt, app)
        assertEquals("Incorrect decryption! decryptDatabase method",
                expectedMap, map)
    }

    @Test
    fun `decryptDatabaseUtil should cache generated by deriveKeyUtil key`(){
        // create fresh application instance
        app = MyApp()

        // encrypt database
        val expectedMap = getDatabaseMap()
        val encryptedJson = encryptStr(expectedMap)

        decryptDatabaseUtil(encryptedJson, password, salt, app)

        // check that key was cached
        val expectedKey = deriveKeyUtil(password, salt)
        assertEquals(app.keyCache[password], expectedKey)
    }

    @Test
    fun `encryptDatabaseUtil should cache generated by deriveKeyUtil key`(){
        // create fresh application instance
        app = MyApp()

        val database = Database(faker.name().name(), password, salt, getDatabaseMap())
        encryptDatabaseUtil(database)

        // check that key was cached
        val expectedKey = deriveKeyUtil(password, salt)
        assertEquals(app.keyCache[password], expectedKey)
    }

    @Test
    fun `loadsUtil should return empty map when passed empty string`(){
        val loadMap = loadsUtil("")
        assertTrue(loadMap.isEmpty())
    }

    @Test
    fun `loadsUtil should return non empty map when passed non empty string`(){
        val map = loadsUtil(jsonDatabase)
        val expectedMap = getDatabaseMap()
        assertEquals(expectedMap, map)
    }

    @Test
    fun `dumpsUtil should return empty string when passed empty map`(){
        val dumpStr = dumpsUtil(mapOf())
        assertTrue(dumpStr.isEmpty())
    }

    @Test
    fun `dumpsUtil should return serialized string when passed non empty map`(){
        // create database with account that we will serialize
        val database = getDatabaseMap()

        // serialize database and check resulting json string
        val dumpStr = dumpsUtil(database)
        val expectedStr = jsonDatabase

        assertEquals(expectedStr, dumpStr)
    }

    @Test
    fun `encryptDatabaseUtil should return encrypted json string from Database`(){
        val dataMap = getDatabaseMap()

        val database = Database(
                faker.name().name(),
                faker.str(),
                salt, dataMap)

        // get encrypted json string
        val jsonStr = encryptDatabaseUtil(database)

        // here we decrypt the json string using salt and password we defined earlier
        // to check if it were encrypted correctly
        val data = decryptStr(jsonStr, database.password!!, database.salt!!)

        assertEquals("encryptDatabase has returned incorrectly encrypted json string!",
                jsonDatabase, data)
    }

    @Test
    fun `createDatabaseUtil should create db file given Database instance`(){
        val database = Database("main", "123", salt, getDatabaseMap())
        createDatabaseUtil(database, SRC_DIR)

        // this is a .db file that createDatabase should create for us
        val actualDb = File("$SRC_DIR/main.db").readBytes()

        // here we decrypt data saved to .db file to check that it was encrypted correctly
        val data = decryptStr(String(actualDb), "123", salt)
        assertEquals("createDatabaseUtil creates incorrectly encrypted database!",
                jsonDatabase, data)
    }

    @Test
    fun `createDatabaseUtil should create salt file given Database instance`(){
        val database = Database("main", "123", salt)
        createDatabaseUtil(database, SRC_DIR)

        // this is a salt file that createDatabase should create for us
        val actualBin = File("$SRC_DIR/main.bin").readBytes()

        assertEquals("createDatabaseUtil created incorrect salt file!",
                String(salt), String(actualBin))
    }
}