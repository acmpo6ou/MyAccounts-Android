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

package com.acmpo6ou.myaccounts.create_edit_account

import android.os.Build
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.superclass.CreateEditAccountFragment
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.account.CreateAccountViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CreateEditAccountInst {
    class TestFragment : CreateEditAccountFragment() {
        override val viewModel: CreateAccountViewModel = mock()
    }

    private lateinit var scenario: FragmentScenario<TestFragment>
    val faker = Faker()

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
        scenario.onFragment {
            it.initForm()
            doNothing().whenever(it.viewModel).applyPressed(
                anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()
            )
        }
    }

    @Test
    fun `press on applyButton should call applyPressed`() {
        scenario.onFragment {
            val accountName = faker.str()
            val username = faker.str()
            val email = faker.str()
            val pass = faker.str()
            val date = faker.str()
            val comment = faker.str()

            it.nameField.setText(accountName)
            it.b.accountUsername.setText(username)
            it.b.accountEmail.setText(email)
            it.passwordField.setText(pass)
            it.repeatPasswordField.setText(pass)
            it.b.birthDate.text = date
            it.b.accountComment.setText(comment)

            it.applyButton.performClick()
            verify(it.viewModel).applyPressed(accountName, username, email, pass, date, comment)
        }
    }
}
