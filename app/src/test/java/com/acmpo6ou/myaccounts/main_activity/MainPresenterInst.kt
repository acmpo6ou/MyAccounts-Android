/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.NoInternet
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainModelI
import com.acmpo6ou.myaccounts.database.main_activity.MainPresenter
import com.acmpo6ou.myaccounts.str
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class MainPresenterInst : NoInternet {
    lateinit var presenter: MainPresenter
    lateinit var model: MainModelI
    private lateinit var view: MainActivityI

    private val faker = Faker()
    private val locationUri: Uri = mock()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val resources: Resources = context.resources

    private val importErrorTitle = resources.getString(R.string.import_error_title)
    private val importExistsMsg = resources.getString(R.string.db_exists)
    private val ioError = resources.getString(R.string.io_error)

    @Before
    fun setup() {
        val app: MyApp = mock {
            on { res } doReturn resources
            on { SRC_DIR } doReturn ""
        }
        view = mock()
        model = mock()
        presenter = MainPresenter({ view }, model, app)
    }

    @Test
    fun `checkDbaFile should check dba file size`() {
        val size = faker.random().nextInt(0, 115) // we need incorrect size
        doReturn(size).whenever(model).getSize(locationUri)

        presenter.checkDbaFile(locationUri)
        val importSizeMsg = resources.getString(R.string.import_dba_size, size)
        verify(view).showError(importErrorTitle, importSizeMsg)
    }

    @Test
    fun `importDatabase should handle FileAlreadyExistsException`() {
        whenever(model.importDatabase(locationUri)).thenAnswer {
            throw FileAlreadyExistsException(File(""))
        }

        presenter.importDatabase(locationUri)
        verify(view).showError(importErrorTitle, importExistsMsg)
    }

    @Test
    fun `importDatabase should handle IOException`() {
        whenever(model.importDatabase(locationUri)).thenAnswer {
            throw IOException()
        }

        presenter.importDatabase(locationUri)
        verify(view).showError(importErrorTitle, ioError)
    }

    @Test
    fun `importDatabase should handle any other Exception`() {
        val msg = faker.str()
        val exception = Exception(msg)
        whenever(model.importDatabase(locationUri)).thenAnswer {
            throw exception
        }

        presenter.importDatabase(locationUri)
        verify(view).showError(importErrorTitle, exception.toString())
    }
}
