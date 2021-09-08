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

package com.acmpo6ou.myaccounts.databases_list

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.SRC_DIR
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class DatabasesTests {
    private val faker = Faker()

    @Test
    fun `Database should have isOpen property set to false when password is null`() {
        // we didn't  pass the password so it will be null by default
        val database = Database(faker.str())

        // if password is null then database should be closed
        assertFalse(database.isOpen)
    }

    @Test
    fun `Database should have isOpen property set to true when password is NOT null`() {
        // we passed the password, so it is not null
        val database = Database(faker.str(), faker.str())

        // when password is not null database should be opened
        assertTrue(database.isOpen)
    }
}

class DatabasesModelTests : ModelTest() {
    lateinit var model: DatabasesModel

    @Before
    fun setup() {
        model = DatabasesModel(app)
    }

    @Test
    fun `getDatabases should return list of Databases that reside in SRC_DIR`() {
        // first we copy some databases to our fake file system
        copyDatabase("main")
        copyDatabase("crypt")
        copyDatabase("database")

        val databases = model.getDatabases()
        val expectedDatabases = listOf( // note that list is sorted
            Database("crypt"),
            Database("database"),
            Database("main")
        )
        assertEquals(expectedDatabases, databases)
    }

    @Test
    fun `exportDatabase should export database tar to given location`() {
        setupOutputResolver()
        copyDatabase("main")

        // export database `main` to the fake file system
        model.exportDatabase("main", destinationUri)

        val expectedDb = String(
            File("sampledata/src/main.dba").readBytes()
        )
        val exportedDb = String(
            File("$SRC_DIR/main.dba").readBytes()
        )
        assertEquals(expectedDb, exportedDb)
    }

    @Test
    fun `exportDatabase should throw FileNotFoundException if there are no db or bin files`() {
        setupOutputResolver()
        // there is no database named `testing` so we can't export it, because there are no
        // testing.db and testing.bin files
        try {
            model.exportDatabase("testing", destinationUri)
            assert(false) // if there is no exception thrown the test will fail
        } catch (e: FileNotFoundException) {
            // if this exception were thrown its okay, test should pass
        }
    }
}
