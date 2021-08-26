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

package com.acmpo6ou.myaccounts.my_app

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.str
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class MyAppTests {
    lateinit var app: MyApp

    @Before
    fun setup() {
        app = spy()
        app.prefs = SPMockBuilder().createSharedPreferences()
    }

    @Test
    fun `onAppForegrounded should call startLockActivity when lock_app setting is true`() {
        app.databases = mutableListOf(Database("main", Faker().str()))
        app.prefs.edit().putBoolean("lock_app", true).commit()

        app.onAppForegrounded()
        verify(app).startLockActivity()
    }

    @Test
    fun `onAppForegrounded should NOT call startLockActivity when lock_app setting is false`() {
        app.databases = mutableListOf(Database("main", Faker().str()))
        app.prefs.edit().putBoolean("lock_app", false).commit()

        app.onAppForegrounded()
        verify(app, never()).startLockActivity()
    }

    @Test
    fun `onAppForegrounded should call startLockActivity when there are opened databases`() {
        app.databases = mutableListOf(Database("main", Faker().str()))
        app.onAppForegrounded()
        verify(app).startLockActivity()
    }

    @Test
    fun `onAppForegrounded should NOT call startLockActivity when no database is opened`() {
        app.databases = mutableListOf(Database("main"))
        app.onAppForegrounded()
        verify(app, never()).startLockActivity()
    }
}
