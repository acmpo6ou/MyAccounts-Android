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

package com.acmpo6ou.myaccounts.create_edit_database

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.CreateEditFragment
import com.acmpo6ou.myaccounts.core.CreateEditViewModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

// this is because CreateEditFragment is abstract
class TestFragment : CreateEditFragment(){
    override var viewModel: CreateEditViewModel = spy()
}

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class CreateEditFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var scenario: FragmentScenario<TestFragment>
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val app = context.applicationContext as MyApp
    val faker = Faker()
    val name = faker.str()

    @Before
    fun setup() {
        // Create a graphical FragmentScenario for the fragment
        scenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
        scenario.onFragment {
            it.viewModel.initialize(app, faker.str())
            it.initModel()
            it.initForm()
        }
    }

    @Test
    fun `should call validateName when text in databaseName changes`(){
        scenario.onFragment {
            it.b.databaseName.setText(name)
            verify(it.viewModel).validateName(name)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyNameErr and existsNameErr`(){
        val emptyName = context.resources.getString(R.string.empty_name)
        val nameExists = context.resources.getString(R.string.db_exists)

        scenario.onFragment {
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
        scenario.onFragment {
            val str = faker.str()

            it.b.databasePassword.setText(str)
            verify(it.viewModel).validatePasswords(str, "")

            it.b.databaseRepeatPassword.setText(str)
            verify(it.viewModel).validatePasswords(str, str)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyPassErr and diffPassErr`(){
        val emptyPassword = context.resources.getString(R.string.empty_password)
        val diffPasswords = context.resources.getString(R.string.diff_passwords)

        scenario.onFragment {
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
    fun `applyButton should change according to nameErrors and passwordErrors`(){
        scenario.onFragment {
            val createButton = it.b.applyButton

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

    @Test
    fun `press on applyButton should call applyPressed`(){
        scenario.onFragment {
            val pass = faker.str()
            doNothing().whenever(it.viewModel).applyPressed(name, pass)

            it.b.databaseName.setText(name)
            it.b.databasePassword.setText(pass)
            it.b.databaseRepeatPassword.setText(pass)

            it.b.applyButton.performClick()
            verify(it.viewModel).applyPressed(name, pass)
        }
    }

    @Test
    fun `should display or hide progress bar depending on 'loading' of view model`(){
        scenario.onFragment {
            // when loading is true progress bar should be displayed and button - disabled
            it.viewModel._loading.value = true
            assertEquals(View.VISIBLE, it.b.progressLoading.visibility)
            assertFalse(it.b.applyButton.isEnabled)

            // when loading false progress bar should be hidden and button - enabled
            it.viewModel._loading.value = false
            assertEquals(View.GONE, it.b.progressLoading.visibility)
            assertTrue(it.b.applyButton.isEnabled)
        }
    }
}