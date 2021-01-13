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
import com.acmpo6ou.myaccounts.core.*
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.macasaet.fernet.Token
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseUtilsTests: ModelTest() {
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
        // encrypt database so we can check how decryptDatabase will decrypt it
        val expectedMap = getDatabaseMap()
        val encryptedJson = encryptStr(expectedMap)

        val map = decryptDatabaseUtil(encryptedJson, password, salt, app)
        assertEquals(
                "Incorrect decryption! decryptDatabase method",
                expectedMap,
                map
        )
    }

    @Test
    fun `loadsUtil should return empty map when passed empty string`(){
        val loadMap = loadsUtil("")
        assertTrue(loadMap.isEmpty())
    }

    @Test
    fun `loadsUtil should return non empty map when passed non empty string`(){
        // load database map from json string
        val map = loadsUtil(jsonDatabase)

        // get database map that we expect
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
}