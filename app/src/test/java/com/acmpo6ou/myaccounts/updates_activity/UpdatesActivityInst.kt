/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.updates_activity

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.UpdatesActivity
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class UpdatesActivityInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var scenario: ActivityScenario<UpdatesActivity>
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val loadingText = context.resources.getString(R.string.loading)

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(UpdatesActivity::class.java)
    }

    @Test
    fun `changelogObserver should set changelogText when changelog is downloaded`() {
        scenario.onActivity {
            val changelog = Faker().str()
            assertEquals(loadingText, it.b.changelogText.text.toString())

            it.viewModel.changelog.value = changelog
            assertEquals(changelog, it.b.changelogText.text.toString())
        }
    }

    @Test
    fun `downloadEnabledObserver should disable downloadUpdate button`() {
        scenario.onActivity {
            // at first downloadUpdate should be enabled
            assertTrue(it.b.downloadUpdate.isEnabled)

            // then – disabled
            it.viewModel.downloadEnabled.value = false
            assertFalse(it.b.downloadUpdate.isEnabled)
        }
    }
}
