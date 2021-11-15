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

package com.acmpo6ou.myaccounts.create_edit_database

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.database.create_edit_database.RenameDatabaseFragment
import com.acmpo6ou.myaccounts.database.create_edit_database.RenameDatabaseViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityModule
import com.acmpo6ou.myaccounts.launchFragmentInHiltContainer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.ActivityScoped
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
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(AppModule::class, MainActivityModule::class)
@RunWith(RobolectricTestRunner::class)
class RenameDatabaseFragmentInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    lateinit var viewModel: RenameDatabaseViewModel

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fragment: RenameDatabaseFragment
    private val b get() = fragment.b

    @BindValue
    @JvmField
    @ActivityScoped
    val superActivity: MainActivity = mock { on { myContext } doReturn context }

    @BindValue
    @JvmField
    @ActivityScoped
    val mainActivityI: MainActivityI = mock()

    @BindValue
    @JvmField
    @Singleton
    val sharedPreferences: SharedPreferences = mock()

    @BindValue
    @Singleton
    lateinit var app: MyApp
    lateinit var db: Database

    @Before
    fun setup() {
        db = Database("main")
        app = mock {
            on { resources } doReturn context.resources
            on { databases } doReturn mutableListOf(db)
        }

        viewModel = RenameDatabaseViewModel(app)
        setupFragment()
    }

    private fun setupFragment() {
        val bundle = Bundle()
        bundle.putInt("databaseIndex", 0)
        fragment = launchFragmentInHiltContainer(bundle)
    }

    @Test
    fun `initForm should fill name field`() {
        assertEquals(db.name, b.databaseName.text.toString())
    }

    @Test
    fun `press on saveButton should call savePressed`() {
        val model = RenameDatabaseViewModel(app)
        viewModel = spy(model)
        setupFragment()

        b.databaseName.setText(db.name)
        b.saveButton.performClick()
        verify(fragment.viewModel).savePressed(db.name)
    }
}
