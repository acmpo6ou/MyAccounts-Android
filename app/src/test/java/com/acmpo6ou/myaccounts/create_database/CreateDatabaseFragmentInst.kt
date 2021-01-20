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
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.CreateDatabaseFragment
import com.acmpo6ou.myaccounts.ui.CreateDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class CreateDatabaseFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var createScenario: FragmentScenario<CreateDatabaseFragment>
    lateinit var spyModel: CreateDatabaseViewModel

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val faker = Faker()
    val name = faker.str()

    @Before
    fun setup() {
        // Create a graphical FragmentScenario for the fragment
        createScenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
        createScenario.onFragment {
            spyModel = spy()
            spyModel.initialize(MyApp(), faker.str())
            it.viewModel = spyModel
        }
    }

    @Test
    fun `should call validateName when text in databaseName changes`(){
        createScenario.onFragment {
            it.b.databaseName.setText(name)
            verify(spyModel).validateName(name)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyNameErr`(){
        val emptyName = context.resources.getString(R.string.empty_name)
        createScenario.onFragment {
            it.viewModel.emptyNameErr = true
            assertEquals(emptyName, it.b.parentName.error)

            it.viewModel.emptyNameErr = false
            assertEquals(null, it.b.parentName.error)
        }
    }
}