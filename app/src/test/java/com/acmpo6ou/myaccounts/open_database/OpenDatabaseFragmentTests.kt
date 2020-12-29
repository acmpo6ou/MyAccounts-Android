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

package com.acmpo6ou.myaccounts.open_database

import android.content.Context
import android.content.res.Resources
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragmentArgs
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import java.io.File

class OpenDatabaseFragmentTests {
    private val fragment = OpenDatabaseFragment()
    private val model: OpenDatabaseViewModel = mock()

    private val faker = Faker()
    private val SRC_DIR = faker.file().fileName()
    private val OPEN_DB = faker.lorem().sentence()

    private val context: Context = mock()
    private val app = MyApp()
    private val args: OpenDatabaseFragmentArgs = mock()
    private val filesDir: File = mock()

    @Before
    fun setup(){
        whenever(args.databaseIndex).thenReturn(0)
        whenever(filesDir.path).thenReturn(SRC_DIR)
        whenever(context.getExternalFilesDir(null)).thenReturn(filesDir)
        doNothing().whenever(model).initialize(any(), anyInt(), anyString(), anyString())

        // mock string resources
        val res: Resources = mock()
        whenever(res.getString(R.string.open_db)).thenReturn(OPEN_DB)
        whenever(context.resources).thenReturn(res)

        fragment.viewModel = model
        fragment.myContext = context
        fragment.app = app
        fragment.args = args
    }

    @Test
    fun `initModel should initialize view model`(){
        fragment.initModel()
        verify(model).initialize(app, 0, SRC_DIR, OPEN_DB)
    }
}