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

package com.acmpo6ou.myaccounts.create_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.ui.CreateDatabaseViewModel
import com.github.javafaker.Faker
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CreateDatabaseModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = CreateDatabaseViewModel()
    val faker = Faker()

    @Test
    fun `fixName should remove all unsupported characters`(){
        val name = model.fixName("This is (test)/.\\-_-")
        assertEquals("Thisis(test).-_-", name)
    }

    @Test
    fun `validateName should change emptyNameErr`(){
        // if name isn't empty emptyNameErr should be false
        model.validateName(faker.lorem().sentence())
        assertFalse(model.emptyNameErr)

        // if name is empty emptyNameErr should be true
        model.validateName("")
        assertTrue(model.emptyNameErr)
    }
}