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

package com.acmpo6ou.myaccounts.databases_list

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.FileNotFoundException
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DatabasesPresenterInst : DatabasesPresenterTest() {
    // get string resources
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val resources: Resources = context.resources

    private val exportErrorTitle = resources.getString(R.string.export_error_title)
    private val deleteErrorTitle = resources.getString(R.string.delete_error_title)
    private val exportFileNotFoundDetails =
        resources.getString(R.string.export_file_not_found_details)
    private val ioError = resources.getString(R.string.io_error)

    @Before
    fun setup() {
        app.res = resources
        setupPresenter()
    }

    @Test
    fun `exportDatabase should handle FileNotFoundException`() {
        whenever(model.exportDatabase(anyString(), eq(locationUri)))
            .thenAnswer {
                throw FileNotFoundException("")
            }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, exportFileNotFoundDetails)
    }

    @Test
    fun `exportDatabase should handle IOException`() {
        whenever(model.exportDatabase(anyString(), eq(locationUri)))
            .thenAnswer {
                throw IOException()
            }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, ioError)
    }

    @Test
    fun `exportDatabase should handle any other exception`() {
        val msg = faker.str()
        val exception = Exception(msg)

        whenever(model.exportDatabase(anyString(), eq(locationUri)))
            .thenAnswer {
                throw exception
            }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, exception.toString())
    }

    @Test
    fun `deleteDatabase should handle any exception`() {
        val msg = faker.str()
        val exception = Exception(msg)

        whenever(model.deleteDatabase(anyString()))
            .thenAnswer {
                throw exception
            }
        presenter.deleteDatabase(0)

        verify(view).showError(deleteErrorTitle, exception.toString())
    }
}
