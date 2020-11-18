/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts

import android.content.Intent
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.acmpo6ou.myaccounts.core.DatabasesAdapterInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.mock
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import kotlin.math.exp

@RunWith(RobolectricTestRunner::class)
class DatabaseFragmentInstrumentation {
    @Test
    fun `+ FAB must navigate to CreateDatabaseFragment`() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        // Create a graphical FragmentScenario for the DatabaseFragment
        val databaseScenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)

        databaseScenario.onFragment { fragment ->
            // Set the NavController property on the fragment
            Navigation.setViewNavController(fragment.requireView(), navController)

            // Verify that performing a click changes the NavController’s state
            val addButton = fragment.view?.findViewById<View>(R.id.addDatabase)
            addButton?.performClick()
        }

        assertEquals(
            "(+) FAB on DatabaseFragment doesn't navigate to CreateDatabaseFragment!",
            navController.currentDestination?.id,
            R.id.createDatabaseFragment
        )
    }

    @Test
    fun `exportDialog should start appropriate intent`(){
        // create expected intent with default file name `main.tar` and file type `.tar`
        val expectedIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        expectedIntent.addCategory(Intent.CATEGORY_OPENABLE)
        expectedIntent.type = "application/x-tar"
        expectedIntent.putExtra(Intent.EXTRA_TITLE, "main.tar")

        val adapter = mock<DatabasesAdapterInter>()
        var presenter: DatabasesPresenterInter = mock()
        val fragment = DatabaseFragment(adapter, presenter)
        fragment.exportDialog("main")

        val actual: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(expectedIntent, actual)
    }
}