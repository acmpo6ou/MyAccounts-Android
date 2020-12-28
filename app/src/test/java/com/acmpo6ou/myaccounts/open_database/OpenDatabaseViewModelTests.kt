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

package com.acmpo6ou.myaccounts.open_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.macasaet.fernet.TokenValidationException
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OpenDatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = OpenDatabaseViewModel()
    private val spyModel = spy(model)
    private val faker = Faker()

    val SRC_DIR = "sampledata/src/"
    val salt = "0123456789abcdef".toByteArray()
    val app = MyApp()

    @Before
    fun setup(){
        app.databases = mutableListOf(Database("main"))
    }

    @Test
    fun `setDatabase should set title`(){
        model.setDatabase(app, 0, SRC_DIR)
        assertEquals("Open main", model.getTitle())
    }

    @Test
    fun `verifyPassword should set incorrectPassword to true if there is TokenValidation error`(){
        spyModel.setDatabase(app, 0, SRC_DIR)
        doAnswer{
            throw TokenValidationException("")
        }.whenever(spyModel).openDatabase(app.databases[0])

        spyModel.verifyPassword(faker.lorem().sentence())
        assertTrue(spyModel.isIncorrectPassword())
    }

    @Test
    fun `verifyPassword should set corrupted to true if there is deserialization error`(){
        spyModel.setDatabase(app, 0, SRC_DIR)
        doAnswer{
            // here we throw Exception instead of JsonDecodingException because
            // JsonDecodingException is private
            throw Exception("JsonDecodingException")
        }.whenever(spyModel).openDatabase(app.databases[0])

        spyModel.verifyPassword(faker.lorem().sentence())
        assertTrue(spyModel.isCorrupted())
    }

    @Test
    fun `verifyPassword should save deserialized Database to list`(){
        spyModel.setDatabase(app, 0, SRC_DIR)
        val expectedDatabase = Database("main", "main", salt, mapOf())
        val database = app.databases[0].copy()
        database.password = "main"
        database.salt = salt
        doReturn(expectedDatabase).whenever(spyModel).openDatabase(eq(database))

        spyModel.verifyPassword("main")
        assertEquals(expectedDatabase, app.databases[0])
    }
}