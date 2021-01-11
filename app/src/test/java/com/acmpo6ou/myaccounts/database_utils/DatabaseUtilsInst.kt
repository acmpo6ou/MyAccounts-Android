/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.database_utils

import android.content.Intent
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.startDatabaseUtil
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.github.javafaker.Faker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabaseUtilsInst {
    private val faker = Faker()
    lateinit var scenario: FragmentScenario<DatabaseFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `startDatabaseUtil should start appropriate intent`(){
        // database index that wil be passed with intent
        val number = faker.number()
        val index = number.randomDigit()

        // create expected intent
        var expectedIntent = Intent()

        // call startDatabaseUtil
        scenario.onFragment {
            expectedIntent = Intent(it.myContext, AccountsActivity::class.java)
            expectedIntent.putExtra("databaseIndex", index)

            startDatabaseUtil(index, it)
        }

        // check that appropriate intent was started
        val actual: Intent = Shadows.shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(
                expectedIntent.getIntExtra("databaseIndex", number.randomDigit()),
                actual.getIntExtra("databaseIndex", number.randomDigit()),
        )
        assertEquals(
                "startDatabase should start AccountsActivity!",
                expectedIntent.component?.className,
                actual.component?.className,
        )
    }
}