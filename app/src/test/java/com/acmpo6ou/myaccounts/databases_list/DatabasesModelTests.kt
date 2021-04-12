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
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
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
        val file: File = mock { on { path } doReturn accountsDir }
        val app = mock<MyApp> {
            on { getExternalFilesDir(null) } doReturn file
            on { contentResolver } doReturn contentResolver
        }
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

        // check that database tar file was exported properly
        val exportedTar = String(
            File("$accountsDir/main.tar").readBytes()
        )
        val expectedDb = String(
            File("$SRC_DIR/main.db").readBytes()
        )
        val expectedBin = String(
            File("$SRC_DIR/main.bin").readBytes()
        )

        // check that files are present and they reside in `src` folder
        assertTrue(
            "exportDatabase incorrect export: tar file doesn't contain .db file!",
            "src/main.db" in exportedTar
        )
        assertTrue(
            "exportDatabase incorrect export: tar file doesn't contain .bin file!",
            "src/main.bin" in exportedTar
        )

        // check that files have appropriate content
        assertTrue(
            "exportDatabase incorrect export: content of .db file is incorrect!",
            expectedDb in exportedTar
        )
        assertTrue(
            "exportDatabase incorrect export: content of .bin file is incorrect!",
            expectedBin in exportedTar
        )
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
