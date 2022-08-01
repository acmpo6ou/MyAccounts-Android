/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragment
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListBindings
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListPresenterI
import com.acmpo6ou.myaccounts.core.AppModule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class, AccountsListBindings::class)
class AccountsFragmentFunc {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @BindValue
    @JvmField
    val app = MyApp()

    @BindValue
    @JvmField
    val presenter: AccountsListPresenterI = mock { on { accountsList } doReturn listOf(account) }

    @Before
    fun setup() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        launchFragmentInHiltContainer<AccountsFragment> {
            (this as AccountsFragment).confirmDelete(account)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // check dialog message
        val msg = context.resources.getString(R.string.confirm_account_delete, account.accountName)
        onView(withId(android.R.id.message)).check(matches(withText(msg)))
    }

    @Test
    fun confirmDelete_should_call_deleteAccount_when_Yes_is_chosen_in_dialog() {
        onView(withId(android.R.id.button1)).perform(click()) // choose Yes
        verify(presenter).deleteAccount(account)
    }

    @Test
    fun confirmDelete_should_not_call_deleteAccount_when_No_is_chosen_in_dialog() {
        onView(withId(android.R.id.button2)).perform(click()) // choose No
        verify(presenter, never()).deleteAccount(account)
    }
}
