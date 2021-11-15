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

package com.acmpo6ou.myaccounts.create_edit_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.SRC_DIR
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.database.create_edit_database.RenameDatabaseViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class RenameDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var model: RenameDatabaseViewModel
    private val oldName = "main"
    private val newName = "clean_name"

    @Before
    fun setup() {
        copyDatabase(oldName)
        app = MyApplication()
        app.databases = mutableListOf(Database(oldName))

        val spyApp = spy(app) {
            on { SRC_DIR } doReturn SRC_DIR
        }

        model = RenameDatabaseViewModel(spyApp)
        model.databaseIndex = 0
    }

    @Test
    fun `savePressed should rename database dba file`() {
        model.savePressed(newName)
        val oldFile = File("$SRC_DIR/$oldName.dba")
        val newFile = File("$SRC_DIR/$newName.dba")

        assertTrue(newFile.exists())
        assertFalse(oldFile.exists())
    }

    @Test
    fun `savePressed should use fixName`() {
        model.savePressed("c/lea  %\$n_name/") // the name should be cleaned
        val oldFile = File("$SRC_DIR/$oldName.dba")
        val newFile = File("$SRC_DIR/$newName.dba")

        assertTrue(newFile.exists())
        assertFalse(oldFile.exists())
    }

    @Test
    fun `savePressed should update name property of Database`() {
        model.savePressed(newName)
        assertEquals(newName, app.databases[0].name)
    }

    @Test
    fun `savePressed should handle any error`() {
        val mockApp: MyApp = mock()
        val msg = faker.str()
        val exception = Exception(msg)
        doAnswer { throw exception }.whenever(mockApp).databases

        model = RenameDatabaseViewModel(mockApp)
        model.savePressed(newName)
        assertEquals(exception.toString(), model.errorMsg.value!!)
    }
}
