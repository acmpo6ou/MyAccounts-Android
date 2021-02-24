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

package com.acmpo6ou.myaccounts.superclass

import android.os.Build
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.create_edit_database.TestFragment
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CreateEditFragmentInst {
    lateinit var scenario: FragmentScenario<TestFragment>
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val app = context.applicationContext as MyApp

    val faker = Faker()
    val name = faker.str()

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
        app.res = context.resources

        scenario.onFragment {
            it.viewModel.initialize(app, faker.str())
            it.initModel()
            it.initForm()
        }
    }

    @Test
    fun `should call validateName when text in nameField changes`(){
        scenario.onFragment {
            it.nameField.setText(name)
            verify(it.viewModel).validateName(name)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyNameErr and existsNameErr`(){
        val nameEmpty = context.resources.getString(R.string.name_empty)
        val nameExists = context.resources.getString(R.string.name_exists)

        scenario.onFragment {
            // name field is empty
            it.viewModel.emptyNameErr = true
            it.viewModel.existsNameErr = false
            assertEquals(nameEmpty, it.parentName.error)

            // name field contains name that is already taken
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = true
            assertEquals(nameExists, it.parentName.error)

            // name field is filled and contains name that isn't taken
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            Assert.assertNull(it.parentName.error)
        }
    }

    @Test
    fun `should call validatePasswords when password in either password fields changes`(){
        scenario.onFragment {
            val str = faker.str()

            it.passwordField.setText(str)
            verify(it.viewModel).validatePasswords(str, "")

            it.repeatPasswordField.setText(str)
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
            assertEquals(emptyPassword, it.parentPassword.error)

            // passwords do not match
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = true
            assertEquals(diffPasswords, it.parentPassword.error)

            // everything is okay
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertNull(it.parentPassword.error)
        }
    }

    @Test
    fun `applyButton should change according to nameErrors and passwordErrors`(){
        scenario.onFragment {
            // there are no errors
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertTrue(it.applyButton.isEnabled)

            // there is a name error
            it.viewModel.emptyNameErr = true
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = false
            it.viewModel.diffPassErr = false
            assertFalse(it.applyButton.isEnabled)

            // there is a password error
            it.viewModel.emptyNameErr = false
            it.viewModel.existsNameErr = false
            it.viewModel.emptyPassErr = true
            it.viewModel.diffPassErr = false
            assertFalse(it.applyButton.isEnabled)
        }
    }

    @Test
    fun `press on applyButton should call applyPressed`(){
        scenario.onFragment {
            val pass = faker.str()
            doNothing().whenever(it.viewModel).applyPressed(name, pass)

            it.nameField.setText(name)
            it.passwordField.setText(pass)
            it.repeatPasswordField.setText(pass)

            it.applyButton.performClick()
            verify(it.viewModel).applyPressed(name, pass)
        }
    }
}