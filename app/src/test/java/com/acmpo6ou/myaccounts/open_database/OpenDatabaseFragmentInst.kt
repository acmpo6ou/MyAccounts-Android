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

package com.acmpo6ou.myaccounts.open_database

import android.app.Dialog
import android.view.View
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragmentArgs
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@ExperimentalCoroutinesApi
class OpenDatabaseFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var openScenario: FragmentScenario<OpenDatabaseFragment>
    private var model: OpenDatabaseViewModel = spy()
    private val args: OpenDatabaseFragmentArgs = mock{ on{databaseIndex} doReturn 0 }
    private val faker = Faker()

    @Before
    fun setUp() {
        // Create a graphical FragmentScenario for the fragment
        openScenario = launchFragmentInContainer(themeResId=R.style.Theme_MyAccounts_NoActionBar)
    }

    /**
     * Helper method to initialize test database for OpenDatabaseFragment.
     */
    private fun setupDatabase(){
        openScenario.onFragment {
            it.app.databases = mutableListOf(Database("main"))
            it.args = args
        }
    }

    @Test
    fun `'Open database' button should call startPasswordCheck`(){
        openScenario.onFragment {
            it.viewModel = model
            val txt = faker.lorem().sentence()
            it.b.databasePassword.setText(txt)

            it.b.openDatabase.performClick()
            verify(model).startPasswordCheck(txt)
        }
    }

    @Test
    fun `error tip should change when incorrectPassword changes`(){
        openScenario.onFragment {
            val errorMsg = it.myContext.resources.getString(R.string.password_error)

            // error tip should appear when incorrectPassword is true
            it.viewModel.incorrectPassword.value = true
            assertEquals(errorMsg, it.b.parentPassword.error)

            // and disappear when incorrectPassword is false
            it.viewModel.incorrectPassword.value = false
            assertEquals(null, it.b.parentPassword.error)
        }
    }

    @Test
    fun `should display error message when corrupted is true`(){
        openScenario.onFragment {
            setupDatabase()
            val errorTitle = it.myContext.resources.getString(R.string.open_error)
            val errorMsg = it.myContext.resources.getString(R.string.corrupted_db, "main")
            it.viewModel.corrupted.value = true

            val dialog: Dialog = ShadowAlertDialog.getLatestDialog()
            val title = dialog.findViewById<TextView>(R.id.alertTitle)
            val message = dialog.findViewById<TextView>(android.R.id.message)

            assertEquals(errorTitle, title.text)
            assertEquals(errorMsg, message.text)
        }
    }

    @Test
    fun `should display or hide progress bar depending on 'loading' of view model`(){
        openScenario.onFragment {
            // when loading is true progress bar should be displayed and button - disabled
            it.viewModel.loading.value = true
            assertEquals(View.VISIBLE, it.b.progressLoading.visibility)
            assertFalse(it.b.openDatabase.isEnabled)

            // when loading false progress bar should be hidden and button - enabled
            it.viewModel.loading.value = false
            assertEquals(View.GONE, it.b.progressLoading.visibility)
            assertTrue(it.b.openDatabase.isEnabled)
        }
    }
}