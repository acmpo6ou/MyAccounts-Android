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
import com.acmpo6ou.myaccounts.account.create_edit_account.CreateAccountViewModel
import com.acmpo6ou.myaccounts.account.create_edit_account.LoadFileModel
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

class CreateAccountModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: CreateAccountViewModel
    lateinit var model: LoadFileModel

    private val fileName = Faker().str()
    private val attachedFileName = "test.txt"
    override val location = "$accountsDir/$attachedFileName"

    private val decodedContent = "This is a simple file.\nTo test PyQtAccounts.\nHello World!\n"
    private val encodedContent =
        "VGhpcyBpcyBhIHNpbXBsZSBmaWxlLgpUbyB0ZXN0IFB5UXRBY2NvdW50cy4KSGVsbG8gV29ybGQhCg=="

    @Before
    fun setup() {
        model = mock { on { loadFile(locationUri) } doReturn encodedContent }
        viewModel = CreateAccountViewModel(mock(), model)
        viewModel.accounts = databaseMap.copy()
    }

    @Test
    fun `applyPressed should create new account`() {
        // prepare attached file to load
        viewModel.filePaths[attachedFileName] = locationUri
        File(location).apply {
            createNewFile()
            writeText(decodedContent)
        }
        setupInputResolver()

        val expectedAccount = account.copy()
        expectedAccount.attachedFiles = mutableMapOf(attachedFileName to encodedContent)

        viewModel.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertEquals(expectedAccount, viewModel.accounts[account.accountName])
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
    fun `applyPressed should set finished to true`() {
        viewModel.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertTrue(viewModel.finished.value!!)
    }

    @Test
    fun `addFile should add file to filePaths`() {
        viewModel.addFile(locationUri, fileName)
        assertTrue(fileName in viewModel.filePaths)
        assertEquals(locationUri, viewModel.filePaths[fileName])
    }

    @Test
    fun `addFile should notify about addition`() {
        viewModel.addFile(locationUri, fileName)
        assertEquals(0, viewModel.notifyAdded.value)
    }

    @Test
    fun `removeFile should remove file from filePaths`() {
        viewModel.filePaths[fileName] = locationUri
        viewModel.removeFile(0)
        assertFalse(fileName in viewModel.filePaths)
    }

    @Test
    fun `removeFile should notify about removal`() {
        viewModel.filePaths[fileName] = locationUri
        viewModel.removeFile(0)
        assertEquals(0, viewModel.notifyRemoved.value)
    }
}
