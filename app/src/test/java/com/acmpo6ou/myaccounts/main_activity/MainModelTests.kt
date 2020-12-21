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

package com.acmpo6ou.myaccounts.main_activity

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.core.MainModel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class MainModelTests: ModelTest(){
    private var model = MainModel(accountsDir)
    private val location = "sampledata/tar/main.tar"

    @Test
    fun `importDatabase should extract given tar file to src folder`(){
        // import database
        model.importDatabase("sampledata/tar/main.tar")

        // check that all database files are imported correctly
        val expectedBin = String(salt)
        val expectedDb = String(
                File("sampledata/src/main.db").readBytes()
        )

        val actualBin = String(
                File("$SRC_DIR/main.bin").readBytes()
        )
        val actualDb = String(
                File("$SRC_DIR/main.db").readBytes()
        )

        assertEquals(
                "importDatabase incorrectly imported .db file!",
                expectedDb,
                actualDb
        )
        assertEquals(
                "importDatabase incorrectly imported .bin file!",
                expectedBin,
                actualBin
        )
    }

    @Test
    fun `importDatabase should extract database files only from given tar file`(){
        model.importDatabase("sampledata/tar/main.tar")

        // there is no other files in parent of `src` folder
        val srcParent = File(accountsDir)
        val filesList = srcParent.list()

        assertEquals(
                "importDatabase must extract only .db and .bin files from given tar!",
                1, // there must be only one directory – `src`
                filesList.size
        )
        assertEquals(
                "importDatabase must extract only .db and .bin files from given tar!",
                "src", // the only directory must be `src`
                filesList.first()
        )
    }

    @Test
    fun `countFiles should return number of files in tar file`(){
        val count = model.countFiles(location)
        assertEquals(2, count)
    }

    @Test
    fun `getNames should return list of names`(){
        val expectedList = mutableListOf("main", "main")
        val actualList = model.getNames(location)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `getSizes should return list of file sizes`(){
        val expectedList = mutableListOf(268, 16) // sizes of .bin and .db files
        val actualList = model.getSizes(location)
        assertEquals(expectedList, actualList)
    }
}