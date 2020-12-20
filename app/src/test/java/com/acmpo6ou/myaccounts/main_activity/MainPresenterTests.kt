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

import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainModelInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.spy

class MainPresenterTests {
    lateinit var presenter: MainPresenter
    lateinit var spyPresenter: MainPresenter
    lateinit var view: MainActivityInter
    private val faker = Faker()

    @Before
    fun setup(){
        view = mock()
        whenever(view.ACCOUNTS_DIR).thenReturn("")

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
        val location = faker.file().fileName()
        spyPresenter.model = model

        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                16, // size of bin file should be exactly 16
                100 // size of db file should be not less then 100
        )

        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)
        whenever(model.getSizes(location)).thenReturn(sizesList)

        spyPresenter.checkTarFile(location)
        verify(spyPresenter).importDatabase(location)
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
}