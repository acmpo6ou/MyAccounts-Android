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

package com.acmpo6ou.myaccounts.display_account

import android.content.Context
import android.net.Uri
import com.acmpo6ou.myaccounts.account
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountFragmentInter
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountModelInter
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountPresenter
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class DisplayAccountPresenterTests {
    lateinit var presenter: DisplayAccountPresenter
    lateinit var view: DisplayAccountFragmentInter
    lateinit var model: DisplayAccountModelInter

    private val fileName = Faker().str()
    private val content = Faker().str()
    private val destinationUri: Uri = mock()

    @Before
    fun setup() {
        val mockContext: Context = mock { on { contentResolver } doReturn mock() }
        val mockAccount = account
        mockAccount.attachedFiles = mutableMapOf(fileName to content)

        view = mock {
            on { myContext } doReturn mockContext
            on { account } doReturn mockAccount
        }
        model = mock()

        presenter = DisplayAccountPresenter(view)
        presenter.model = model
    }

    @Test
    fun `fileSelected should save fileName to selectedFile`() {
        presenter.fileSelected(fileName)
        assertEquals(fileName, presenter.selectedFile)
    }

    @Test
    fun `fileSelected should call view saveFileDialog`() {
        presenter.fileSelected(fileName)
        verify(view).saveFileDialog(fileName)
    }

    @Test
    fun `saveFile should call model saveFile`() {
        presenter.selectedFile = fileName
        presenter.saveFile(destinationUri)
        verify(model).saveFile(destinationUri, content)
    }

    @Test
    fun `saveFile should call view showSuccess`() {
        presenter.selectedFile = fileName
        presenter.saveFile(destinationUri)

        verify(view).showSuccess()
        verify(view, never()).fileCorrupted()
        verify(view, never()).showError(anyString())
    }

    @Test
    fun `saveFile should handle IllegalArgumentException`() {
        whenever(model.saveFile(destinationUri, content))
            .doAnswer { throw IllegalArgumentException() }
        presenter.selectedFile = fileName

        presenter.saveFile(destinationUri)
        verify(view).fileCorrupted()
        verify(view, never()).showError(anyString())
        verify(view, never()).showSuccess()
    }

    @Test
    fun `saveFile should handle any Exception`() {
        val msg = Faker().str()
        val exception = Exception(msg)
        whenever(model.saveFile(destinationUri, content))
            .doAnswer { throw exception }
        presenter.selectedFile = fileName

        presenter.saveFile(destinationUri)
        verify(view).showError(exception.toString())
        verify(view, never()).fileCorrupted()
        verify(view, never()).showSuccess()
    }
}
