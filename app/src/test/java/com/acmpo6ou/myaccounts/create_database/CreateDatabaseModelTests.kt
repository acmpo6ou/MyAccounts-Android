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

package com.acmpo6ou.myaccounts.create_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.CreateDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateDatabaseModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = CreateDatabaseViewModel()
    lateinit var spyModel: CreateDatabaseViewModel

    val faker = Faker()
    private val name = faker.str()
    private val password = faker.str()
    private val salt = "1234567890abcdef".toByteArray()
    private val db = Database(name, password, salt)

    val SRC_DIR = "sampledata/src/"
    val titleStart = faker.str()

    @Before
    fun setup(){
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, titleStart, SRC_DIR)
        spyModel = spy(model){
            on{generateSalt()} doReturn salt
        }
        doNothing().whenever(spyModel).createDatabase(any())
    }

    @Test
    fun `fixName should remove all unsupported characters`(){
        val name = model.fixName("This is (test)/.\\-_-")
        assertEquals("Thisis(test).-_-", name)
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
    fun `createPressed should call createDatabase`(){
        spyModel.createPressed(name, password)
        verify(spyModel).createDatabase(db)
    }

    @Test
    fun `createPressed should handle any error`(){
        val msg = faker.str()
        val exception = Exception(msg)
        whenever(spyModel.createDatabase(db))
                .doAnswer{
                    throw exception
                }

        spyModel.createPressed(name, password)
        assertEquals(exception.toString(), spyModel.errorMsg)
    }

    @Test
    fun `createPressed should add created Database to the list`(){
        spyModel.createPressed(name, password)
        assertTrue(db in spyModel.databases)
    }

    @Test
    fun `createPressed should set createdIndex`(){
        spyModel.createPressed(name, password)
        val index = spyModel.databases.indexOf(db)
        assertEquals(index, spyModel.createdIndex)
    }
}