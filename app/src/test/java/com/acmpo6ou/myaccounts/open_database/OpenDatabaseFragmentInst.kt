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

package com.acmpo6ou.myaccounts.open_database

import android.os.Build
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.database.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.ui.database.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class OpenDatabaseFragmentInst {
    lateinit var scenario: FragmentScenario<OpenDatabaseFragment>
    private var model: OpenDatabaseViewModel = spy()
    private val faker = Faker()

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId=R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `'Open database' button should call startPasswordCheck`(){
        scenario.onFragment {
            it.viewModel = model
            val txt = faker.str()
            it.b.databasePassword.setText(txt)

            it.b.openDatabase.performClick()
            verify(model).startPasswordCheck(txt)
        }
    }

    @Test
    fun `error tip should change when incorrectPassword changes`(){
        scenario.onFragment {
            val errorMsg = it.myContext.resources.getString(R.string.password_error)

            // error tip should appear when incorrectPassword is true
            it.viewModel._incorrectPassword.value = true
            assertEquals(errorMsg, it.b.parentPassword.error)

            // and disappear when incorrectPassword is false
            it.viewModel._incorrectPassword.value = false
            assertEquals(null, it.b.parentPassword.error)
        }
    }

    @Test
    fun `should display or hide progress bar depending on 'loading' of view model`(){
        scenario.onFragment {
            // when loading is true progress bar should be displayed and button - disabled
            it.viewModel._loading.value = true
            assertEquals(View.VISIBLE, it.b.progressLoading.visibility)
            assertFalse(it.b.openDatabase.isEnabled)

            // when loading false progress bar should be hidden and button - enabled
            it.viewModel._loading.value = false
            assertEquals(View.GONE, it.b.progressLoading.visibility)
            assertTrue(it.b.openDatabase.isEnabled)
        }
    }
}