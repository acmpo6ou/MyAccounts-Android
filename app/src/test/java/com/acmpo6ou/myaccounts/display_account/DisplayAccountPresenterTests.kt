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

import com.acmpo6ou.myaccounts.account.DisplayAccountFragmentInter
import com.acmpo6ou.myaccounts.account.DisplayAccountPresenter
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DisplayAccountPresenterTests {
    lateinit var presenter: DisplayAccountPresenter
    lateinit var view: DisplayAccountFragmentInter
    private val fileName = Faker().str()

    @Before
    fun setup() {
        view = mock()
        val account = com.acmpo6ou.myaccounts.account
        account.attachedFiles = mutableMapOf("somefile.txt" to "This is some file.")
        presenter = DisplayAccountPresenter(view, account)
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
}
