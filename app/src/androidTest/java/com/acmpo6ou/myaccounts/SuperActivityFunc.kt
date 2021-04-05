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

package com.acmpo6ou.myaccounts

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.account.AccountsPresenterI
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuperActivityFunc {
    lateinit var scenario: ActivityScenario<AccountsActivity>
    lateinit var presenter: AccountsPresenterI
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        val app = context.applicationContext as MyApp
        app.databases = mutableListOf(Database("main"))

        presenter = mock()
        doNothing().whenever(presenter).saveSelected()

        scenario = ActivityScenario.launch(AccountsActivity::class.java)
        scenario.onActivity {
            it.presenter = presenter
        }
    }

    @Test
    fun confirmBack_should_call_presenter_saveSelected_when_Save_is_chosen_in_dialog() {
        scenario.onActivity {
            it.confirmBack()
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Save
        onView(withText(R.string.save))
            .inRoot(isDialog())
            .perform(click())

        verify(presenter).saveSelected()
    }

    @Test
    fun confirmBack_should_not_call_presenter_saveSelected_when_Ok_is_chosen_in_dialog() {
        scenario.onActivity {
            it.confirmBack()
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Ok
        onView(withText("Ok"))
            .inRoot(isDialog())
            .perform(click())

        verify(presenter, never()).saveSelected()
    }

    @Test
    fun confirmBack_should_not_call_presenter_saveSelected_when_Cancel_is_chosen_in_dialog() {
        scenario.onActivity {
            it.confirmBack()
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Cancel
        onView(withText(R.string.cancel))
            .inRoot(isDialog())
            .perform(click())

        verify(presenter, never()).saveSelected()
    }
}
