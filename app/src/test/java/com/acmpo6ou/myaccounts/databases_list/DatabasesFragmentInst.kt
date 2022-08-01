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

package com.acmpo6ou.myaccounts.databases_list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation.setViewNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.*
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.*
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityModule
import com.github.javafaker.Faker
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
import org.robolectric.shadows.ShadowAlertDialog
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(
    AppModule::class,
    DatabasesBindings::class,
    DatabasesModule::class,
    MainActivityModule::class,
)
@RunWith(RobolectricTestRunner::class)
class DatabasesFragmentInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var fragment: DatabasesFragment
    private lateinit var navController: NavController

    @BindValue
    @JvmField
    @Singleton
    val app = MyApp()

    @BindValue
    @JvmField
    @Singleton
    val sharedPreferences: SharedPreferences = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val presenter: DatabasesPresenterI = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val mainActivityI: MainActivityI = mock()

    @BindValue
    @JvmField
    @FragmentScoped
    val mainActivity: MainActivity = mock()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_delete)
    private val confirmCloseMsg = context.resources.getString(R.string.confirm_close)

    @Before
    fun setUp() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
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
        val expectedType = "application/octet-stream"
        val expectedTitle = "main.dba"

        fragment.exportDialog(app.databases[0])

        // check all intent properties
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(
            "exportDatabase: incorrect intent action!",
            expectedAction,
            intent.action
        )
        assertEquals(
            "exportDatabase: incorrect intent category!",
            expectedCategory,
            intent.categories.first()
        )
        assertEquals(
            "exportDatabase: incorrect intent type!",
            expectedType,
            intent.type
        )
        assertEquals(
            "exportDatabase: incorrect intent title!",
            expectedTitle,
            intent.getStringExtra(Intent.EXTRA_TITLE)
        )
    }

    @Test
    fun `navigateToRename should pass appropriate database index`() {
        mockNavController()
        fragment.navigateToRename(0)

        val expectedAction = actionRenameDatabase(0)
        verify(navController).navigate(expectedAction)
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
    fun `showError should call mainActivity showError`() {
        val title = Faker().str()
        val details = Faker().str()

        fragment.showError(title, details)
        verify(mainActivityI).showError(title, details)
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

    @Test
    fun `when closing database lock icon should change`() {
        setupPresenterAndAdapter()
        fragment.presenter.closeDatabase(app.databases[1])
        val recycler = fragment.getRecycler()

        // check that lock icon changed to locked
        val itemLayout = recycler.getChildAt(1)
        val lockImg = itemLayout.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(R.drawable.ic_locked, lockImg.tag)
    }

    @Test
    fun `when deleting database it should disappear from recycler`() {
        setupPresenterAndAdapter()
        fragment.presenter.deleteDatabase(app.databases[0])
        val recycler = fragment.getRecycler()

        // check that the first database is `test` as `main` was deleted
        val itemLayout = recycler.getChildAt(0)
        val databaseName = itemLayout.findViewById<TextView>(R.id.itemName)
        assertEquals("test", databaseName.text)
    }
}
