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

package com.acmpo6ou.myaccounts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class DatabasesPresenterInst:DatabasesPresenterTest() {
    // get string resources
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val exportErrorTitle = context.resources.getString(R.string.export_error_title)
    val exportNoSuchFileDetails = context.resources.getString(R.string.export_no_such_file_details)

    @Test
    fun `exportDatabase should handle NoSuchFileException`(){
        whenever(model.exportDatabase(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenAnswer{
                    throw NoSuchFileException(File(""))
                }
        callExportDatabase()

        verify(view).showError(exportErrorTitle, exportNoSuchFileDetails)
    }
}