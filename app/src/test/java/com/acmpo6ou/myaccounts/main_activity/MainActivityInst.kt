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

package com.acmpo6ou.myaccounts.main_activity

import android.content.Context
import android.os.Looper
import android.view.View
import androidx.core.view.GravityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesBindings
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesModelI
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesPresenterI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityBindings
import com.acmpo6ou.myaccounts.database.main_activity.MainModelI
import com.acmpo6ou.myaccounts.database.main_activity.MainPresenterI
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(
    AppModule::class,
    MainActivityBindings::class, DatabasesBindings::class
)
@RunWith(RobolectricTestRunner::class)
class MainActivityInst : NoInternet {
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

    lateinit var scenario: ActivityScenario<MainActivity>
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val exitTip = context.resources.getString(R.string.exit_tip)

    @Before
    fun setup() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        scenario = launch(MainActivity::class.java)
    }

    @Test
    fun `'Import database' should call presenter importSelected`() {
        scenario.onActivity {
            selectNavigationItem(R.id.import_database, it)
            verify(presenter).importSelected()

            // all other methods should not be called
            verifyNoMoreInteractions(presenter)
        }
    }

    @Test
    fun `navigation drawer should be closed when any of it's items is selected`() {
        scenario.onActivity {
            it.drawerLayout = mock()
            selectNavigationItem(R.id.import_database, it)
            verify(it.drawerLayout).closeDrawer(GravityCompat.START)
        }
    }

    @Test
    fun `importDialog should start an intent`() {
        scenario.onActivity { it.importDialog() }
        val intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertNotNull(intent)
    }

    @Test
    fun `showExitTip should display snackbar`() {
        scenario.onActivity {
            it.showExitTip()

            // this is because of some Robolectric main looper problems
            shadowOf(Looper.getMainLooper()).idle()

            // get the snackbar
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // check the snackbar's message
            assertEquals(exitTip, snackbar?.text)
        }
    }
}
