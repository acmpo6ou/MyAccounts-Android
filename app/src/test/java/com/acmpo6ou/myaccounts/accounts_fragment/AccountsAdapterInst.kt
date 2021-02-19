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

package com.acmpo6ou.myaccounts.accounts_fragment

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.AccountsListPresenterInter
import com.acmpo6ou.myaccounts.clickMenuItem
import com.acmpo6ou.myaccounts.databaseMap
import com.acmpo6ou.myaccounts.ui.account.AccountsFragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class AccountsAdapterInst {
    lateinit var scenario: FragmentScenario<AccountsFragment>
    lateinit var spyPresenter: AccountsListPresenterInter

    var recycler: RecyclerView? = null
    var itemLayout: View? = null

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)

        scenario.onFragment {
            spyPresenter = spy(it.presenter)
            whenever(spyPresenter.accounts).doReturn(databaseMap)
            it.presenter = spyPresenter
            recycler = it.view?.findViewById(R.id.itemsList)
        }
        // measure and lay recycler out as is needed so we can later obtain its items
        recycler?.measure(0, 0)
        recycler?.layout(0, 0, 100, 10000)

        // get item layout from recycler
        itemLayout = recycler?.getChildAt(0)
    }

    @Test
    fun `click on recycler item should call displayAccount`(){
        itemLayout?.performClick()
        verify(spyPresenter).displayAccount(0)
    }

    @Test
    fun `account item should have appropriate name`(){
        val accountName = itemLayout?.findViewById<TextView>(R.id.itemName)
        assertEquals("gmail", accountName?.text)
    }

    @Test
    fun `clicking on 'Edit' should call editSelected`(){
        clickMenuItem(itemLayout, R.id.edit_account_item)
        verify(spyPresenter).editAccount(0)
    }

    @Test
    fun `clicking on 'Delete' should call editSelected`(){
        clickMenuItem(itemLayout, R.id.delete_account_item)
        verify(spyPresenter).deleteAccount(0)
    }
}