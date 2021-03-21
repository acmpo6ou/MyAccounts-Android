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
import com.acmpo6ou.myaccounts.account.AccountsListPresenterInter
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.ui.account.AccountsFragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountsFragmentFunc {
    lateinit var scenario: FragmentScenario<AccountsFragment>
    lateinit var presenter: AccountsListPresenterInter

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
        val account = Account("", "", "", "", "", "")
        presenter = mock { on { accountsList } doReturn listOf(account) }

        scenario.onFragment {
            it.presenter = presenter
        }
    }

    @Test
    fun confirmDelete_should_call_deleteAccount_when_Yes_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose Yes
        onView(withId(android.R.id.button1)).perform(click())

        verify(presenter).deleteAccount(0)
    }

    @Test
    fun confirmDelete_should_not_call_deleteAccount_when_No_is_chosen_in_dialog() {
        scenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        Thread.sleep(1000)

        // choose No
        onView(withId(android.R.id.button2)).perform(click())

        verify(presenter, never()).deleteAccount(0)
    }
}
