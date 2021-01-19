/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.macasaet.fernet.TokenValidationException
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OpenDatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var spyModel: OpenDatabaseViewModel
    private val faker = Faker()
    private val password = "123"

    val SRC_DIR = "sampledata/src/"
    val titleStart = faker.lorem().sentence()
    private val salt = "0123456789abcdef".toByteArray()
    lateinit var app: MyApp

    @Before
    fun setup(){
        app = MyApp()
        app.databases = mutableListOf(Database("main"))

        // init spyModel
        spyModel = spy()
        spyModel.initialize(app, SRC_DIR, titleStart, 0)
        spyModel.defaultDispatcher = Dispatchers.Unconfined
        spyModel.uiDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun `verifyPassword should set incorrectPassword to true if there is TokenValidation error`(){
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertTrue(spyModel.incorrectPassword)
    }

    @Test
    fun `verifyPassword should remove key from cache if password is incorrect`(){
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertTrue(app.keyCache.isEmpty())
    }

    @Test
    fun `verifyPassword should set corrupted to true if there is deserialization error`(){
        doAnswer{
            // here we throw Exception instead of JsonDecodingException because
            // JsonDecodingException is private
            throw Exception("JsonDecodingException")
        }.whenever(spyModel).openDatabase(any())

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertTrue(spyModel.corrupted)
    }

    @Test
    fun `verifyPassword should save deserialized Database to list`(){
        val expectedDatabase = Database("main", password, salt, getDatabaseMap())

        runBlocking {
            spyModel.verifyPassword(password)
        }
        assertEquals(expectedDatabase.toString(), app.databases[0].toString())
    }

    @Test
    fun `verifyPassword should set isOpened to true after successful deserialization`(){
        runBlocking {
            spyModel.verifyPassword(password)
        }
        assertTrue(spyModel.opened)
    }

    @Test
    fun `verifyPassword should set loading to true`(){
        runBlocking {
            spyModel.verifyPassword(password)
        }
        assertTrue(spyModel.loading)
    }

    @Test
    fun `verifyPassword should set loading to false when password is incorrect`(){
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertFalse(spyModel.loading)
    }

    @Test
    fun `startPasswordCheck should not start verifyPassword if passwordJob already active`(){
        spyModel.passwordJob = mock { on {isActive} doReturn true }
        spyModel.startPasswordCheck(password)

        runBlocking {
            verify(spyModel, never()).verifyPassword(password)
        }
    }

    @Test
    fun `startPasswordCheck should not start verifyPassword if password is empty`(){
        spyModel.startPasswordCheck("")

        runBlocking {
            verify(spyModel, never()).verifyPassword("")
        }
    }

    @Test
    fun `startPasswordCheck should start verifyPassword if passwordJob isn't active`(){
        spyModel.passwordJob = mock { on {isActive} doReturn false }
        spyModel.startPasswordCheck(password)

        runBlocking {
            verify(spyModel).verifyPassword(password)
        }
    }

    @Test
    fun `startPasswordCheck should start verifyPassword if passwordJob is null`(){
        spyModel.startPasswordCheck(password)

        runBlocking {
            verify(spyModel).verifyPassword(password)
        }
    }
}