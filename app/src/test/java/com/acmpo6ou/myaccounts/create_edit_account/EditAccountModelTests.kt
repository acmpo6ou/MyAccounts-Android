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
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.account.create_edit_account.EditAccountViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.DbMap
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class EditAccountModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = EditAccountViewModel()

    lateinit var myAccount: Account
    lateinit var dbMap: DbMap

    private val fileName = Faker().str()
    private val fileName2 = Faker().str()

    private val attachedFileName = "test.txt"
    override val location = "$accountsDir/$attachedFileName"
    private val decodedContent = "This is a simple file.\nTo test PyQtAccounts.\nHello World!\n"
    private val encodedContent =
        "VGhpcyBpcyBhIHNpbXBsZSBmaWxlLgpUbyB0ZXN0IFB5UXRBY2NvdW50cy4KSGVsbG8gV29ybGQhCg=="

    @Before
    fun setup() {
        myAccount = account.copy()
        myAccount.attachedFiles = mutableMapOf(
            fileName to Faker().str(),
            fileName2 to Faker().str(),
        )
        dbMap = mutableMapOf(account.accountName to myAccount)

        val app: MyApp = mock { on { contentResolver } doReturn contentResolver }
        model.initialize(app, dbMap, account.accountName)
    }

    @Test
    fun `applyPressed should create new account`() {
        // prepare attached file to load
        model.filePaths[attachedFileName] = locationUri
        File(location).apply {
            createNewFile()
            writeText(decodedContent)
        }
        setupInputResolver()

        // new account should have old attached files and new ones
        val expectedAccount = account.copy()
        expectedAccount.attachedFiles = (
            mutableMapOf(attachedFileName to encodedContent) +
                myAccount.attachedFiles.toMutableMap()
            ) as MutableMap<String, String>

        model.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertEquals(expectedAccount, model.accounts[account.accountName])
        assertTrue(model.finished.value!!)
    }

    @Test
    fun `applyPressed should handle any exception`() {
        val msg = faker.str()
        val exception = Exception(msg)

        model.model = mock()
        doAnswer { throw exception }.whenever(model.model).loadFile(locationUri)

        model.filePaths[fileName] = locationUri
        model.applyPressed("", "", "", "", "", "")

        assertEquals(exception.toString(), model.errorMsg.value)
        assertNotEquals(true, model.finished.value)
    }

    @Test
    fun `applyPressed should delete old account`() {
        model.applyPressed(
            "habr",
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertFalse(account.accountName in model.accounts)
        assertTrue("habr" in model.accounts)
    }

    @Test
    fun `applyPressed should not delete old account if there is an error`() {
        model.model = mock()
        doAnswer { throw Exception() }.whenever(model.model).loadFile(locationUri)

        model.filePaths[fileName] = locationUri
        model.applyPressed("", "", "", "", "", "")

        assertTrue(account.accountName in model.accounts)
    }

    @Test
    fun `validateName when name of Database didn't change through editing`() {
        // account already exists but it's being edited, so that doesn't count
        model.validateName(account.accountName)
        assertFalse(model.existsNameErr.value!!)
        assertFalse(model.emptyNameErr.value!!)
    }

    @Test
    fun `initialize should fill filePaths with existing attached files`() {
        assertTrue(fileName in model.filePaths)
        assertNull(model.filePaths[fileName])
        assertTrue(fileName2 in model.filePaths)
        assertNull(model.filePaths[fileName2])
    }
}
