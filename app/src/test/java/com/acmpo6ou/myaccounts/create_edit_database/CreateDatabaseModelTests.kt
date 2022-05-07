/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.database.create_edit_database.CreateDatabaseViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("DeferredResultUnused")
class CreateDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var model: CreateDatabaseViewModel
    lateinit var spyModel: CreateDatabaseViewModel

    private val name = "clean_name"
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup() {
        app = MyApplication()
        app.databases = mutableListOf(Database("main"))
        val spyApp = spy(app) {
            on { SRC_DIR } doReturn SRC_DIR
        }

        model = CreateDatabaseViewModel(spyApp, Dispatchers.Unconfined, Dispatchers.Unconfined)
        spyModel = spy(model) { on { generateSalt() } doReturn salt }
    }

    @Test
    fun `apply should set loading to true`() {
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.loading.value!!)
    }

    @Test
    fun `apply should call createDatabaseAsync`() {
        runBlocking {
            spyModel.apply(name, password)
        }
        verify(spyModel).createDatabaseAsync(db)
    }

    @Test
    fun `apply should use fixName`() {
        runBlocking {
            // will become `clean_name` when cleaned by fixName
            spyModel.apply("c/lea  %\$n_name/", password)
        }
        verify(spyModel).createDatabaseAsync(db)
    }

    @Test
    fun `apply should add created Database to the list`() {
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(db in app.databases)
    }

    @Test
    fun `apply should set finished to true`() {
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.finished.value!!)
    }

    @Test
    fun `apply should handle any error`() {
        val msg = faker.str()
        val exception = Exception(msg)
        whenever(spyModel.createDatabaseAsync(db))
            .doAnswer {
                throw exception
            }

        runBlocking {
            spyModel.apply(name, password)
        }
        assertEquals(exception.toString(), spyModel.errorMsg.value!!)
        assertFalse(spyModel.loading.value!!)
    }
}
