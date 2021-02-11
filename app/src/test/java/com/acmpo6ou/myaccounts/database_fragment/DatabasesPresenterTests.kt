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

package com.acmpo6ou.myaccounts.database_fragment

import android.content.Context
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt


class DatabasesPresenterTests: DatabasesPresenterTest() {
    private val context: Context = mock()

    @Before
    fun setup(){
        whenever(context.contentResolver).thenReturn(contextResolver)
        whenever(view.myContext).thenReturn(context)
        setupPresenter()
    }

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
        val presenterSpy = spy(presenter)
        doReturn(true).`when`(presenterSpy).isDatabaseSaved(any(), eq(app))

        presenterSpy.closeSelected(0)
        verify(presenterSpy).closeDatabase(0)
    }

    @Test
    fun `closeSelected should call confirmClose when database isn't saved`(){
        val presenterSpy = spy(presenter)
        doReturn(false).`when`(presenterSpy).isDatabaseSaved(any(), eq(app))

        presenterSpy.closeSelected(0)
        verify(view).confirmClose(0)
    }

    @Test
    fun `editSelected should call navigateToEdit passing through database index`(){
        presenter.editSelected(0)
        verify(view).navigateToEdit(0)
    }

    @Test
    fun `closeDatabase should remove cryptography key from cache`(){
        presenter.closeDatabase(1)
        assertTrue(app.keyCache.isEmpty())
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
        verify(model).exportDatabase("test", locationUri)
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
    fun `deleteDatabase should remove database from list`(){
        presenter.deleteDatabase(0)
        // first database should no longer be `main`
        assertEquals("test", presenter.databases[0].name)
    }

    @Test
    fun `deleteDatabase should call notifyRemoved`(){
        presenter.deleteDatabase(0)
        verify(view).notifyRemoved(0)
    }

    @Test
    fun `deleteDatabase should remove corresponding key from cache if it there`(){
        // there is a key for second database (it's opened) and it has to be removed
        presenter.deleteDatabase(1)
        assertTrue(app.keyCache.isEmpty())
    }

    @Test
    fun `deleteDatabase should not remove anything from cache if there is no key to remove`(){
        // there is no key for first database (it's closed), so nothing should be removed
        presenter.deleteDatabase(0)
        assertEquals(1, app.keyCache.size)
    }

    @Test
    fun `openDatabase should navigate to OpenDatabaseFragment if database is closed`(){
        // first database is closed
        presenter.openDatabase(0)
        verify(view).navigateToOpen(0)

        // startDatabase should not be called
        verify(view, never()).startDatabase(anyInt())
    }

    @Test
    fun `openDatabase should call startDatabase passing database index if database is opened`(){
        // second database is opened
        presenter.openDatabase(1)
        verify(view).startDatabase(1)

        // navigateToOpen should not be called
        verify(view, never()).navigateToOpen(1)
    }
}