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

import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.create_edit_database.CEFTest
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateEditFragmentInst : CEFTest() {

    @Test
    fun `should call validateName when text in nameField changes`() {
        scenario.onFragment {
            it.viewModel = spy(model)
            it.nameField.setText(name)
            verify(it.viewModel).validateName(name)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyNameErr and existsNameErr`() {
        val nameEmpty = context.resources.getString(R.string.name_empty)
        val nameExists = context.resources.getString(R.string.name_exists)

        scenario.onFragment {
            // name field is empty
            it.viewModel.emptyNameErr.value = true
            it.viewModel.existsNameErr.value = false
            assertEquals(nameEmpty, it.parentName.error)

            // name field contains name that is already taken
            it.viewModel.emptyNameErr.value = false
            it.viewModel.existsNameErr.value = true
            assertEquals(nameExists, it.parentName.error)

            // name field is filled and contains name that isn't taken
            it.viewModel.emptyNameErr.value = false
            it.viewModel.existsNameErr.value = false
            assertNull(it.parentName.error)
        }
    }

    @Test
    fun `should call validatePasswords when password in either password fields changes`() {
        scenario.onFragment {
            it.viewModel = spy(model)
            val str = faker.str()

            it.passwordField.setText(str)
            verify(it.viewModel).validatePasswords(str, "")

            it.repeatPasswordField.setText(str)
            verify(it.viewModel).validatePasswords(str, str)
        }
    }

    @Test
    fun `should hide or display error tip according to emptyPassErr and diffPassErr`() {
        val emptyPassword = context.resources.getString(R.string.empty_password)
        val diffPasswords = context.resources.getString(R.string.diff_passwords)

        scenario.onFragment {
            // password fields are empty
            it.viewModel.emptyPassErr.value = true
            it.viewModel.diffPassErr.value = false
            assertEquals(emptyPassword, it.parentPassword.error)

            // passwords do not match
            it.viewModel.emptyPassErr.value = false
            it.viewModel.diffPassErr.value = true
            assertEquals(diffPasswords, it.parentPassword.error)

            // everything is okay
            it.viewModel.emptyPassErr.value = false
            it.viewModel.diffPassErr.value = false
            assertNull(it.parentPassword.error)
        }
    }

    @Test
    fun `applyButton should change according to nameErrors and passwordErrors`() {
        scenario.onFragment {
            // there are no errors
            it.viewModel.emptyNameErr.value = false
            it.viewModel.existsNameErr.value = false
            it.viewModel.emptyPassErr.value = false
            it.viewModel.diffPassErr.value = false
            assertTrue(it.applyButton.isEnabled)

            // there is a name error
            it.viewModel.emptyNameErr.value = true
            it.viewModel.existsNameErr.value = false
            it.viewModel.emptyPassErr.value = false
            it.viewModel.diffPassErr.value = false
            assertFalse(it.applyButton.isEnabled)

            // there is a password error
            it.viewModel.emptyNameErr.value = false
            it.viewModel.existsNameErr.value = false
            it.viewModel.emptyPassErr.value = true
            it.viewModel.diffPassErr.value = false
            assertFalse(it.applyButton.isEnabled)
        }
    }
}
