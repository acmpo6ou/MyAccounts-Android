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

package com.acmpo6ou.myaccounts.superclass

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SuperActivityInst : NoInternet {
    // here we use MainActivity instead of SuperActivity because SuperActivity is abstract
    // and MainActivity inherits from SuperActivity
    lateinit var scenario: ActivityScenario<MainActivity>
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val faker = Faker()

    private val goBackTitle = context.resources.getString(R.string.go_back_title)
    private val confirmExit = context.resources.getString(R.string.confirm_exit)
    private val noUpdatesMsg = context.resources.getString(R.string.no_updates)

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            it.myContext.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        }
    }

    @Test
    fun `showError should create dialog with appropriate title and message`() {
        val expectedTitle = faker.str()
        val expectedMsg = faker.str()

        scenario.onActivity {
            it.showError(expectedTitle, expectedMsg)
        }

        val dialog: Dialog = ShadowAlertDialog.getLatestDialog()
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
            "showError created dialog with incorrect title!",
            expectedTitle, title.text
        )
        assertEquals(
            "showError created dialog with incorrect message!",
            expectedMsg, message.text
        )
    }

    @Test
    fun `updatesSnackbar should display snackbar when isAutoCheck is false`() {
        scenario.onActivity {
            val msg = R.string.no_updates
            it.updatesSnackbar(msg, false)

            // this is because of some Robolectric main looper problems
            shadowOf(Looper.getMainLooper()).idle()

            // get the snackbar
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // check the snackbar's message
            assertEquals(noUpdatesMsg, snackbar?.text)
        }
    }

    @Test
    fun `updatesSnackbar should not display snackbar when isAutoCheck is true`() {
        scenario.onActivity {
            val msg = R.string.no_updates
            it.updatesSnackbar(msg, true)

            // this is because of some Robolectric main looper problems
            shadowOf(Looper.getMainLooper()).idle()

            // try to get the snackbar
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // but it should be null
            assertNull(snackbar)
        }
    }

    @Test
    fun `navigation drawer should be locked when current fragment is not mainFragment`() {
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(R.id.aboutFragment)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    @Test
    fun `navigation drawer should not be locked when current fragment is mainFragment`() {
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(it.mainFragmentId)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    @Test
    fun `confirmBack should display confirmation dialog`() {
        scenario.onActivity { it.confirmBack() }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(goBackTitle, title?.text)
        assertEquals(confirmExit, message?.text)
    }

    @Test
    fun `startUpdatesActivity should start appropriate intent`() {
        val latestVersion = faker.str()
        scenario.onActivity { it.startUpdatesActivity(latestVersion) }

        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(latestVersion, intent.getStringExtra("version"))
    }
}
