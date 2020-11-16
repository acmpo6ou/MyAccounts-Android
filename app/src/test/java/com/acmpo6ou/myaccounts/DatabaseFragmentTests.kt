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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.acmpo6ou.myaccounts.core.DatabasesAdapterInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class DatabaseFragmentTests {
    @Test
    fun `onActivityResult should call exportDatabase when code is EXPORT_RC`(){
        val adapter = mock<DatabasesAdapterInter>()
        val presenter = mock<DatabasesPresenterInter>()
        val intent = mock<Intent>()
        val uri = mock<Uri>()
        whenever(uri.toString()).thenReturn("location")
        whenever(intent.data).thenReturn(uri)
        val fragment = DatabaseFragment(adapter, presenter)

        // call onActivityResult passing export request code, result ok and intent with
        // location where to export database
        fragment.onActivityResult(fragment.EXPORT_RC, Activity.RESULT_OK, intent)

        verify(presenter).exportDatabase(eq("location"))
    }
}