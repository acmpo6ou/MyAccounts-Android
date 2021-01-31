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

package com.acmpo6ou.myaccounts.main_activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.findSnackbarTextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityInst {
    lateinit var scenario: ActivityScenario<MainActivity>

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val noUpdatesMsg = context.resources.getString(R.string.no_updates)

    @Before
    fun setup(){
        scenario = launch(MainActivity::class.java)
        scenario.onActivity {
            it.myContext.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        }
    }

    @Test
    fun `noUpdates should display snackbar`(){
        scenario.onActivity {
            it.noUpdates()

            // this is because of some Robolectric main looper problems
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // get the snackbar
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // check that snackbar was displayed
            assertTrue("No snackbar is displayed when call to noUpdates is made!",
                    snackbar != null)

            // check the snackbar's message
            assertEquals("noUpdates snackbar has incorrect message!",
                        noUpdatesMsg, snackbar?.text)
        }
    }

    @Test
    fun `importDialog should start appropriate intent`(){
        val expectedAction = Intent.ACTION_OPEN_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedType = "application/x-tar"

        scenario.onActivity { it.importDialog() }

        // check all intent properties
        val intent: Intent =
                Shadows.shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals("importDialog: incorrect intent action!",
                expectedAction, intent.action)
        assertEquals("importDialog: incorrect intent category!",
                expectedCategory, intent.categories.first())
        assertEquals("importDialog: incorrect intent type!",
                expectedType, intent.type)
    }
}