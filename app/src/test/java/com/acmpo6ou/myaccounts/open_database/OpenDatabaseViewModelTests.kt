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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OpenDatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = OpenDatabaseViewModel()
    val app = MyApp()

    @Before
    fun setup(){
        app.databases = mutableListOf(Database("main"))
    }

    @Test
    fun `setDatabase should set title`(){
        model.setDatabase(app, 0)
        assertEquals("Open main", model.getTitle().value)
    }

    @Test
    fun `verifyPassword should set incorrectPassword to true if there are errors`(){

    }
}