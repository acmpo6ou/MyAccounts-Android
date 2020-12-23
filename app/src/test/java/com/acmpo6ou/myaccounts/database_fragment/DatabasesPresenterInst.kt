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

package com.acmpo6ou.myaccounts.database_fragment

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import java.io.FileNotFoundException
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class DatabasesPresenterInst: DatabasesPresenterTest() {
    // get string resources
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val resources = context.resources
    val exportErrorTitle = resources.getString(R.string.export_error_title)
    val deleteErrorTitle = resources.getString(R.string.delete_error_title)

    val exportFileNotFoundDetails = resources.getString(R.string.export_file_not_found_details)
    val ioError = resources.getString(R.string.io_error)

    @Before
    fun setup(){
        whenever(view.myContext).thenReturn(context)
    }

    @Test
    fun `exportDatabase should handle FileNotFoundException`(){
        whenever(model.exportDatabase(anyString(), eq(locationUri)))
                .thenAnswer{
                    throw FileNotFoundException("")
                }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, exportFileNotFoundDetails)
    }

    @Test
    fun `exportDatabase should handle IOException`(){
        whenever(model.exportDatabase(anyString(), eq(locationUri)))
                .thenAnswer{
                    throw IOException()
                }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, ioError)
    }

    @Test
    fun `exportDatabase should handle any other exception`(){
        val msg = faker.lorem().sentence()
        val expectedDetails = "java.lang.Exception $msg"
        whenever(model.exportDatabase(anyString(), eq(locationUri)))
                .thenAnswer{
                    throw Exception(msg)
                }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, expectedDetails)
    }

    @Test
    fun `deleteDatabase should handle any exception`(){
        val msg = faker.lorem().sentence()
        val expectedDetails = "java.lang.Exception $msg"
        whenever(model.deleteDatabase(anyString()))
                .thenAnswer{
                    throw Exception(msg)
                }
        presenter.deleteDatabase(0)

        verify(view).showError(deleteErrorTitle, expectedDetails)
    }
}