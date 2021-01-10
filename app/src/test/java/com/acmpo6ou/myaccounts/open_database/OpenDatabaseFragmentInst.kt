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
import android.content.Intent
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.ui.OpenDatabaseFragmentArgs
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class OpenDatabaseFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var openScenario: FragmentScenario<OpenDatabaseFragment>
    private var model: OpenDatabaseViewModel = mock()
    private val args: OpenDatabaseFragmentArgs = mock()
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
            whenever(args.databaseIndex).thenReturn(0)
            it.args = args
        }
    }

    @Test
    fun `'Open database' button should call verifyPassword`(){
        openScenario.onFragment {
            it.viewModel = model
            val txt = faker.lorem().sentence()
            it.b.databasePassword.setText(txt)

            it.b.openDatabase.performClick()
            verify(model).verifyPassword(txt)
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
    fun `startDatabase should start AccountsActivity when opened is true`(){
        var expectedIntent = Intent()
        openScenario.onFragment {
            setupDatabase()
            it.viewModel.opened.value = true

            expectedIntent = Intent(it.myContext, AccountsActivity::class.java)
            expectedIntent.putExtra("databaseIndex", 0)

            it.startDatabase(0)
        }

        // check that appropriate intent was started
        val actual: Intent =
                Shadows.shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(
                expectedIntent.getStringExtra("databaseIndex"),
                actual.getStringExtra("databaseIndex"),
        )
        assertEquals(
                expectedIntent.component?.className,
                actual.component?.className,
        )
    }
}