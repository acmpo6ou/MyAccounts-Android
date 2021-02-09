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

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.database.DatabaseFragment
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseFragmentInst {
    lateinit var scenario: FragmentScenario<DatabaseFragment>
    lateinit var presenter: DatabasesPresenterInter

    @Before
    fun setUp(){
        // Create a graphical FragmentScenario for the DatabaseFragment
        scenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)

        val app = MyApp()
        app.databases = mutableListOf(Database("main"))
        presenter = mock()

        scenario.onFragment {
            it.app = app
            it.presenter = presenter
        }
    }

    @Test
    fun confirmDelete_should_call_deleteDatabase_when_Yes_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Yes
        onView(withId(android.R.id.button1)).perform(click())

        verify(presenter).deleteDatabase(0)
    }

    @Test
    fun confirmDelete_should_not_call_deleteDatabase_when_No_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose No
        onView(withId(android.R.id.button2)).perform(click())

        verify(presenter, never()).deleteDatabase(0)
    }

    @Test
    fun confirmClose_should_call_closeDatabase_when_Yes_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmClose(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Yes
        onView(withId(android.R.id.button1)).perform(click())

        verify(presenter).closeDatabase(0)
    }

    @Test
    fun confirmClose_should_not_call_closeDatabase_when_No_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmClose(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose No
        onView(withId(android.R.id.button2)).perform(click())

        verify(presenter, never()).closeDatabase(0)
    }
}
