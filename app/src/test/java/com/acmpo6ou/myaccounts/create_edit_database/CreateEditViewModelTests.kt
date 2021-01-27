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
import com.acmpo6ou.myaccounts.core.CreateEditViewModel
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// CreateDatabaseViewModel class is abstract and we can't instantiate it for tests
open class TestModel : CreateEditViewModel(){
    override suspend fun apply(name: String, password: String) {

    }
}

class CreateEditViewModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = TestModel()
    lateinit var spyModel: TestModel

    private val name = faker.str()
    override val password = faker.str()

    @Before
    fun setup(){
        val app = MyApp()
        app.databases = mutableListOf(Database("main"),
                                      Database("test", "123"))

        model.initialize(app, SRC_DIR)
        spyModel = spy(model){ on{generateSalt()} doReturn salt }
        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.defaultDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun `validateName should change emptyNameErr`(){
        // if name isn't empty emptyNameErr should be false
        model.validateName(faker.str())
        assertFalse(model.emptyNameErr)

        // if name is empty emptyNameErr should be true
        model.validateName("")
        assertTrue(model.emptyNameErr)
    }

    @Test
    fun `validateName should use fixName`(){
        val name = " \\/%$" // this name will be empty when cleaned by fixName
        model.validateName(name)
        assertTrue(model.emptyNameErr)
    }

    @Test
    fun `validateName should set existsNameErr to true when Database with such name exists`(){
        model.validateName("main")
        assertTrue(model.existsNameErr)

        // same should happen even if name contains unsupported characters
        model.validateName("m/a/i/n/") // will become `main` when cleaned by fixName
        assertTrue(model.existsNameErr)

        // and even if database is opened
        model.validateName("test") // test is opened
        assertTrue(model.existsNameErr)
    }

    @Test
    fun `fixName should remove all unsupported characters`(){
        val name = model.fixName("This is (test)/.\\-_-")
        assertEquals("Thisis(test).-_-", name)
    }

    @Test
    fun `validatePasswords should change diffPassErr`(){
        val pass1 = faker.str()
        val pass2 = faker.str()

        // if passwords are different - diffPassErr = true
        model.validatePasswords(pass1, pass2)
        assertTrue(model.diffPassErr)

        // if passwords are same - diffPassErr = false
        model.validatePasswords(pass1, pass1)
        assertFalse(model.diffPassErr)
    }

    @Test
    fun `validatePasswords should change emptyPassErr`(){
        // if password is empty - emptyPassErr = true
        model.validatePasswords("", "")
        assertTrue(model.emptyPassErr)

        // if password isn't empty - emptyPassErr = false
        model.validatePasswords(faker.str(), faker.str())
        assertFalse(model.emptyPassErr)
    }

    @Test
    fun `applyPressed should not call apply if coroutineJob is active`(){
        spyModel.coroutineJob = mock { on {isActive} doReturn true }
        spyModel.applyPressed(name, password)

        runBlocking {
            verify(spyModel, never()).apply(name, password)
        }
    }

    @Test
    fun `applyPressed should call apply if coroutineJob isn't active`(){
        spyModel.coroutineJob = mock { on {isActive} doReturn false }
        spyModel.applyPressed(name, password)

        runBlocking {
            verify(spyModel).apply(name, password)
        }
    }

    @Test
    fun `applyPressed should call apply if coroutineJob is null`(){
        spyModel.applyPressed(name, password)

        runBlocking {
            verify(spyModel).apply(name, password)
        }
    }
}