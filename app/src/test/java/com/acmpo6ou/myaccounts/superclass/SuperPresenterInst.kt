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

import com.acmpo6ou.myaccounts.core.utils.GitHubService
import com.nhaarman.mockitokotlin2.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import kotlin.random.Random

class SuperPresenterInst {
    @get:Rule
    val mockWebServer = MockWebServer()

    private val service: GitHubService by lazy {
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(GitHubService::class.java)
    }

    private lateinit var presenter: TestSuperPresenter
    private lateinit var spyPresenter: TestSuperPresenter

    lateinit var expectedVersion: String
    lateinit var jsonStr: String

    @Before
    fun setup() {
        presenter = TestSuperPresenter()
        spyPresenter = spy(presenter)

        spyPresenter.service = service
        doReturn(true).whenever(presenter.view).isInternetAvailable()
    }

    /**
     * Generates random version string and json containing this version string.
     */
    private fun generateVersion() {
        val versionNums = List(10) { Random.nextInt(0, 100) }
        expectedVersion = String.format("%d.%d.%d", *versionNums.toTypedArray())
        jsonStr = """ {"name":"v%s"} """.format(expectedVersion)
    }

    @Test
    fun `checkUpdatesSelected should call checkForUpdates passing through version`() {
        // mock successful response with random version name
        generateVersion()
        mockWebServer.enqueue(
            MockResponse()
                .setBody(jsonStr)
                .setResponseCode(200)
        )

        spyPresenter.checkUpdatesSelected()
        verify(spyPresenter, timeout(3000)).checkForUpdates(expectedVersion)
    }

    @Test
    fun `checkUpdatesSelected should handle failure`() {
        // mock failure response
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500)
        )

        spyPresenter.checkUpdatesSelected()
        verify(spyPresenter.view, timeout(3000)).updatesCheckFailed()
    }
}
