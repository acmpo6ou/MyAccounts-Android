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

package com.acmpo6ou.myaccounts.superclass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.superclass.DatabaseViewModel
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

open class TestDatabaseModel : DatabaseViewModel {
    override var defaultDispatcher: CoroutineDispatcher = mock()
    override var uiDispatcher: CoroutineDispatcher = mock()
    override var coroutineJob: Job? = mock()

    override var app: MyApp = MyApp()
    override var titleStart: String = ""
    override var SRC_DIR: String = ""
    override var databaseIndex: Int = 0

    override var _title: MutableLiveData<String> = mock()
    override var _loading: MutableLiveData<Boolean> = mock()
    override var errorMsg_: MutableLiveData<String> = MutableLiveData()
}

class DatabaseViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = TestDatabaseModel()
    private val faker = Faker()

    @Test
    fun `initialize should set title`() {
        val titleStart = faker.str()
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, "", titleStart, 0)
        assertEquals("$titleStart main", model.title)
    }
}
