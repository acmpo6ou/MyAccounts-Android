/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.databases_list

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt

class DatabasesPresenterTests : DatabasesPresenterTest() {
    @Before
    fun setup() {
        setupPresenter()
    }

    @Test
    fun `exportSelected should call exportDialog`() {
        presenter.exportSelected(presenter.databases[0])
        verify(view).exportDialog(presenter.databases[0])
    }

    @Test
    fun `exportDatabase should call model exportDatabase passing name and location`() {
        callExportDatabase()
        verify(model).exportDatabase("test", locationUri)
    }

    @Test
    fun `exportDatabase should call showSuccess when there are no errors`() {
        callExportDatabase()
        verify(view).showSuccess()
    }

    @Test
    fun `editSelected should call navigateToRename if database is closed`() {
        presenter.editSelected(presenter.databases[0])
        verify(view).navigateToRename(0)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `editSelected should call navigateToEdit if database is opened`() {
        presenter.editSelected(presenter.databases[1])
        verify(view).navigateToEdit(1)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `closeSelected should call closeDatabase when database is saved`() {
        val spyPresenter = spy(presenter)
        doReturn(true).`when`(model).isDatabaseSaved(any())

        spyPresenter.closeSelected(presenter.databases[0])
        verify(spyPresenter).closeDatabase(presenter.databases[0])
    }

    @Test
    fun `closeSelected should call view confirmClose when database isn't saved`() {
        doReturn(false).`when`(model).isDatabaseSaved(any())
        presenter.closeSelected(presenter.databases[0])
        verify(view).confirmClose(presenter.databases[0])
    }

    @Test
    fun `closeDatabase should remove cryptography key from cache`() {
        presenter.closeDatabase(presenter.databases[1])
        assertTrue(app.keyCache.isEmpty())
    }

    @Test
    fun `closeDatabase should reset database password`() {
        presenter.closeDatabase(presenter.databases[1])
        assertNull(presenter.databases[1].password)
    }

    @Test
    fun `closeDatabase should call notifyChanged`() {
        presenter.closeDatabase(presenter.databases[1])
        verify(view).notifyChanged(1)
    }

    @Test
    fun `openDatabase should navigate to OpenDatabaseFragment if database is closed`() {
        // first database is closed
        presenter.openDatabase(presenter.databases[0])
        verify(view).navigateToOpen(0)

        // startDatabase should not be called
        verify(view, never()).startDatabase(anyInt())
    }

    @Test
    fun `openDatabase should call startDatabase passing database index if database is opened`() {
        // second database is opened
        presenter.openDatabase(presenter.databases[1])
        verify(view).startDatabase(1)

        // navigateToOpen should not be called
        verify(view, never()).navigateToOpen(anyInt())
    }

    @Test
    fun `deleteSelected should call confirmDelete`() {
        presenter.deleteSelected(presenter.databases[0])
        verify(view).confirmDelete(presenter.databases[0])
    }

    @Test
    fun `deleteDatabase should call model deleteDatabase passing name`() {
        presenter.deleteDatabase(presenter.databases[0])
        verify(model).deleteDatabase("main")
    }

    @Test
    fun `deleteDatabase should remove database from list`() {
        presenter.deleteDatabase(presenter.databases[0])
        assertEquals("test", presenter.databases[0].name)
        assertEquals(1, presenter.databases.size)
    }

    @Test
    fun `deleteDatabase should call notifyRemoved`() {
        presenter.deleteDatabase(presenter.databases[0])
        verify(view).notifyRemoved(0)
    }

    @Test
    fun `deleteDatabase should remove corresponding key from cache if it is there`() {
        // there is a key for second database (it's opened) and it has to be removed
        presenter.deleteDatabase(presenter.databases[1])
        assertTrue(app.keyCache.isEmpty())
    }

    @Test
    fun `deleteDatabase should not remove anything from cache if there is no key to remove`() {
        // there is no key for first database (it's closed), so nothing should be removed
        presenter.deleteDatabase(presenter.databases[0])
        assertEquals(1, app.keyCache.size)
    }
}
