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

package com.acmpo6ou.myaccounts.superclass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.str
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateEditViewModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    private val model = TestModel()

    override val password = faker.str()
    @Before
    fun setup() {
        model.app.databases =
            mutableListOf(
                Database("main"),
                Database("test", "123")
            )
    }

    @Test
    fun `validateName should change emptyNameErr`() {
        // if name isn't empty emptyNameErr should be false
        model.validateName(faker.str())
        assertFalse(model.emptyNameErr.value!!)

        // if name is empty emptyNameErr should be true
        model.validateName("")
        assertTrue(model.emptyNameErr.value!!)
    }

    @Test
    fun `validateName should set existsNameErr to true when Database with such name exists`() {
        model.validateName("main")
        assertTrue(model.existsNameErr.value!!)

        // and even if database is opened
        model.validateName("test") // test is opened
        assertTrue(model.existsNameErr.value!!)
    }

    @Test
    fun `validatePasswords should change emptyPassErr`() {
        // when passwords are empty
        model.validatePasswords("", "")
        assertTrue(model.emptyPassErr.value!!)

        // when they aren't
        model.validatePasswords(faker.str(), faker.str())
        assertFalse(model.emptyPassErr.value!!)
    }

    @Test
    fun `validatePasswords should change diffPassErr`() {
        val pass1 = faker.str()
        val pass2 = faker.str()

        // when passwords are different
        model.validatePasswords(pass1, pass2)
        assertTrue(model.diffPassErr.value!!)

        // when passwords are same
        model.validatePasswords(pass1, pass1)
        assertFalse(model.diffPassErr.value!!)
    }
}

private open class TestModel : CreateEditViewModel() {
    override val app: MyApp = MyApplication()
    override var errorMsg = MutableLiveData<String>()
    override val itemNames: List<String> get() = app.databases.map { it.name }
}
