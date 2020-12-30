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
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
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
class OpenDatabaseFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var openScenario: FragmentScenario<OpenDatabaseFragment>
    private var model: OpenDatabaseViewModel = mock()
    private val faker = Faker()

    @Before
    fun setUp() {
        // Create a graphical FragmentScenario for the fragment
        openScenario = launchFragmentInContainer(themeResId=R.style.Theme_MyAccounts_NoActionBar)
        openScenario.onFragment {
            it.viewModel = model
        }
    }

    @Test
    fun `'Open database' button should call verifyPassword`(){
        openScenario.onFragment {
            val txt = faker.lorem().sentence()
            it.b.databasePassword.setText(txt)

            it.b.openDatabase.performClick()
            verify(model).verifyPassword(txt)
        }
    }

    @Test
    fun `error tip should change when incorrectPassword changes`(){
        openScenario.onFragment {
            it.viewModel = OpenDatabaseViewModel()
            it.initModel()
            val errorMsg = it.myContext.resources.getString(R.string.password_error)

            // error tip should appear when incorrectPassword is true
            it.viewModel.incorrectPassword.value = true
            assertEquals(errorMsg, it.b.databasePassword.error)

            // and disappear when incorrectPassword is false
            it.viewModel.incorrectPassword.value = false
            assertEquals(null, it.b.databasePassword.error)
        }
    }
}