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
import com.acmpo6ou.myaccounts.core.superclass.SuperActivityI
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import retrofit2.Call

class SuperPresenterTests {
    private lateinit var presenter: TestSuperPresenter
    private val latestVersion = Faker().str()

    @Before
    fun setup() {
        val callback: Call<ResponseBody> = mock()
        presenter = TestSuperPresenter()
        presenter.service = mock { on { getLatestRelease() } doReturn callback }
    }

    @Test
    fun `checkUpdatesSelected should not get release version when there is no internet`() {
        doReturn(false).whenever(presenter.view).isInternetAvailable()
        presenter.checkUpdatesSelected()
        verify(presenter.service, never()).getLatestRelease()
    }

    @Test
    fun `checkUpdatesSelected should get latest release when there is internet`() {
        doReturn(true).whenever(presenter.view).isInternetAvailable()
        presenter.checkUpdatesSelected()
        verify(presenter.service).getLatestRelease()
    }

    @Test
    fun `checkUpdatesSelected should call noInternetConnection when there is no internet`() {
        doReturn(false).whenever(presenter.view).isInternetAvailable()
        presenter.checkUpdatesSelected()
        verify(presenter.view).noInternetConnection()
    }

    @Test
    fun `checkUpdatesSelected should not call noInternetConnection when there is internet`() {
        doReturn(true).whenever(presenter.view).isInternetAvailable()
        presenter.checkUpdatesSelected()
        verify(presenter.view, never()).noInternetConnection()
    }

    @Test
    fun `checkForUpdates should call noUpdates when updates aren't available`() {
        // here we pass version that is exactly the same as installed one, so there are
        // no updates available
        presenter.checkForUpdates(BuildConfig.VERSION_NAME)
        verify(presenter.view).noUpdates()
        verify(presenter.view, never()).startUpdatesActivity(anyString())
    }

    @Test
    fun `checkForUpdates should call startUpdatesActivity when updates are available`() {
        // here we pass different version then the installed one, so there are
        // updates available
        presenter.checkForUpdates(latestVersion)
        verify(presenter.view).startUpdatesActivity(latestVersion)
        verify(presenter.view, never()).noUpdates()
    }
}

open class TestSuperPresenter : SuperPresenter() {
    override var view: SuperActivityI = mock()

    override fun backPressed() {
    }
    override fun saveSelected() {
    }
}
