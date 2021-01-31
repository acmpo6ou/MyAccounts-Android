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
import androidx.test.core.app.ActivityScenario
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import org.junit.Assert
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
    lateinit var scenario: ActivityScenario<TestActivity>
    private val faker = Faker()

    @Before
    fun setup(){
        scenario = ActivityScenario.launch(TestActivity::class.java)
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

        Assert.assertEquals("showError created dialog with incorrect title!",
                expectedTitle, title.text)
        Assert.assertEquals("showError created dialog with incorrect message!",
                expectedMsg, message.text)
    }
}