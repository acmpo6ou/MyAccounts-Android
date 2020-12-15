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

import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class MainPresenterTests {
    lateinit var presenter: MainPresenter
    lateinit var spyPresenter: MainPresenter
    lateinit var view: MainActivityInter

    @Before
    fun setup(){
        view = mock()
        presenter = MainPresenter(view)
        spyPresenter = spy(presenter)
    }

    @Test
    fun `importSelected should call view importDialog`(){
        presenter.importSelected()
        verify(view).importDialog()
    }

    @Test
    fun `checkUpdatesSelected should call view noUpdates when checkForUpdates returns false`(){
        doReturn(false).`when`(spyPresenter).checkForUpdates()
        spyPresenter.checkUpdatesSelected()

        // verify that only noUpdates was called and not startUpdatesActivity
        verify(view).noUpdates()
        verify(view, never()).startUpdatesActivity()
    }

    @Test
    fun `checkUpdatesSelected should call view startUpdatesActivity when checkForUpdates returns true`(){
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
}