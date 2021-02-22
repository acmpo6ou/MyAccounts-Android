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

package com.acmpo6ou.myaccounts.display_account

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.os.Looper
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.findSnackbarTextView
import com.acmpo6ou.myaccounts.ui.account.DisplayAccountFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DisplayAccountInst {
    lateinit var scenario: FragmentScenario<DisplayAccountFragment>
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    val usernameStr = context.resources.getString(R.string.username_)
    val emailStr = context.resources.getString(R.string.e_mail_)
    val passwordStr = context.resources.getString(R.string.password_)
    val commentStr = context.resources.getString(R.string.comment_)

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `setAccount should fill all text views with data`(){
        scenario.onFragment{
            it.setAccount(account)

            assertEquals("$usernameStr ${account.name}", it.b.accountUsername.text.toString())
            assertEquals("$emailStr ${account.email}", it.b.accountEmail.text.toString())
            assertEquals("$passwordStr ${account.password}", it.b.accountPassword.text.toString())
            assertEquals(account.date, it.b.birthDate.text.toString())
            assertEquals("$commentStr\n${account.comment}", it.b.accountComment.text.toString())
        }
    }

    @Test
    fun `press on copy FAB should copy password`(){
        scenario.onFragment {
            it.setAccount(account)
            it.b.copyPassword.performClick()

            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            assertEquals(account.password, clipboard.primaryClip!!.getItemAt(0).text)
        }
    }

    @Test
    fun `press on copy FAB should display snackbar`(){
        scenario.onFragment {
            val copyMessage = context.resources.getString(R.string.copied)
            it.setAccount(account)
            it.b.copyPassword.performClick()

            // this is because of some Robolectric main looper problems
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            val snackbar: TextView? = it.view?.findSnackbarTextView()
            assertEquals(copyMessage, snackbar?.text)
        }
    }
}