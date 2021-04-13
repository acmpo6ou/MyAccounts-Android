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

package com.acmpo6ou.myaccounts.databases_list

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation.setViewNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.*
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionEditDatabase
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionOpenDatabase
import com.acmpo6ou.myaccounts.launchFragmentInHiltContainer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@UninstallModules(AppModule::class, DatabasesBindings::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabasesFragmentInst {
    @get:Rule var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var fragment: DatabasesFragment
    private lateinit var navController: NavController

    @BindValue
    @JvmField
    @Singleton
    val app = MyApp()

    @BindValue
    @JvmField
    @FragmentScoped
    val presenter: DatabasesPresenterI = mock()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_delete)
    private val confirmCloseMsg = context.resources.getString(R.string.confirm_close)

    @Before
    fun setUp() {
        app.databases = mutableListOf(Database("main"))
        hiltAndroidRule.inject()
        fragment = launchFragmentInHiltContainer()
    }

    private fun mockNavController() {
        navController = mock()
        setViewNavController(fragment.requireView(), navController)
    }

    @Test
    fun `exportDialog should start appropriate intent`() {
        val expectedAction = Intent.ACTION_CREATE_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedType = "application/x-tar"
        val expectedTitle = "main.tar"

        fragment.exportDialog(0)

        // check all intent properties
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(
            "exportDatabase: incorrect intent action!",
            expectedAction, intent.action
        )
        assertEquals(
            "exportDatabase: incorrect intent category!",
            expectedCategory, intent.categories.first()
        )
        assertEquals(
            "exportDatabase: incorrect intent type!",
            expectedType, intent.type
        )
        assertEquals(
            "exportDatabase: incorrect intent title!",
            expectedTitle, intent.getStringExtra(Intent.EXTRA_TITLE)
        )
    }
    @Test
    fun `navigateToEdit should pass appropriate database index`() {
        mockNavController()
        fragment.navigateToEdit(0)

        val expectedAction = actionEditDatabase(0)
        verify(navController).navigate(expectedAction)
    }

    @Test
    fun `navigateToOpen should pass appropriate database index`() {
        mockNavController()
        fragment.navigateToOpen(0)

        val expectedAction = actionOpenDatabase(0)
        verify(navController).navigate(expectedAction)
    }

    @Test
    fun `confirmDelete should create dialog with appropriate message and title`() {
        fragment.confirmDelete(0)

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(warningTitle, title?.text)
        assertEquals(String.format(confirmDeleteMsg, "main"), message?.text)
    }

    @Test
    fun `confirmClose should create dialog with appropriate message and title`() {
        fragment.confirmClose(0)

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(warningTitle, title?.text)
        assertEquals(String.format(confirmCloseMsg, "main"), message?.text)
    }

    private fun setupPresenterAndAdapter() {
        val presenter = DatabasesPresenter({ fragment }, mock(), app)
        presenter.databases = mutableListOf(
            Database("main"), // locked
            Database("test", "123") // opened
        )
        val adapter = DatabasesAdapter(presenter, context, app)

        fragment.presenter = presenter
        fragment.adapter = adapter
    }

    /**
     * Used in tests to get measured and laid out recyclerview.
     */
    private fun getRecycler(): RecyclerView {
        // find recycler, measure and lay it out, so that later we can obtain its items
        val recycler: RecyclerView = fragment.view!!.findViewById(R.id.itemsList)
        recycler.measure(0, 0)
        recycler.layout(0, 0, 100, 10000)
        return recycler
    }

    @Test
    fun `when closing database lock icon should change`() {
        setupPresenterAndAdapter()
        fragment.presenter.closeDatabase(1)
        val recycler = getRecycler()

        // check that lock icon changed to locked
        val itemLayout = recycler.getChildAt(1)
        val lockImg = itemLayout.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(R.drawable.ic_locked, lockImg.tag)
    }

    @Test
    fun `when deleting database it should disappear from recycler`() {
        setupPresenterAndAdapter()
        fragment.presenter.deleteDatabase(0)
        val recycler = getRecycler()

        // check that the first database is `test` as `main` was deleted
        val itemLayout = recycler.getChildAt(0)
        val databaseName = itemLayout.findViewById<TextView>(R.id.itemName)
        assertEquals("test", databaseName.text)
    }
}
