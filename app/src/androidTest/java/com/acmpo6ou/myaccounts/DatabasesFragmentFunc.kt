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
import android.content.SharedPreferences
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.*
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityModule
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(
    AppModule::class,
    DatabasesBindings::class, DatabasesModule::class,
    MainActivityModule::class,
)
class DatabasesFragmentFunc {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dbName = Faker().str()

    @BindValue
    @Singleton
    @JvmField
    val app: MyApp = mock { on { databases } doReturn mutableListOf(Database(dbName)) }

    @BindValue
    @JvmField
    @Singleton
    val sharedPreferences: SharedPreferences = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val presenter: DatabasesPresenterI = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val mainActivityI: MainActivityI = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val mainActivity: MainActivity = mock()

    @Test
    fun confirmDelete_should_call_deleteDatabase_when_Yes_is_chosen_in_dialog() {
        launchFragmentInHiltContainer<DatabasesFragment> {
            (this as DatabasesFragment).confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // check dialog message
        val msg = context.resources.getString(R.string.confirm_delete, dbName)
        onView(withId(android.R.id.message)).check(matches(withText(msg)))

        // choose Yes
        onView(withId(android.R.id.button1)).perform(click())

        verify(presenter).deleteDatabase(0)
    }

    @Test
    fun confirmDelete_should_not_call_deleteDatabase_when_No_is_chosen_in_dialog() {
        launchFragmentInHiltContainer<DatabasesFragment> {
            (this as DatabasesFragment).confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // check dialog message
        val msg = context.resources.getString(R.string.confirm_delete, dbName)
        onView(withId(android.R.id.message)).check(matches(withText(msg)))

        // choose No
        onView(withId(android.R.id.button2)).perform(click())

        verify(presenter, never()).deleteDatabase(0)
    }

    @Test
    fun confirmClose_should_call_closeDatabase_when_Yes_is_chosen_in_dialog() {
        launchFragmentInHiltContainer<DatabasesFragment> {
            (this as DatabasesFragment).confirmClose(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // check dialog message
        val msg = context.resources.getString(R.string.confirm_close, dbName)
        onView(withId(android.R.id.message)).check(matches(withText(msg)))

        // choose Yes
        onView(withId(android.R.id.button1)).perform(click())

        verify(presenter).closeDatabase(0)
    }

    @Test
    fun confirmClose_should_not_call_closeDatabase_when_No_is_chosen_in_dialog() {
        launchFragmentInHiltContainer<DatabasesFragment> {
            (this as DatabasesFragment).confirmClose(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // check dialog message
        val msg = context.resources.getString(R.string.confirm_close, dbName)
        onView(withId(android.R.id.message)).check(matches(withText(msg)))

        // choose No
        onView(withId(android.R.id.button2)).perform(click())

        verify(presenter, never()).closeDatabase(0)
    }
}
