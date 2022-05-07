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

package com.acmpo6ou.myaccounts.accounts_list

import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsFragment
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListBindings
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsListPresenterI
import com.acmpo6ou.myaccounts.core.AppModule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@UninstallModules(AppModule::class, AccountsListBindings::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AccountsAdapterInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val app = MyApp()

    @BindValue
    @JvmField
    val presenter: AccountsListPresenterI = mock { on { accountsList } doReturn listOf(account) }

    private lateinit var recycler: RecyclerView
    private lateinit var itemLayout: View

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        launchFragmentInHiltContainer<AccountsFragment> {
            recycler = this.getRecycler()
            Navigation.setViewNavController(this.requireView(), mock())
        }

        // get item layout from recycler
        itemLayout = recycler.getChildAt(0)
    }

    @Test
    fun `account item should have appropriate name`() {
        val accountName = itemLayout.findViewById<TextView>(R.id.accountName)
        assertEquals(account.accountName, accountName?.text)
    }

    @Test
    fun `clicking on 'Edit' should call editAccount`() {
        clickMenuItem(itemLayout, R.id.edit_account_item)
        verify(presenter).editAccount(0)
    }

    @Test
    fun `clicking on 'Delete' should call deleteSelected`() {
        clickMenuItem(itemLayout, R.id.delete_account_item)
        verify(presenter).deleteSelected(0)
    }

    @Test
    fun `click on recycler item should call displayAccount`() {
        itemLayout.performClick()
        verify(presenter).displayAccount(0)
    }
}
