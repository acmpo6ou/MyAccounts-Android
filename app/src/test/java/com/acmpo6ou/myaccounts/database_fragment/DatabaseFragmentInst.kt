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

package com.acmpo6ou.myaccounts.database_fragment

import android.content.Context
import android.content.Intent
import android.os.Looper.getMainLooper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesPresenter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.findSnackbarTextView
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.acmpo6ou.myaccounts.ui.DatabaseFragmentDirections
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabaseFragmentInstrumentation {
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>
    private lateinit var navController: NavController

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val successMessage = context.resources.getString(R.string.success_message)
    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_delete)
    private val confirmCloseMsg = context.resources.getString(R.string.confirm_close)

    @Before
    fun setUp(){
        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer(themeResId=R.style.Theme_MyAccounts_NoActionBar)

        // mock presenter with fake database
        val databases = mutableListOf(Database("main"))
        val presenter = mock<DatabasesPresenterInter>()
        whenever(presenter.databases).thenReturn(databases)
        databaseScenario.onFragment { it.presenter = presenter }
    }

    private fun setUpNavController() {
        // Create a TestNavHostController
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        databaseScenario.onFragment {
            // Set the NavController property on the fragment
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }

    private fun mockNavController(fragment: DatabaseFragment){
        navController = mock()
        Navigation.setViewNavController(fragment.requireView(), navController)
    }

    @Test
    fun `navigateToEdit should pass appropriate database index`(){
        databaseScenario.onFragment {
            // mock navigation controller and call navigateToEdit
            mockNavController(it)
            it.navigateToEdit(0)

            val expectedAction = DatabaseFragmentDirections.actionEditDatabase(0)
            verify(navController).navigate(expectedAction)
        }
    }

    @Test
    fun `navigateToOpen should pass appropriate database index`(){
        databaseScenario.onFragment {
            // mock navigation controller and call navigateToOpen
            mockNavController(it)
            it.navigateToOpen(0)

            val expectedAction = DatabaseFragmentDirections.actionOpenDatabase(0)
            verify(navController).navigate(expectedAction)
        }
    }

    @Test
    fun `+ FAB must navigate to CreateDatabaseFragment`() {
        setUpNavController()
        // Verify that performing a click changes the NavController’s state
        databaseScenario.onFragment {
            val addButton = it.view?.findViewById<View>(R.id.addDatabase)
            addButton?.performClick()
        }

        assertEquals(
            "(+) FAB on DatabaseFragment doesn't navigate to CreateDatabaseFragment!",
            navController.currentDestination?.id,
            R.id.createDatabaseFragment
        )
    }

    @Test
    fun `navigateToEdit should navigate to EditDatabaseFragment`(){
        setUpNavController()
        databaseScenario.onFragment {
            // call navigateToEdit
            it.navigateToEdit(0)
        }

        // verify that we navigated to edit database
        assertEquals(
                "navigateToEdit doesn't navigate to EditDatabaseFragment!",
                navController.currentDestination?.id,
                R.id.editDatabaseFragment
        )
    }

    @Test
    fun `exportDialog should start appropriate intent`(){
        val expectedAction = Intent.ACTION_CREATE_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedType = "application/x-tar"
        val expectedTitle = "main.tar"

        databaseScenario.onFragment {
            it.exportDialog(0)
        }

        // check all intent properties
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals("exportDatabase: incorrect intent action!",
                expectedAction, intent.action)
        assertEquals("exportDatabase: incorrect intent category!",
                expectedCategory, intent.categories.first())
        assertEquals("exportDatabase: incorrect intent type!",
                expectedType, intent.type)
        assertEquals("exportDatabase: incorrect intent title!",
                expectedTitle, intent.getStringExtra(Intent.EXTRA_TITLE))
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

    @Test
    fun `confirmDelete should create dialog with appropriate message and title`(){
        // create dialog
        databaseScenario.onFragment {
            it.confirmDelete(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
                "confirmDelete created dialog with incorrect title!",
                warningTitle,
                title?.text
        )
        assertEquals(
                "confirmDelete created dialog with incorrect message!",
                String.format(confirmDeleteMsg, "main"),
                message?.text
        )
    }

    @Test
    fun `confirmClose should create dialog with appropriate message and title`(){
        // create dialog
        databaseScenario.onFragment {
            it.confirmClose(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
                "confirmClose created dialog with incorrect title!",
                warningTitle,
                title?.text
        )
        assertEquals(
                "confirmClose created dialog with incorrect message!",
                String.format(confirmCloseMsg, "main"),
                message?.text
        )
    }

    /**
     * This method is used in tests to setup real (non mocked) DatabasesPresenter to test
     * how does DatabasesPresenter and DatabaseFragment integrated.
     *
     * @param[fragment] DatabaseFragment for which we will setup DatabasesPresenter.
     */
    private fun setupRealPresenter(fragment: DatabaseFragment){
        // list of databases for test
        val databases = mutableListOf(
                Database("main"), // locked
                Database("test", password = "123") // opened
        )
        // set real presenter and call closeDatabase
        val presenter = DatabasesPresenter(fragment)
        presenter.databases = databases
        fragment.presenter = presenter
    }

    /**
     * This method is used in tests to measure and lay out the recyclerview.
     *
     * @param[fragment] DatabaseFragment for which we will setup DatabasesPresenter.
     */
    private fun setupRecycler(fragment: DatabaseFragment): RecyclerView? {
        // find recycler, measure and lay it out, so that later we can obtain its items
        val recycler: RecyclerView? = fragment.view?.findViewById(R.id.databasesList)
        recycler?.measure(0, 0)
        recycler?.layout(0, 0, 100, 10000)
        return recycler
    }

    @Test
    fun `when closing database lock icon should change`(){
        databaseScenario.onFragment {
            setupRealPresenter(it)
            it.presenter.closeDatabase(1)
            val recycler = setupRecycler(it)

            // check that lock icon changed to locked
            val itemLayout = recycler?.getChildAt(1)
            val lockImg = itemLayout?.findViewById<ImageView>(R.id.itemIcon)
            assertEquals(R.drawable.ic_locked, lockImg?.tag)
        }
    }

    @Test
    fun `when deleting database it should disappear from recycler`(){
        databaseScenario.onFragment {
            setupRealPresenter(it)
            it.presenter.deleteDatabase(0)
            val recycler = setupRecycler(it)

            // check that the first database is `test` as `main` was deleted
            val itemLayout = recycler?.getChildAt(0)
            val databaseName = itemLayout?.findViewById<TextView>(R.id.itemName)
            assertEquals("test", databaseName?.text)
        }
    }

    @Test
    fun `checkListPlaceholder should hide placeholder when list has items`(){
        databaseScenario.onFragment {
            it.checkListPlaceholder()

            // placeholder should be invisible
            val placeholder = it.view?.findViewById<TextView>(R.id.no_databases)
            assertEquals(View.GONE, placeholder?.visibility)

            // while the list should be visible
            val list = it.view?.findViewById<RecyclerView>(R.id.databasesList)
            assertEquals(View.VISIBLE, list?.visibility)
        }
    }

    @Test
    fun `checkListPlaceholder should display placeholder when list has no items`(){
        databaseScenario.onFragment {
            it.databases = mutableListOf()
            it.checkListPlaceholder()

            // placeholder should be invisible
            val placeholder = it.view?.findViewById<TextView>(R.id.no_databases)
            assertEquals(View.VISIBLE, placeholder?.visibility)

            // while the list should be visible
            val list = it.view?.findViewById<RecyclerView>(R.id.databasesList)
            assertEquals(View.GONE, list?.visibility)
        }
    }
}
