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

package com.acmpo6ou.myaccounts.main_activity

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainModelInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.spy
import java.io.File

class MainPresenterTests {
    lateinit var presenter: MainPresenter
    lateinit var spyPresenter: MainPresenter
    lateinit var view: MainActivityInter

    private val accountsDir = "/dev/shm/accounts"

    @Before
    fun setup(){
        view = mock()
        whenever(view.ACCOUNTS_DIR).thenReturn(accountsDir)

        val context: Context = mock()
        val resolver: ContentResolver = mock()
        whenever(context.contentResolver).thenReturn(resolver)
        whenever(view.myContext).thenReturn(context)

        presenter = MainPresenter(view)
        spyPresenter = spy(presenter)
    }

    @Test
    fun `importSelected should call view importDialog`(){
        presenter.importSelected()
        verify(view).importDialog()
    }

    @Test
    fun `checkUpdatesSelected should call noUpdates`(){
        doReturn(false).`when`(spyPresenter).checkForUpdates()
        spyPresenter.checkUpdatesSelected()

        // verify that only noUpdates was called and not startUpdatesActivity
        verify(view).noUpdates()
        verify(view, never()).startUpdatesActivity()
    }

    @Test
    fun `checkUpdatesSelected should call startUpdatesActivity`(){
        doReturn(true).`when`(spyPresenter).checkForUpdates()
        spyPresenter.checkUpdatesSelected()

        // verify that only startUpdatesActivity was called and not noUpdates
        verify(view).startUpdatesActivity()
        verify(view, never()).noUpdates()
    }

    @Test
    fun `autocheckForUpdates should call checkUpdatesSelected`(){
        doReturn(true).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter).checkUpdatesSelected()
    }

    @Test
    fun `autocheckForUpdates should not call checkUpdatesSelected`(){
        doReturn(false).whenever(spyPresenter).isTimeToUpdate()
        spyPresenter.autocheckForUpdates()
        verify(spyPresenter, never()).checkUpdatesSelected()
    }

    @Test
    fun `checkTarFile should call importDatabase if there are no errors`(){
        // mock model to return correct file sizes, count and names
        val model: MainModelInter = mock()
        val locationUri: Uri = mock()
        spyPresenter.model = model

        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                16, // size of bin file should be exactly 16
                100 // size of db file should be not less then 100
        )

        whenever(model.getNames(locationUri)).thenReturn(filesList)
        whenever(model.countFiles(locationUri)).thenReturn(2)
        whenever(model.getSizes(locationUri)).thenReturn(sizesList)

        spyPresenter.checkTarFile(locationUri)
        verify(spyPresenter).importDatabase(locationUri)
        verify(view, never()).showError(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
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
}