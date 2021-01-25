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

package com.acmpo6ou.myaccounts.create_edit_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.CreateEditViewModel
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.str
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule

// CreateDatabaseViewModel class is abstract and we can't instantiate it for tests
class TestModel : CreateEditViewModel(){
    override suspend fun createDatabase(name: String, password: String) {

    }
}

class CreateEditViewModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = TestModel()
    lateinit var spyModel: TestModel

    private val name = faker.str()
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup(){
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, SRC_DIR)
        spyModel = spy(model){ on{generateSalt()} doReturn salt }
        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.defaultDispatcher = Dispatchers.Unconfined
    }
}