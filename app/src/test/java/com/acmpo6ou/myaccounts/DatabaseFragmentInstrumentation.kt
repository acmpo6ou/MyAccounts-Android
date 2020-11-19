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
import android.os.Looper.getMainLooper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.testing.*
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.*
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

// This two extensions used to find a snackbar during tests
/**
 * @return a TextView if a snackbar is shown anywhere in the view hierarchy.
 *
 * NOTE: calling Snackbar.make() does not create a snackbar. Only calling #show() will create it.
 *
 * If the textView is not-null you can check its text.
 */
fun View.findSnackbarTextView(): TextView? {
    val possibleSnackbarContentLayout = findSnackbarLayout()?.getChildAt(0) as? SnackbarContentLayout
    return possibleSnackbarContentLayout
            ?.getChildAt(0) as? TextView
}

private fun View.findSnackbarLayout(): Snackbar.SnackbarLayout? {
    when (this) {
        is Snackbar.SnackbarLayout -> return this
        !is ViewGroup -> return null
    }
    // otherwise traverse the children

    // the compiler needs an explicit assert that `this` is an instance of ViewGroup
    this as ViewGroup

    (0 until childCount).forEach { i ->
        val possibleSnackbarLayout = getChildAt(i).findSnackbarLayout()
        if (possibleSnackbarLayout != null) return possibleSnackbarLayout
    }
    return null
}

@RunWith(RobolectricTestRunner::class)
class DatabaseFragmentInstrumentation {
    lateinit var navController: TestNavHostController
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>

    // get string resources
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val successMessage = context.resources.getString(R.string.success_message)

    @Before
    fun setUp(){
        // Create a TestNavHostController
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
            themeResId = R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `+ FAB must navigate to CreateDatabaseFragment`() {
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

        // set mocked adapter and presenter on fragment and call exportDialog
        val adapter = mock<DatabasesAdapterInter>()
        val presenter = mock<DatabasesPresenterInter>()
        databaseScenario.onFragment {
            it.adapter = adapter
            it.presenter = presenter

            it.exportDialog("main")
        }

        // check all intent properties
        val actual: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(
                "exportDatabase: incorrect intent action!",
                expectedIntent.action,
                actual.action
        )
        assertEquals(
                "exportDatabase: incorrect intent category!",
                expectedIntent.categories,
                actual.categories
        )
        assertEquals(
                "exportDatabase: incorrect intent type!",
                expectedIntent.type,
                actual.type
        )
        assertEquals(
                "exportDatabase: incorrect intent title!",
                expectedIntent.getStringExtra(Intent.EXTRA_TITLE),
                actual.getStringExtra(Intent.EXTRA_TITLE)
        )
    }

    @Test
    fun `showSuccess should display snackbar`(){
        databaseScenario.onFragment {
            // call showSuccess and get the snackbar
            it.showSuccess()
            // this is because of some Robolectric main looper problems
            shadowOf(getMainLooper()).idle()
            val snackbar: TextView? = it.view?.findSnackbarTextView()

            // check that snackbar was displayed
            assertTrue(
        "No snackbar is displayed when call to DatabaseFragment.showSuccess is made!",
        snackbar != null)

            // check the snackbar's message
            assertEquals(
                    "showSuccess snackbar has incorrect message!",
                    successMessage,
                    snackbar?.text
            )
        }
    }
}