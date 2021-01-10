/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

import android.app.Activity
import com.acmpo6ou.myaccounts.DatabaseViewTest
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class DatabaseFragmentTests: DatabaseViewTest() {
    lateinit var fragment: DatabaseFragment
    lateinit var presenter: DatabasesPresenterInter

    @Before
    fun setUp(){
        // mock presenter and intent with uri
        presenter = mock()
        mockIntent()

        // setup fragment with mocked adapter and presenter
        fragment = DatabaseFragment()
        fragment.presenter = presenter
    }

    @Test
    fun `onActivityResult should call exportDatabase when code is EXPORT_RC`(){
        // call onActivityResult passing export request code, result ok and intent with
        // location where to export database
        fragment.onActivityResult(fragment.EXPORT_RC, Activity.RESULT_OK, intent)

        verify(presenter).exportDatabase(locationUri)
    }

    @Test
    fun `onActivityResult should not call exportDatabase when code is other than EXPORT_RC`(){
        // call onActivityResult passing other request code, result ok and intent
        fragment.onActivityResult(OTHER_RC, Activity.RESULT_OK, intent)

        verify(presenter, never()).exportDatabase(locationUri)
    }

    @Test
    fun `onActivityResult should not call exportDatabase when result code is canceled`(){
        // call onActivityResult passing other request code, result canceled and intent
        fragment.onActivityResult(fragment.EXPORT_RC, Activity.RESULT_CANCELED, intent)

        verify(presenter, never()).exportDatabase(locationUri)
    }
}