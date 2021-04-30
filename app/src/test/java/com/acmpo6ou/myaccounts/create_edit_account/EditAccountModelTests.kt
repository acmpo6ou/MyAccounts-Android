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

package com.acmpo6ou.myaccounts.create_edit_account

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.account.create_edit_account.EditAccountViewModel
import com.acmpo6ou.myaccounts.account.create_edit_account.LoadFileModel
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.DbMap
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditAccountModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: EditAccountViewModel
    lateinit var model: LoadFileModel

    lateinit var myAccount: Account
    lateinit var dbMap: DbMap

    private val fileName = Faker().str()
    private val fileName2 = Faker().str()
    private val attachedFileName = Faker().str()
    private val fileContent = Faker().str()

    @Before
    fun setup() {
        myAccount = account.copy()
        myAccount.attachedFiles = mutableMapOf(
            fileName to Faker().str(),
            fileName2 to Faker().str(),
        )
        dbMap = mutableMapOf(account.accountName to myAccount)

        model = mock { on { loadFile(locationUri) } doReturn fileContent }
        viewModel = EditAccountViewModel(mock(), model).apply {
            accounts = dbMap
            initialize(account.accountName)
        }
    }

    @Test
    fun `applyPressed should create new account`() {
        viewModel.filePaths[attachedFileName] = locationUri

        // new account should have old attached files and new ones
        val expectedAccount = account.copy()
        expectedAccount.attachedFiles = (
            mutableMapOf(attachedFileName to fileContent) +
                myAccount.attachedFiles.toMutableMap()
            ) as MutableMap<String, String>

        viewModel.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertEquals(expectedAccount, viewModel.accounts[account.accountName])
        assertTrue(viewModel.finished.value!!)
    }

    @Test
    fun `applyPressed should handle any exception`() {
        val msg = faker.str()
        val exception = Exception(msg)
        doAnswer { throw exception }.whenever(model).loadFile(locationUri)

        viewModel.filePaths[fileName] = locationUri
        viewModel.applyPressed("", "", "", "", "", "")

        assertEquals(exception.toString(), viewModel.errorMsg.value)
        assertNotEquals(true, viewModel.finished.value)
    }

    @Test
    fun `applyPressed should delete old account`() {
        viewModel.applyPressed(
            "habr",
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertFalse(account.accountName in viewModel.accounts)
        assertTrue("habr" in viewModel.accounts)
    }

    @Test
    fun `applyPressed should not delete old account if there is an error`() {
        doAnswer { throw Exception() }.whenever(model).loadFile(locationUri)
        viewModel.filePaths[fileName] = locationUri

        viewModel.applyPressed("", "", "", "", "", "")
        assertTrue(account.accountName in viewModel.accounts)
    }

    @Test
    fun `validateName when name of Database didn't change through editing`() {
        // account already exists but it's being edited, so that doesn't count
        viewModel.validateName(account.accountName)
        assertFalse(viewModel.existsNameErr.value!!)
        assertFalse(viewModel.emptyNameErr.value!!)
    }

    @Test
    fun `initialize should fill filePaths with existing attached files`() {
        assertTrue(fileName in viewModel.filePaths)
        assertNull(viewModel.filePaths[fileName])
        assertTrue(fileName2 in viewModel.filePaths)
        assertNull(viewModel.filePaths[fileName2])
    }
}
