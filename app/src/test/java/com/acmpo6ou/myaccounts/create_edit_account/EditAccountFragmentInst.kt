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

package com.acmpo6ou.myaccounts.create_edit_account

import android.os.Build
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.databaseMap
import com.acmpo6ou.myaccounts.ui.account.EditAccountFragment
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
class EditAccountFragmentInst {
    lateinit var scenario: FragmentScenario<EditAccountFragment>
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `setAccount should fill all fields`(){
        scenario.onFragment {
            it.setAccount(databaseMap, account.accountName)

            assertEquals(account.accountName, it.b.accountName.text.toString())
            assertEquals(account.username, it.b.accountUsername.text.toString())
            assertEquals(account.email, it.b.accountEmail.text.toString())
            assertEquals(account.password, it.b.accountPassword.text.toString())
            assertEquals(account.password, it.b.accountRepeatPassword.text.toString())
            assertEquals(account.date, it.b.birthDate.text)
            assertEquals(account.comment, it.b.accountComment.text.toString())
        }
    }

    @Test
    fun `initForm should change text of apply button`(){
        val saveText = context.resources.getString(R.string.save)
        scenario.onFragment {
            it.initForm()
            assertEquals(saveText, it.b.applyButton.text)
        }
    }
}