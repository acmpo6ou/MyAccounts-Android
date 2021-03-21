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

package com.acmpo6ou.myaccounts.main_activity

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.MainActivityInter
import com.acmpo6ou.myaccounts.database.MainModelInter
import com.acmpo6ou.myaccounts.database.MainPresenter
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.spy
import java.io.File
import java.time.LocalDate
import java.util.*

class MainPresenterTests {
    lateinit var presenter: MainPresenter
    lateinit var spyPresenter: MainPresenter

    lateinit var view: MainActivityInter
    lateinit var model: MainModelInter
    lateinit var mockPrefs: SharedPreferences

    private val locationUri: Uri = mock()
    private val accountsDir = "/dev/shm/accounts"
    val app = MyApp()

    @Before
    fun setup() {
        app.databases = mutableListOf(Database("test", "123"))
        val resolver: ContentResolver = mock()
        val context: Context = mock { on { contentResolver } doReturn resolver }

        mockPrefs = SPMockBuilder().createSharedPreferences()
        model = mock()
        doReturn(false).whenever(model).isDatabaseSaved(app.databases[0], app)

        view = mock {
            on { ACCOUNTS_DIR } doReturn accountsDir
            on { app } doReturn MainPresenterTests@app
            on { myContext } doReturn context
            on { prefs } doReturn mockPrefs
        }

        presenter = MainPresenter(view)
        presenter.model = model
        spyPresenter = spy(presenter)
    }

    private fun mockCorrectModel() {
        val filesList = listOf("main", "main")
        val sizesList = listOf(
            100, // size of db file should be not less then 100
            16
        ) // size of bin file should be exactly 16

        // mock model to return correct file sizes, count and names
        model = mock {
            on { getNames(locationUri) } doReturn filesList
            on { countFiles(locationUri) } doReturn 2
            on { getSizes(locationUri) } doReturn sizesList
        }
    }

    @Test
    fun `importSelected should call view importDialog`() {
        presenter.importSelected()
        verify(view).importDialog()
    }

    @Test
    fun `autocheckForUpdates should call checkUpdatesSelected if it's time to autocheck`() {
        doReturn(true).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter).checkUpdatesSelected(true)
    }

    @Test
    fun `autocheckForUpdates should not call checkUpdatesSelected if it's not time to autocheck`() {
        doReturn(false).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter, never()).checkUpdatesSelected(anyBoolean())
        verify(spyPresenter, never()).checkUpdatesSelected()
    }

    @Test
    fun `isTimeToUpdate should return true if app didn't check for updates today`() {
        val date = LocalDate.MIN.toEpochDay()
        mockPrefs.edit().putLong("last_update_check", date).commit()
        assertTrue(presenter.isTimeToUpdate())
    }

    @Test
    fun `isTimeToUpdate should return false if app did check for updates today`() {
        val date = LocalDate.now().toEpochDay()
        mockPrefs.edit().putLong("last_update_check", date).commit()
        assertFalse(presenter.isTimeToUpdate())
    }

    @Test
    fun `isTimeToUpdate should set last_update_check`() {
        // last time we checked for updates was long time ago
        val date = LocalDate.MIN.toEpochDay()
        mockPrefs.edit().putLong("last_update_check", date).commit()
        presenter.isTimeToUpdate()

        // last_update_check should be set to today date because last time we checked for
        // updates were today
        val today = LocalDate.now()
        val lastCheck = LocalDate.ofEpochDay(
            mockPrefs.getLong("last_update_check", 0L)
        )
        assertEquals(today, lastCheck)
    }

    @Test
    fun `checkTarFile should call importDatabase if there are no errors`() {
        mockCorrectModel()
        spyPresenter.model = model
        doNothing().`when`(spyPresenter).importDatabase(locationUri)

        spyPresenter.checkTarFile(locationUri)
        verify(spyPresenter).importDatabase(locationUri)
        verify(view, never()).showError(anyString(), anyString())
    }

    @Test
    fun `fixSrcFolder should create SRC_DIR if it doesn't exist`() {
        // here we delete accounts folder if it already exists to ensure that it will
        // be empty as is needed for our tests
        val accountsFolder = File(accountsDir)
        if (accountsFolder.exists()) accountsFolder.deleteRecursively()

        presenter.fixSrcFolder()

        // the src folder should be created
        val srcDir = File("$accountsDir/src")
        assertTrue(srcDir.exists())
    }

    /**
     * Helper method used by next 2 tests. Simulates importing of database named `main`.
     */
    private fun importMainDatabase() {
        mockCorrectModel()
        doReturn("main").whenever(model).importDatabase(locationUri)
        presenter.model = model
        presenter.importDatabase(locationUri)
    }

    @Test
    fun `importDatabase should add imported database to the list`() {
        importMainDatabase()
        assertTrue(Database("main") in presenter.databases)
    }

    @Test
    fun `importDatabase should call notifyChanged`() {
        importMainDatabase()
        verify(view).notifyChanged(0)
    }

    @Test
    fun `backPressed should call confirmBack when there are unsaved databases`() {
        doReturn(false).whenever(model).isDatabaseSaved(any(), eq(app))
        presenter.backPressed()

        verify(view).confirmBack()
        verify(view, never()).showExitTip()
        verify(view, never()).goBack()
    }

    @Test
    fun `backPressed should call showExitTip when there are opened databases`() {
        doReturn(true).whenever(model).isDatabaseSaved(any(), eq(app))
        presenter.backPressed()

        verify(view).showExitTip()
        verify(view, never()).confirmBack()
        verify(view, never()).goBack()
    }

    @Test
    fun `backPressed should call goBack when there are no opened databases`() {
        app.databases = mutableListOf(Database("main"))
        presenter.backPressed()

        verify(view).goBack()
        verify(view, never()).showExitTip()
        verify(view, never()).confirmBack()
    }

    @Test
    fun `saveSelected should save all unsaved databases`() {
        app.databases = mutableListOf(
            Database("main"),
            Database("test", "123"), // unsaved
            Database("saved", "123")
        )
        doReturn(false).whenever(model).isDatabaseSaved(app.databases[1], app)
        doReturn(true).whenever(model).isDatabaseSaved(app.databases[2], app)

        presenter.saveSelected()
        verify(model).saveDatabase("test", app.databases[1], app)
        verify(model, never()).saveDatabase("main", app.databases[0], app)
        verify(model, never()).saveDatabase("saved", app.databases[2], app)
    }
}
