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

package com.acmpo6ou.myaccounts.utils.superclass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.superclass.SuperViewModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SuperViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = SuperViewModel()
    private val faker = Faker()

    @Test
    fun `initialize should set title`(){
        val titleStart = faker.str()
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, "", titleStart, 0)
        assertEquals("$titleStart main", model.title)
    }
}