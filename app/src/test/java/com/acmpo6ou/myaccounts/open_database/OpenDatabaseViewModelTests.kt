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
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OpenDatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = OpenDatabaseViewModel()
    private var spyModel = spy(model)
    private val faker = Faker()
    private val password = "123"

    val SRC_DIR = "sampledata/src/"
    val OPEN_DB = faker.lorem().sentence()
    private val salt = "0123456789abcdef".toByteArray()
    lateinit var app: MyApp

    @Before
    fun setup(){
        app = MyApp()
        app.databases = mutableListOf(Database("main"))
        spyModel = spy(model)
        spyModel.defaultDispatcher = Dispatchers.Unconfined
        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.initialize(app, 0, SRC_DIR, OPEN_DB)
    }

    @Test
    fun `initialize should set title`(){
        model.initialize(app, 0, SRC_DIR, OPEN_DB)
        assertEquals("$OPEN_DB main", model.getTitle())
    }

    @Test
    fun `verifyPassword should set incorrectPassword to true if there is TokenValidation error`(){
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertTrue(spyModel.isIncorrectPassword())
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
        assertTrue(spyModel.isCorrupted())
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
        assertTrue(spyModel.isOpened())
    }

    @Test
    fun `verifyPassword should set loading to true`(){
        runBlocking {
            spyModel.verifyPassword(password)
        }
        assertTrue(spyModel.isLoading())
    }

    @Test
    fun `verifyPassword should set loading to false when password is incorrect`(){
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        runBlocking {
            spyModel.verifyPassword(faker.lorem().sentence())
        }
        assertFalse(spyModel.isLoading())
    }

    @Test
    fun `startPasswordCheck should not start verifyPassword if passwordJob already active`(){
        // mock passwordJob
        val mockJob = mock<Job>{ on {isActive} doReturn true }
        spyModel.passwordJob = mockJob

        spyModel.startPasswordCheck(password)

        runBlocking {
            verify(spyModel, never()).verifyPassword(password)
        }
    }

    @Test
    fun `startPasswordCheck should start verifyPassword if passwordJob isn't active`(){
        // mock passwordJob
        val mockJob = mock<Job>{ on {isActive} doReturn false }
        spyModel.passwordJob = mockJob

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