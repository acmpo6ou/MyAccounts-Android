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
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.database.EditDatabaseViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

@Suppress("DeferredResultUnused")
class EditDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = EditDatabaseViewModel()
    lateinit var spyModel: EditDatabaseViewModel
    lateinit var app: MyApp

    private val oldName = "main"
    private val name = "clean_name"
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup(){
        app = MyApp()
        app.databases = mutableListOf( Database(oldName, password, salt) )
        app.keyCache = mutableMapOf(password to deriveKey(password, salt))

        model.initialize(app, SRC_DIR, faker.str(), 0)
        spyModel = spy(model){ on{generateSalt()} doReturn salt }

        doNothing().whenever(spyModel).deleteDatabase(anyString())
        doNothing().whenever(spyModel).createDatabase(any(), eq(app))

        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.defaultDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun `validateName when name of Database didn't change through editing`(){
        // database `main` already exists but it's being edited, so that doesn't count
        model.validateName(oldName)
        assertFalse(model.existsNameErr)
        assertFalse(model.emptyNameErr)
    }

    @Test
    fun `validateName should use fixName when Database name didn't change through editing`(){
        model.validateName("m/a/i/n/") // will become `main` when cleaned by fixName
        assertFalse(model.existsNameErr)
        assertFalse(model.emptyNameErr)
    }

    @Test
    fun `apply should call saveDatabase`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        verify(spyModel).saveDatabase(oldName, db)
    }

    @Test
    fun `apply should use fixName`(){
        runBlocking {
            // will become `clean_name` when cleaned by fixName
            spyModel.apply("c/lea  %\$n_name/", password)
        }
        verify(spyModel).saveDatabase(oldName, db)
    }

    @Test
    fun `apply should handle any error`(){
        val msg = faker.str()
        val exception = Exception(msg)
        doAnswer { throw exception }.whenever(spyModel).deleteDatabase(anyString())

        runBlocking {
            spyModel.apply(name, password)
        }
        assertEquals(exception.toString(), spyModel.errorMsg)
        assertFalse(spyModel.loading)
    }

    @Test
    fun `apply should replace old Database with created one`(){
        runBlocking {
            spyModel.apply(name, password)
        }

        assertFalse(Database(oldName, password, salt) in spyModel.databases)
        assertTrue(db in spyModel.databases)
    }

    @Test
    fun `apply should remove cached cryptography key if password has changed`(){
        runBlocking {
            spyModel.apply(name, "123") // now password is 123
        }
        assertFalse(deriveKey(password, salt) in app.keyCache)
    }

    @Test
    fun `apply should set finished to true after successful save of database`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.finished)
    }

    @Test
    fun `apply should set loading to true`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.loading)
    }

    @Test
    fun `saveDatabase should call deleteDatabase and createDatabase`(){
        runBlocking {
            spyModel.apply(name, password)
        }

        verify(spyModel).deleteDatabase(oldName)
        verify(spyModel).createDatabase(db, app)
    }
}