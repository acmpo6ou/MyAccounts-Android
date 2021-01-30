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
import android.net.Uri
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.MainActivityInter
import com.acmpo6ou.myaccounts.database.MainModelInter
import com.acmpo6ou.myaccounts.database.MainPresenter
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.spy
import java.io.File

class MainPresenterTests {
    lateinit var presenter: MainPresenter
    lateinit var spyPresenter: MainPresenter

    lateinit var view: MainActivityInter
    lateinit var model: MainModelInter

    private val locationUri: Uri = mock()
    val app = MyApp()

    private val accountsDir = "/dev/shm/accounts"

    @Before
    fun setup(){
        val resolver: ContentResolver = mock()
        val context: Context = mock{ on{contentResolver} doReturn resolver }

        view = mock{
            on{ACCOUNTS_DIR} doReturn accountsDir
            on{app} doReturn MainPresenterTests@app
            on{myContext} doReturn context
        }

        presenter = MainPresenter(view)
        spyPresenter = spy(presenter)
    }

    private fun mockCorrectModel(){
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                100, // size of db file should be not less then 100
                16, // size of bin file should be exactly 16
        )

        // mock model to return correct file sizes, count and names
        model = mock{
            on{getNames(locationUri)} doReturn filesList
            on{countFiles(locationUri)} doReturn 2
            on{getSizes(locationUri)} doReturn sizesList
        }
    }

    @Test
    fun `importSelected should call view importDialog`(){
        presenter.importSelected()
        verify(view).importDialog()
    }

    @Test
    fun `checkUpdatesSelected should call noUpdates when updates aren't available`(){
        doReturn(false).`when`(spyPresenter).checkForUpdates()
        spyPresenter.checkUpdatesSelected()

        verify(view).noUpdates()
        verify(view, never()).startUpdatesActivity()
    }

    @Test
    fun `checkUpdatesSelected should call startUpdatesActivity when updates are available`(){
        doReturn(true).`when`(spyPresenter).checkForUpdates()
        spyPresenter.checkUpdatesSelected()

        verify(view).startUpdatesActivity()
        verify(view, never()).noUpdates()
    }

    @Test
    fun `autocheckForUpdates should call checkUpdatesSelected if it's time to autocheck`(){
        doReturn(true).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter).checkUpdatesSelected()
    }

    @Test
    fun `autocheckForUpdates should not call checkUpdatesSelected if it's not time to autocheck`(){
        doReturn(false).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter, never()).checkUpdatesSelected()
    }

    @Test
    fun `checkTarFile should call importDatabase if there are no errors`(){
        mockCorrectModel()
        spyPresenter.model = model
        doNothing().`when`(spyPresenter).importDatabase(locationUri)

        spyPresenter.checkTarFile(locationUri)
        verify(spyPresenter).importDatabase(locationUri)
        verify(view, never()).showError(anyString(), anyString())
    }

    @Test
    fun `navigateToChangelog should call navigateTo`(){
        presenter.navigateToChangelog()
        verify(view).navigateTo(R.id.actionChangelog)
    }

    @Test
    fun `navigateToSettings should call navigateTo`(){
        presenter.navigateToSettings()
        verify(view).navigateTo(R.id.actionSettings)
    }

    @Test
    fun `navigateToAbout should call navigateTo`(){
        presenter.navigateToAbout()
        verify(view).navigateTo(R.id.actionAbout)
    }

    @Test
    fun `fixSrcFolder should create SRC_DIR if it doesn't exist`(){
        // here we delete accounts folder if it already exists to ensure that it will
        // be empty as is needed for our tests
        val accountsFolder = File(accountsDir)
        if(accountsFolder.exists()){
            accountsFolder.deleteRecursively()
        }

        presenter.fixSrcFolder()

        // the src folder should be created
        val srcDir = File("$accountsDir/src")
        assertTrue(srcDir.exists())
    }

    /**
     * Helper method used by next 2 tests. Simulates importing of database named `main`.
     */
    private fun importMainDatabase(){
        mockCorrectModel()
        doReturn("main").whenever(model).importDatabase(locationUri)
        presenter.model = model
        presenter.importDatabase(locationUri)
    }

    @Test
    fun `importDatabase should add imported database to the list`(){
        importMainDatabase()
        assertTrue(Database("main") in presenter.databases)
    }

    @Test
    fun `importDatabase should call notifyChanged`(){
        importMainDatabase()
        verify(view).notifyChanged(0)
    }
}