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

package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesModelInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenter
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import java.io.File

open class DatabasesPresenterTest{
    lateinit var view: DatabaseFragmentInter
    lateinit var model: DatabasesModelInter
    lateinit var presenter: DatabasesPresenter

    lateinit var location: String
    val faker = Faker()
    val salt = "0123456789abcdef".toByteArray()

    @Before
    fun setUp(){
        view = mock()
        model = mock()

        presenter = DatabasesPresenter(view)
        presenter.model = model
        presenter.databases = listOf(
                Database("main"),
                Database("test", "123", salt, mapOf())
        )
    }

    fun callExportDatabase(){
        location = faker.file().fileName()
        presenter.exportIndex = 1
        presenter.exportDatabase(location)
    }
}

class DatabasesPresenterTests: DatabasesPresenterTest() {
    @Test
    fun `exportSelected should call exportDialog`(){
        presenter.exportSelected(0)
        verify(view).exportDialog(0)
    }

    @Test
    fun `exportSelected should save database index to exportIndex`(){
        presenter.exportSelected(1)
        assertEquals(1, presenter.exportIndex)
    }

    @Test
    fun `deleteSelected should call confirmDelete`(){
        presenter.deleteSelected(0)
        verify(view).confirmDelete(0)
    }

    @Test
    fun `closeSelected should call closeDatabase when database is saved`(){
        // mock isDatabaseSaved to return true that will mean that database is saved
        // so presenter should call closeDatabase in this condition
        val presenterSpy = spy(presenter)
        doReturn(true).`when`(presenterSpy).isDatabaseSaved(0)

        presenterSpy.closeSelected(0)
        verify(presenterSpy).closeDatabase(0)
    }

    @Test
    fun `closeSelected should call confirmClose when database isn't saved`(){
        // mock isDatabaseSaved to return false that will mean that database is saved
        // so presenter should call confirmClose in this condition
        val presenterSpy = spy(presenter)
        doReturn(false).`when`(presenterSpy).isDatabaseSaved(0)

        presenterSpy.closeSelected(0)
        verify(view).confirmClose(0)
    }

    @Test
    fun `editSelected should call navigateToEdit passing through serialized database`(){
        // mock model.dumps() to return fake serialized string
        val expectedJson = faker.lorem().sentence()
        whenever(model.dumps(anyMap())).thenReturn(expectedJson)

        presenter.editSelected(0)
        verify(view).navigateToEdit(expectedJson)
    }

    @Test
    fun `isDatabaseSaved should return false when database is different from the one on disk`(){
        // here database on disk is different then database in memory
        val diskDatabase = Database(
            "test",
            "123",
            salt,
            getDatabaseMap()
        )
        whenever(model.openDatabase(presenter.databases[1])).thenReturn(diskDatabase)

        assertFalse(presenter.isDatabaseSaved(1))
    }

    @Test
    fun `isDatabaseSaved should return true when database isn't different from the one on disk`(){
        // here database on disk is exactly the same as database in memory
        val diskDatabase = Database(
                "test",
                "123",
                salt,
                mapOf())
        whenever(model.openDatabase(presenter.databases[1])).thenReturn(diskDatabase)

        assertTrue(presenter.isDatabaseSaved(1))
    }

    @Test
    fun `closeDatabase should reset database password`(){
        presenter.closeDatabase(1)
        assertEquals(null, presenter.databases[1].password)
    }

    @Test
    fun `closeDatabase should call notifyChanged`(){
        presenter.closeDatabase(1)
        verify(view).notifyChanged(1)
    }

    @Test
    fun `exportDatabase should call model exportDatabase passing name and location`(){
        callExportDatabase()
        verify(model).exportDatabase("test", location)
    }

    @Test
    fun `exportDatabase should call showSuccess when there are no errors`(){
        callExportDatabase()
        verify(view).showSuccess()
    }

    @Test
    fun `deleteDatabase should call model deleteDatabase passing name`(){
        presenter.deleteDatabase(0)
        verify(model).deleteDatabase("main")
    }

    @Test
    fun `deleteDatabase should call showSuccess when there are no errors`(){
        presenter.deleteDatabase(0)
        verify(view).showSuccess()
    }

    @Test
    fun `deleteDatabase should call notifyRemoved`(){
        presenter.deleteDatabase(0)
        verify(view).notifyRemoved(0)
    }

    @Test
    fun `isDatabaseSaved should return false when NoSuchFileException occurred`(){
        whenever(model.openDatabase(presenter.databases[1])).thenAnswer{
            throw NoSuchFileException(File(""))
        }
        assertFalse(presenter.isDatabaseSaved(1))
    }
}