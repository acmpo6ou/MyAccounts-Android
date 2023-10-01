/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsBindings
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsPresenterI
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(AppModule::class, AccountsBindings::class)
class SuperActivityFunc {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var scenario: ActivityScenario<AccountsActivity>

    @BindValue
    @Singleton
    @JvmField
    val app: MyApp = mock { on { databases } doReturn mutableListOf(Database("main")) }

    @BindValue
    @JvmField
    @ActivityScoped
    val presenter: AccountsPresenterI = mock()

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(AccountsActivity::class.java)

        // prepare confirm back dialog
        scenario.onActivity { it.confirmBack() }
        Thread.sleep(1000) // wait for dialog to appear
    }

    @Test
    fun confirmBack_should_call_presenter_saveSelected_when_Save_is_chosen_in_dialog() {
        // choose Save
        onView(withText(R.string.save))
            .inRoot(isDialog())
            .perform(click())
        verify(presenter).saveSelected()
    }

    @Test
    fun confirmBack_should_not_call_presenter_saveSelected_when_Ok_is_chosen_in_dialog() {
        // choose Ok
        onView(withText("Ok"))
            .inRoot(isDialog())
            .perform(click())
        verify(presenter, never()).saveSelected()
    }

    @Test
    fun confirmBack_should_not_call_presenter_saveSelected_when_Cancel_is_chosen_in_dialog() {
        // choose Cancel
        onView(withText(R.string.cancel))
            .inRoot(isDialog())
            .perform(click())
        verify(presenter, never()).saveSelected()
    }
}
