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

import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.databaseMap
import com.acmpo6ou.myaccounts.ui.account.AccountsFragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class AccountsFragmentInst {
    lateinit var scenario: FragmentScenario<AccountsFragment>
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_account_delete)

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
        scenario.onFragment {
            it.presenter = mock{ on{accountsList} doReturn databaseMap.values.toList()}
        }
    }

    @Test
    fun `confirmDelete should create dialog with appropriate message and title`(){
        scenario.onFragment {
            it.confirmDelete(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals("confirmDelete created dialog with incorrect title!",
                warningTitle, title?.text)
        assertEquals("confirmDelete created dialog with incorrect message!",
                String.format(confirmDeleteMsg, account.account), message?.text)
    }
}