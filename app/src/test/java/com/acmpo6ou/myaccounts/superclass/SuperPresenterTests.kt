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

import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.SuperActivityInter
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

val fakeApp = MyApp()

open class TestSuperPresenter : SuperPresenter() {
    override val view: SuperActivityInter = mock{ on{app} doReturn fakeApp }

    override fun backPressed() {
    }
    override fun saveSelected() {
    }
}

class SuperPresenterTests {
    private lateinit var presenter: TestSuperPresenter
    private val latestVersion = Faker().str()

    @Before
    fun setup(){
        presenter = TestSuperPresenter()
    }

    @Test
    fun `checkForUpdates should call noUpdates when updates aren't available`(){
        // here we pass version that is exactly the same as installed one, so there are
        // no updates available
        presenter.checkForUpdates(BuildConfig.VERSION_NAME)
        verify(presenter.view).noUpdates()
        verify(presenter.view, never()).startUpdatesActivity()
    }

    @Test
    fun `checkForUpdates should call startUpdatesActivity when updates are available`(){
        // here we pass different version then the installed one, so there are
        // updates available
        presenter.checkForUpdates(latestVersion)
        verify(presenter.view).startUpdatesActivity()
        verify(presenter.view, never()).noUpdates()
    }

    @Test
    fun `checkForUpdates should save latestVersion to MyApp when updates are available`(){
        presenter.checkForUpdates(latestVersion)
        assertEquals(latestVersion, fakeApp.latestVersion)
    }

    @Test
    fun `navigateToChangelog should call navigateTo`(){
        presenter.navigateToChangelog()
        verify(presenter.view).navigateTo(R.id.actionChangelog)
    }

    @Test
    fun `navigateToSettings should call navigateTo`(){
        presenter.navigateToSettings()
        verify(presenter.view).navigateTo(R.id.actionSettings)
    }

    @Test
    fun `navigateToAbout should call navigateTo`(){
        presenter.navigateToAbout()
        verify(presenter.view).navigateTo(R.id.actionAbout)
    }
}