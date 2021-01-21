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
import org.junit.Assert.*
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
        spyModel = spy()
        spyModel.initialize(MyApp(), faker.str())

        // Create a graphical FragmentScenario for the fragment
        createScenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `should call validateName when text in databaseName changes`(){
        createScenario.onFragment {
            it.viewModel = spyModel
            it.b.databaseName.setText(name)
            verify(spyModel).validateName(name)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyNameErr and existsNameErr`(){
        val emptyName = context.resources.getString(R.string.empty_name)
        val nameExists = context.resources.getString(R.string.db_exists)

        createScenario.onFragment {
            // name field is empty
            it.viewModel.emptyNameErr = true
            it.viewModel.existsNameErr = false
            assertEquals(emptyName, it.b.parentName.error)

            // name field contains name that is already taken
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = true
            assertEquals(nameExists, it.b.parentName.error)

            // name field is filled and contains name that isn't taken
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            assertEquals(null, it.b.parentName.error)
        }
    }

    @Test
    fun `should call validatePasswords when password in either password fields changes`(){
        createScenario.onFragment {
            it.viewModel = spyModel
            val str = faker.str()

            it.b.databasePassword.setText(str)
            verify(spyModel).validatePasswords(str, "")

            it.b.databaseRepeatPassword.setText(str)
            verify(spyModel).validatePasswords(str, str)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyPassErr and diffPassErr`(){
        val emptyPassword = context.resources.getString(R.string.empty_password)
        val diffPasswords = context.resources.getString(R.string.diff_passwords)

        createScenario.onFragment {
            // password fields are empty
            it.viewModel.emptyPassErr = true
            it.viewModel.diffPassErr = false
            assertEquals(emptyPassword, it.b.parentPassword.error)

            // passwords do not match
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = true
            assertEquals(diffPasswords, it.b.parentPassword.error)

            // everything is okay
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertEquals(null, it.b.parentPassword.error)
        }
    }

    @Test
    fun `databaseCreate should change according to nameErrors and passwordErrors`(){
        createScenario.onFragment {
            val createButton = it.b.databaseCreate

            // there are no errors
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertTrue(createButton.isEnabled)

            // there is a name error
            it.viewModel.emptyNameErr = true
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertFalse(createButton.isEnabled)

            // there is a password error
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = true
            it.viewModel.diffPassErr = false
            assertFalse(createButton.isEnabled)
        }
    }
}