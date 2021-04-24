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

package com.acmpo6ou.myaccounts.open_database

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityModule
import com.acmpo6ou.myaccounts.database.open_database.OpenDatabaseFragment
import com.acmpo6ou.myaccounts.database.open_database.OpenDatabaseViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(AppModule::class, MainActivityModule::class)
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class OpenDatabaseFragmentInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    @Singleton
    val app = MyApp()

    private val imm: InputMethodManager = mock()
    @BindValue
    @JvmField
    @ActivityScoped
    val superActivity: MainActivity = mock {
        on { getSystemService(Context.INPUT_METHOD_SERVICE) } doReturn imm
    }

    @BindValue
    @JvmField
    @ActivityScoped
    val mainActivityI: MainActivityI = mock()

    @BindValue
    @JvmField
    @Singleton
    val sharedPreferences: SharedPreferences = mock()

    @BindValue
    lateinit var spyModel: OpenDatabaseViewModel
    lateinit var model: OpenDatabaseViewModel

    lateinit var fragment: OpenDatabaseFragment
    private val b get() = fragment.b

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val faker = Faker()

    @Before
    fun setUp() {
        model = OpenDatabaseViewModel(mock(), Dispatchers.Unconfined, Dispatchers.Unconfined)
        spyModel = spy(model)

        app.databases = mutableListOf(Database("main"))
        hiltAndroidRule.inject()

        val bundle = Bundle()
        bundle.putInt("databaseIndex", 0)
        fragment = launchFragmentInHiltContainer(bundle)
    }

    @Test
    fun `'Open database' button should call startPasswordCheck`() {
        val password = faker.str()
        b.databasePassword.setText(password)

        b.openDatabase.performClick()
        verify(spyModel).startPasswordCheck(password, 0)
    }

    @Test
    fun `error tip should change when incorrectPassword changes`() {
        val errorMsg = context.resources.getString(R.string.password_error)

        // error tip should appear when incorrectPassword is true
        spyModel.incorrectPassword.value = true
        assertEquals(errorMsg, b.parentPassword.error)

        // and disappear when incorrectPassword is false
        spyModel.incorrectPassword.value = false
        assertNull(b.parentPassword.error)
    }

    @Test
    fun `error tip should be hidden when password changes`() {
        spyModel.incorrectPassword.value = true
        assertNotNull(b.parentPassword.error)

        b.databasePassword.setText(faker.str())
        assertNull(b.parentPassword.error)
    }

    @Test
    fun `should display or hide progress bar depending on 'loading' of view model`() {
        // when loading is true progress bar should be displayed and button - disabled
        spyModel.loading.value = true
        assertEquals(View.VISIBLE, b.progressLoading.visibility)
        assertFalse(b.openDatabase.isEnabled)

        // when loading false progress bar should be hidden and button - enabled
        spyModel.loading.value = false
        assertEquals(View.GONE, b.progressLoading.visibility)
        assertTrue(b.openDatabase.isEnabled)
    }
}
