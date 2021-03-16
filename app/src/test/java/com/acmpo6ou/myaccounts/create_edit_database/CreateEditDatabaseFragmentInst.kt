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

package com.acmpo6ou.myaccounts.create_edit_database

import android.content.Context
import android.os.Build
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.superclass.CreateEditDatabaseFragment
import com.acmpo6ou.myaccounts.database.superclass.CreateEditDatabaseModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

class TestFragment : CreateEditDatabaseFragment() {
    override var viewModel: TestModel = spy()
}

open class TestModel : CreateEditDatabaseModel() {
    override suspend fun apply(name: String, password: String) {
    }
}

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CreateEditDatabaseFragmentInst {
    lateinit var scenario: FragmentScenario<TestFragment>
    lateinit var model: TestModel
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val app = context.applicationContext as MyApp

    val faker = Faker()
    val name = faker.str()

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
        app.res = context.resources

        model = TestModel()
        model.initialize(app, faker.str())

        scenario.onFragment {
            it.viewModel = model
            it.initModel()
            it.initForm()
        }
    }

    @Test
    fun `should display or hide progress bar depending on 'loading' of view model`() {
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

    @Test
    fun `press on applyButton should call applyPressed`() {
        scenario.onFragment {
            it.viewModel = spy(model)
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
