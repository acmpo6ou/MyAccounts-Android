/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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
import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesBindings
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesModelI
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesPresenterI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityBindings
import com.acmpo6ou.myaccounts.database.main_activity.MainModelI
import com.acmpo6ou.myaccounts.database.main_activity.MainPresenterI
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(
    AppModule::class,
    MainActivityBindings::class, DatabasesBindings::class
)
@RunWith(RobolectricTestRunner::class)
class SuperActivityInst : NoInternet {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    @Singleton
    val app = MyApp()

    @BindValue
    @JvmField
    @ActivityScoped
    val presenter: MainPresenterI = mock()

    @BindValue
    @JvmField
    @ActivityScoped
    val model: MainModelI = mock()

    @BindValue
    @JvmField
    @ActivityScoped
    val databasesPresenter: DatabasesPresenterI = mock()

    @BindValue
    @JvmField
    @ActivityScoped
    val databasesModel: DatabasesModelI = mock()

    // here we use MainActivity instead of SuperActivity because SuperActivity is abstract
    // and MainActivity inherits from SuperActivity
    lateinit var scenario: ActivityScenario<MainActivity>
    lateinit var mockController: NavController

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val faker = Faker()

    private val goBackTitle = context.resources.getString(R.string.go_back_title)
    private val confirmExit = context.resources.getString(R.string.confirm_exit)
    private val noUpdatesMsg = context.resources.getString(R.string.no_updates)

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            mockController = mock()
            it.navController = mockController
        }
    }

    // shortcut
    private fun selectItem(itemId: Int) = scenario.onActivity {
        selectNavigationItem(itemId, it)
    }

    @Test
    fun `navigation drawer should be locked when current fragment is not mainFragment`() {
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(R.id.aboutFragment)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    @Test
    fun `navigation drawer should be unlocked when current fragment is mainFragment`() {
        scenario.onActivity {
            it.drawerLayout = mock()
            val navController = it.findNavController(R.id.nav_host_fragment)

            navController.navigate(it.mainFragmentId)
            verify(it.drawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    @Test
    fun `updatesSnackbar should display snackbar when isAutoCheck is false`() {
        scenario.onActivity {
            it.updatesSnackbar(R.string.no_updates, false)

            // this is because of some Robolectric main looper problems
            shadowOf(Looper.getMainLooper()).idle()

            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()
            assertEquals(noUpdatesMsg, snackbar?.text)
        }
    }

    @Test
    fun `updatesSnackbar should not display snackbar when isAutoCheck is true`() {
        scenario.onActivity {
            it.updatesSnackbar(R.string.no_updates, true)

            // this is because of some Robolectric main looper problems
            shadowOf(Looper.getMainLooper()).idle()

            // try to get the snackbar
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // but it should be null
            assertNull(snackbar)
        }
    }

    @Test
    fun `confirmBack should display confirmation dialog`() {
        scenario.onActivity { it.confirmBack() }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(goBackTitle, title?.text)
        assertEquals(confirmExit, message?.text)
    }

    @Test
    fun `back button should close nav drawer if it is opened`() {
        scenario.onActivity {
            it.drawerLayout = mock { on { isDrawerOpen(GravityCompat.START) } doReturn true }
            it.onBackPressed()
            verify(it.drawerLayout).closeDrawer(GravityCompat.START)
        }
    }

    @Test
    fun `back button should not close nav drawer if it isn't opened`() {
        scenario.onActivity {
            it.drawerLayout = mock { on { isDrawerOpen(GravityCompat.START) } doReturn false }
            it.onBackPressed()
            verify(it.drawerLayout, never()).closeDrawer(GravityCompat.START)
        }
    }

    @Test
    fun `'Changelog' should navigate to actionChangelog`() {
        selectItem(R.id.changelog)
        verify(mockController).navigate(R.id.actionChangelog)
        verifyNoMoreInteractions(mockController)
    }

    @Test
    fun `'Settings' should navigate to actionSettings`() {
        selectItem(R.id.settings)
        verify(mockController).navigate(R.id.actionSettings)
        verifyNoMoreInteractions(mockController)
    }

    @Test
    fun `'About' should navigate to actionAbout`() {
        selectItem(R.id.about)
        verify(mockController).navigate(R.id.actionAbout)
        verifyNoMoreInteractions(mockController)
    }

    @Test
    fun `showError should create dialog with appropriate title and message`() {
        val expectedTitle = faker.str()
        val expectedMsg = faker.str()

        scenario.onActivity {
            it.showError(expectedTitle, expectedMsg)
        }

        val dialog: Dialog = ShadowAlertDialog.getLatestDialog()
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(expectedTitle, title.text)
        assertEquals(expectedMsg, message.text)
    }
}
