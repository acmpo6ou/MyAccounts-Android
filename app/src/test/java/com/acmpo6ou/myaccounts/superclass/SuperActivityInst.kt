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
import android.os.Build
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
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
class SuperActivityInst {
    // here we use MainActivity instead of SuperActivity because SuperActivity is abstract
    // and MainActivity inherits from SuperActivity
    lateinit var scenario: ActivityScenario<MainActivity>
    private val faker = Faker()

    @Before
    fun setup(){
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            it.myContext.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        }
    }

    @Test
    fun `showError should create dialog with appropriate title and message`(){
        val expectedTitle = faker.str()
        val expectedMsg = faker.str()

        scenario.onActivity {
            it.showError(expectedTitle, expectedMsg)
        }

        val dialog: Dialog = ShadowAlertDialog.getLatestDialog()
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals("showError created dialog with incorrect title!",
                expectedTitle, title.text)
        assertEquals("showError created dialog with incorrect message!",
                expectedMsg, message.text)
    }

    @Test
    fun `navigation drawer should be locked when current fragment is not mainFragment`(){
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(R.id.aboutFragment)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    @Test
    fun `navigation drawer should not be locked when current fragment is mainFragment`(){
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(it.mainFragmentId)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }
}