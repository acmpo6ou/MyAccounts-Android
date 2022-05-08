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
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.superclass.CreateEditDatabaseModel
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateEditDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var model: TestDatabaseModel
    private lateinit var spyModel: TestDatabaseModel

    private val name = faker.str()
    override val password = faker.str()

    @Before
    fun setup() {
        app = MyApplication()
        app.databases = mutableListOf(
            Database("main"),
            Database("test", "123")
        )

        model = TestDatabaseModel(app)
        spyModel = spy(model)
    }

    @Test
    fun `fixName should remove all unsupported characters`() {
        val name = model.fixName("This is (test)/.\\-_-")
        assertEquals("Thisis(test).-_-", name)
    }

    @Test
    fun `validateName should set existsNameErr to true when Database with such name exists`() {
        // even if name contains unsupported characters
        model.validateName("m/a/i/n/") // will become `main` when cleaned by fixName
        assertTrue(model.existsNameErr.value!!)
    }

    @Test
    fun `validateName should use fixName`() {
        val name = " \\/%$" // this name will be empty when cleaned by fixName
        model.validateName(name)
        assertTrue(model.emptyNameErr.value!!)
    }

    @Test
    fun `applyPressed should call apply if coroutineJob is null`() {
        spyModel.applyPressed(name, password)
        runBlocking {
            verify(spyModel).apply(name, password)
        }
    }

    @Test
    fun `applyPressed should call apply if coroutineJob isn't active`() {
        spyModel.coroutineJob = mock { on { isActive } doReturn false }
        spyModel.applyPressed(name, password)

        runBlocking {
            verify(spyModel).apply(name, password)
        }
    }

    @Test
    fun `applyPressed should not call apply if coroutineJob is active`() {
        spyModel.coroutineJob = mock { on { isActive } doReturn true }
        spyModel.applyPressed(name, password)

        runBlocking {
            verify(spyModel, never()).apply(name, password)
        }
    }
}

open class TestDatabaseModel(override val app: MyApp) : CreateEditDatabaseModel() {
    override val uiDispatcher = Dispatchers.Unconfined
    override suspend fun apply(name: String, password: String) {
    }
}
