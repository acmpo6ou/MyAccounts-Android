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
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.ui.account.CreateAccountViewModel
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateAccountModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = CreateAccountViewModel()
    val fileName = Faker().str()

    @Before
    fun setup() {
        val app: MyApp = mock { on { contentResolver } doReturn contentResolver }
        model.initialize(app, databaseMap.copy())
    }

    @Test
    fun `applyPressed should create new account`() {
        model.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertEquals(account, model.accounts[account.accountName])
    }

    @Test
    fun `applyPressed should set finished to true`() {
        model.applyPressed(
            account.accountName,
            account.username,
            account.email,
            account.password,
            account.date,
            account.comment
        )
        assertTrue(model.finished)
    }

    @Test
    fun `addFile should add file to filePaths`() {
        model.addFile(locationUri, fileName)
        assertTrue(fileName in model.filePaths)
        assertEquals(locationUri, model.filePaths[fileName])
    }

    @Test
    fun `addFile should notify about addition`() {
        model.addFile(locationUri, fileName)
        assertEquals(0, model.notifyAdded.value)
    }

    @Test
    fun `removeFile should remove file from filePaths`() {
        model.filePaths[fileName] = locationUri
        model.removeFile(0)
        assertFalse(fileName in model.filePaths)
    }

    @Test
    fun `removeFile should notify about removal`() {
        model.filePaths[fileName] = locationUri
        model.removeFile(0)
        assertEquals(0, model.notifyRemoved.value)
    }
}
