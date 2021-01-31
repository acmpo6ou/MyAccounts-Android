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

package com.acmpo6ou.myaccounts.superclass

import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.SuperActivityInter
import com.acmpo6ou.myaccounts.core.SuperPresenter
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

// SuperPresenter is abstract
open class TestPresenter : SuperPresenter() {
    override val view: SuperActivityInter = mock()
}

class SuperPresenterTests {
    private lateinit var spyPresenter: TestPresenter
    lateinit var presenter: TestPresenter
    val view get() = spyPresenter.view

    @Before
    fun setup(){
        presenter = TestPresenter()
        spyPresenter = spy(presenter)
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