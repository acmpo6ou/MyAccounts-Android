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
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.core.createDatabaseUtil
import com.acmpo6ou.myaccounts.core.deriveKeyUtil
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.database.EditDatabaseViewModel
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount

class EditDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = EditDatabaseViewModel()
    lateinit var spyModel: EditDatabaseViewModel
    lateinit var testModel: EditDatabaseViewModel
    lateinit var app: MyApp

    private val oldName = "main"
    private val name = "clean_name"
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup(){
        app = MyApp()
        app.databases = mutableListOf(Database(oldName, password, salt))
        app.keyCache = mutableMapOf(password to deriveKeyUtil(password, salt))

        model.initialize(app, SRC_DIR, faker.str(), 0)
        spyModel = spy(model){ on{generateSalt()} doReturn salt }
        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.defaultDispatcher = Dispatchers.Unconfined

        // inherit from EditDatabaseViewModel to override saveDatabase because it's a
        // coroutine and can't be mocked
        open class TestModel : EditDatabaseViewModel(){
            override fun saveDatabase(oldName: String, database: Database) =
                    viewModelScope.async (Dispatchers.Unconfined) {
                    }
        }
        testModel = TestModel()
        testModel.initialize(app, SRC_DIR, faker.str(), 0)
    }

    @Test
    fun `if name of Database didn't change through editing`(){
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
        spyModel = spy(testModel)
        runBlocking {
            spyModel.apply(name, password)
        }
        verify(spyModel).saveDatabase(oldName, db)
    }

    @Test
    fun `apply should use fixName`(){
        spyModel = spy(testModel)
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

        // inherit from EditDatabaseViewModel to override saveDatabase because it's a
        // coroutine and can't be mocked
        class TestModel : EditDatabaseViewModel(){
            override fun saveDatabase(oldName: String, database: Database) =
                    viewModelScope.async (Dispatchers.Unconfined) {
                        throw exception
                    }
        }
        val model = TestModel()
        model.initialize(app, SRC_DIR, faker.str(), 0)

        runBlocking {
            model.apply(name, password)
        }
        assertEquals(exception.toString(), model.errorMsg)
        assertFalse(model.loading)
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
        assertFalse(deriveKeyUtil(password, salt) in app.keyCache)
    }

    @Test
    fun `apply should set finished to true after successful save of database`(){
        runBlocking {
            testModel.apply(name, password)
        }
        assertTrue(testModel.finished)
    }

    @Test
    fun `apply should set loading to true`(){
        runBlocking {
            testModel.apply(name, password)
        }
        assertTrue(testModel.loading)
    }

    /**
     * Helper method used by saveDatabase test to create old database and to call
     * saveDatabase passing through new database.
     */
    private fun setUpSaveDatabase(){
        // this database will be deleted by saveDatabase
        val db = Database("test", "123", salt)
        createDatabaseUtil(db, SRC_DIR, MyApp())

        // this database will be created by saveDatabase
        val newDb = Database("test2", "321",
                salt.reversedArray(), getDatabaseMap())

        // save newDb deleting db
        runBlocking {
            model.saveDatabase("test", newDb).await()
        }
    }

    /**
     * This method decrypts given string using password `123` and [salt].
     *
     * @param[string] string to decrypt.
     * @return decrypted string.
     */
    private fun decryptStr(string: String): String{
        val key = deriveKeyUtil("321", salt.reversedArray())
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }
        val token = Token.fromString(string)
        return token.validateAndDecrypt(key, validator)
    }

    @Test
    fun `saveDatabase should delete files of old database`(){
        setUpSaveDatabase()

        // check that there is no longer test.db and test.bin files
        val oldDb = File("$SRC_DIR/test.db")
        val oldBin = File("$SRC_DIR/test.bin")

        assertFalse(".db file of old database is not deleted by saveDatabase method!",
                oldDb.exists())
        assertFalse(".bin file of old database is not deleted by saveDatabase method!",
                oldBin.exists())
    }

    @Test
    fun `saveDatabase should create new, non empty database file`(){
        setUpSaveDatabase()

        // this is a .db file that saveDatabase should create for us
        val actualDb = File("$SRC_DIR/test2.db").readBytes()

        // here we decrypt data saved to .db file to check that it was encrypted correctly
        val data = decryptStr(String(actualDb))
        assertEquals("saveDatabase creates incorrect database!",
                jsonDatabase, data)
    }

    @Test
    fun `saveDatabase should create new, non empty salt file`(){
        setUpSaveDatabase()

        // this is a .bin file that saveDatabase should create for us
        val actualBin = File("$SRC_DIR/test2.bin").readBytes()

        // .bin file must have appropriate content (i.e. salt)
        assertEquals("saveDatabase created .bin file with incorrect salt!",
                String(salt.reversedArray()), String(actualBin))
    }
}