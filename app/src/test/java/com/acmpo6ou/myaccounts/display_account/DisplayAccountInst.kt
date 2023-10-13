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

package com.acmpo6ou.myaccounts.display_account

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsModule
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountBindings
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountFragment
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountPresenterI
import com.acmpo6ou.myaccounts.launchFragmentInHiltContainer
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@HiltAndroidTest
@UninstallModules(DisplayAccountBindings::class, AccountsModule::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DisplayAccountInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    @FragmentScoped
    val activity: AccountsActivityI = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val presenter: DisplayAccountPresenterI = mock()

    lateinit var fragment: DisplayAccountFragment
    private val b get() = fragment.b
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val usernameStr = context.resources.getString(R.string.username_)
    private val emailStr = context.resources.getString(R.string.e_mail_)
    private val passwordStr = context.resources.getString(R.string.password_)
    private val commentStr = context.resources.getString(R.string.comment_)

    @Before
    fun setup() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        hiltAndroidRule.inject()
        fragment = launchFragmentInHiltContainer()
    }

    @Test
    fun `initForm should fill all text views with data`() {
        fragment.initForm(account)

        assertEquals("$usernameStr ${account.username}", b.accountUsername.text.toString())
        assertEquals("$emailStr ${account.email}", b.accountEmail.text.toString())
        assertEquals("$passwordStr ${"•".repeat(16)}", b.accountPassword.text.toString())
        assertEquals(account.date, b.birthDate.text.toString())
        assertEquals("$commentStr\n${"•".repeat(16)}", b.accountComment.text.toString())
    }

    @Test
    fun `should display or hide password when pressing and releasing password label`() {
        fragment.initForm(account)

        // when touching password label password should be displayed
        b.accountPassword.dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 0F, 0F, 0
            )
        )
        assertEquals(
            "$passwordStr ${account.password}",
            b.accountPassword.text.toString()
        )

        // when releasing password label password should be hidden
        b.accountPassword.dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 0F, 0F, 0
            )
        )
        assertEquals(
            "$passwordStr ${"•".repeat(16)}",
            b.accountPassword.text.toString()
        )
    }

    @Test
    fun `should display or hide comment when pressing and releasing comment label`() {
        fragment.initForm(account)

        // when touching comment label comment should be displayed
        b.accountComment.dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 0F, 0F, 0
            )
        )
        assertEquals(
            "$commentStr\n${account.comment}",
            b.accountComment.text.toString()
        )

        // when releasing comment label comment should be hidden
        b.accountComment.dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 0F, 0F, 0
            )
        )
        assertEquals(
            "$commentStr\n${"•".repeat(16)}",
            b.accountComment.text.toString()
        )
    }

    @Test
    fun `saveFileDialog should start appropriate intent`() {
        val expectedAction = Intent.ACTION_CREATE_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedType = "*/*"
        val expectedTitle = Faker().str()

        fragment.saveFileDialog(expectedTitle)
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(expectedAction, intent.action)
        assertEquals(expectedType, intent.type)
        assertEquals(expectedCategory, intent.categories.first())
        assertEquals(expectedTitle, intent.getStringExtra(Intent.EXTRA_TITLE))
    }

    @Test
    fun `copyPassword should copy password to safe clipboard`() {
        fragment.initForm(account)
        assertTrue(fragment.app.password.isEmpty()) // app.password is our safe clipboard

        try {
            fragment.b.copyPassword.performClick()
        } catch (e: NullPointerException) {
            // because Robolectric cannot provide to us InputMethodManager it will be null
            // but it's okay we only care about whether password was copied
        }
        assertEquals(account.password, fragment.app.password)
    }
}
