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

package com.acmpo6ou.myaccounts.open_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.open_database.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.macasaet.fernet.TokenValidationException
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("DeferredResultUnused")
class OpenDatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var model: OpenDatabaseViewModel
    private lateinit var spyModel: OpenDatabaseViewModel

    private val faker = Faker()
    lateinit var app: MyApp
    lateinit var spyApp: MyApp

    private val password = "123"
    val SRC_DIR = "sampledata/src/"

    @Before
    fun setup() {
        app = MyApplication()
        app.databases = mutableListOf(Database("main"))
        spyApp = spy(app) {
            on { SRC_DIR } doReturn SRC_DIR
        }

        model = OpenDatabaseViewModel(spyApp, Dispatchers.Unconfined, Dispatchers.Unconfined)
        spyModel = spy(model)
    }

    @Test
    fun `startPasswordCheck should not launch verifyPassword if password is empty`() {
        spyModel.startPasswordCheck("", 0)
        runBlocking {
            verify(spyModel, never()).verifyPassword("", 0)
        }
    }

    @Test
    fun `startPasswordCheck should launch verifyPassword if passwordJob is null`() {
        spyModel.startPasswordCheck(password, 0)
        runBlocking {
            verify(spyModel).verifyPassword(password, 0)
        }
    }

    @Test
    fun `startPasswordCheck should launch verifyPassword if passwordJob isn't active`() {
        spyModel.coroutineJob = mock { on { isActive } doReturn false }
        spyModel.startPasswordCheck(password, 0)

        runBlocking {
            verify(spyModel).verifyPassword(password, 0)
        }
    }

    @Test
    fun `verifyPassword should set loading to true`() {
        runBlocking {
            spyModel.verifyPassword(password, 0)
        }
        assertTrue(spyModel.loading.value!!)
    }

    @Test
    fun `verifyPassword should save deserialized Database to the list`() {
        val expectedDatabase = Database("main", password, salt, databaseMap.copy())

        runBlocking {
            spyModel.verifyPassword(password, 0)
        }
        assertEquals(expectedDatabase.toString(), app.databases[0].toString())
    }

    @Test
    fun `verifyPassword should set opened to true after successful deserialization`() {
        runBlocking {
            spyModel.verifyPassword(password, 0)
        }
        assertTrue(spyModel.opened.value!!)
    }

    @Test
    fun `verifyPassword should set incorrectPassword to true if there is TokenValidation error`() {
        doAnswer {
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabaseAsync(any())

        runBlocking {
            spyModel.verifyPassword(faker.str(), 0)
        }
        assertTrue(spyModel.incorrectPassword.value!!)
    }

    @Test
    fun `verifyPassword should set loading to false when password is incorrect`() {
        doAnswer {
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabaseAsync(any())

        runBlocking {
            spyModel.verifyPassword(faker.str(), 0)
        }
        assertFalse(spyModel.loading.value!!)
    }

    @Test
    fun `verifyPassword should remove key from cache if password is incorrect`() {
        doAnswer {
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabaseAsync(any())

        runBlocking {
            spyModel.verifyPassword(faker.str(), 0)
        }
        assertTrue(app.keyCache.isEmpty())
    }

    @Test
    fun `verifyPassword should set corrupted to true if there is deserialization error`() {
        doAnswer {
            // here we throw Exception instead of JsonDecodingException because
            // JsonDecodingException is private
            throw Exception("JsonDecodingException")
        }.whenever(spyModel).openDatabaseAsync(any())

        runBlocking {
            spyModel.verifyPassword(faker.str(), 0)
        }
        assertTrue(spyModel.corrupted.value!!)
    }

    @Test
    fun `verifyPassword should handle any error`() {
        val msg = faker.str()
        val exception = Exception(msg)
        doAnswer {
            throw exception
        }.whenever(spyModel).openDatabaseAsync(any())

        runBlocking {
            spyModel.verifyPassword(password, 0)
        }
        assertEquals(exception.toString(), spyModel.errorMsg.value!!)
        assertFalse(spyModel.loading.value!!)
    }

    @Test
    fun `startPasswordCheck should not start verifyPassword if passwordJob already active`() {
        spyModel.coroutineJob = mock { on { isActive } doReturn true }
        spyModel.startPasswordCheck(password, 0)

        runBlocking {
            verify(spyModel, never()).verifyPassword(password, 0)
        }
    }
}
