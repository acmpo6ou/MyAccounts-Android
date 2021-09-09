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

package com.acmpo6ou.myaccounts.main_activity

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.SRC_DIR
import com.acmpo6ou.myaccounts.accountsDir
import com.acmpo6ou.myaccounts.database.main_activity.MainModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class MainModelTests : ModelTest() {
    lateinit var model: MainModel

    @Before
    fun setup() {
        setupInputResolver()
        val app: MyApp = mock {
            on { contentResolver } doReturn contentResolver
            on { ACCOUNTS_DIR } doReturn accountsDir
        }
        model = MainModel(app)
    }

    @Test
    fun `getSize should return file size`() {
        val size = model.getSize(locationUri)
        assertEquals(284, size)
    }

    @Test
    fun `importDatabase should copy given dba file to src folder`() {
        model.importDatabase(locationUri)

        val expected = String(
            File("sampledata/src/main.dba").readBytes()
        )
        val actual = String(
            File("$SRC_DIR/main.dba").readBytes()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `importDatabase should return name of imported database`() {
        val name = model.importDatabase(locationUri)
        assertEquals("main", name)
    }

    @Test
    fun `importDatabase should throw FileAlreadyExistsException if database already exists`() {
        copyDatabase("main")
        try {
            model.importDatabase(locationUri)
            // if model won't throw an exception we will reach this code and test will fail
            assert(false)
        } catch (e: FileAlreadyExistsException) {
            // everything is okay - test should pass
        }
    }
}
