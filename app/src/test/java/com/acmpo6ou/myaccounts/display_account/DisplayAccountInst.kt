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

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.view.MotionEvent
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.account.DisplayAccountFragment
import com.github.javafaker.Faker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
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
    fun `setAccount should fill all text views with data`() {
        scenario.onFragment {
            it.setAccount(account)

            assertEquals("$usernameStr ${account.username}", it.b.accountUsername.text.toString())
            assertEquals("$emailStr ${account.email}", it.b.accountEmail.text.toString())
            assertEquals("$passwordStr ${"•".repeat(16)}", it.b.accountPassword.text.toString())
            assertEquals(account.date, it.b.birthDate.text.toString())
            assertEquals("$commentStr\n${account.comment}", it.b.accountComment.text.toString())
        }
    }

    @Test
    fun `should hide display or hide password when pressing and releasing password label`() {
        scenario.onFragment {
            it.setAccount(account)

            // when touching password label password should be displayed
            it.b.accountPassword.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, 0F, 0F, 0
                )
            )
            assertEquals(
                "$passwordStr ${account.password}",
                it.b.accountPassword.text.toString()
            )

            // when releasing password label password should be hidden
            it.b.accountPassword.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP, 0F, 0F, 0
                )
            )
            assertEquals(
                "$passwordStr ${"•".repeat(16)}",
                it.b.accountPassword.text.toString()
            )
        }
    }

    @Test
    fun `saveFileDialog should start appropriate intent`() {
        val expectedAction = Intent.ACTION_CREATE_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedTitle = Faker().str()

        scenario.onFragment { it.saveFileDialog(expectedTitle) }
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(expectedAction, intent.action)
        assertEquals(expectedCategory, intent.categories.first())
        assertEquals(expectedTitle, intent.getStringExtra(Intent.EXTRA_TITLE))
    }
}
