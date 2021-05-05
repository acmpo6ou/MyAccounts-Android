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

package com.acmpo6ou.myaccounts.accounts_list

import androidx.navigation.NavController
import androidx.navigation.Navigation.setViewNavController
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragment
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragmentDirections.actionDisplayAccount
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragmentDirections.actionEditAccount
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListBindings
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListPresenterI
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.launchFragmentInHiltContainer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@HiltAndroidTest
@UninstallModules(AppModule::class, AccountsListBindings::class)
@RunWith(RobolectricTestRunner::class)
class AccountsFragmentInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val app = MyApp()

    @BindValue
    @JvmField
    val presenter: AccountsListPresenterI = mock { on { accountsList } doReturn listOf(account) }

    lateinit var fragment: AccountsFragment
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        fragment = launchFragmentInHiltContainer()
    }

    private fun mockNavController() {
        navController = mock()
        setViewNavController(fragment.requireView(), navController)
    }

    @Test
    fun `navigateToDisplay should pass appropriate account name`() {
        mockNavController()
        fragment.navigateToDisplay(account.accountName)

        val expectedAction = actionDisplayAccount(account.accountName)
        verify(navController).navigate(expectedAction)
    }

    @Test
    fun `navigateToEdit should pass appropriate account name`() {
        mockNavController()
        fragment.navigateToEdit(account.accountName)

        val expectedAction = actionEditAccount(account.accountName)
        verify(navController).navigate(expectedAction)
    }
}
