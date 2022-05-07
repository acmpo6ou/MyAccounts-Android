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

package com.acmpo6ou.myaccounts.create_edit_account

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.account.create_edit_account.LoadFileModel
import com.acmpo6ou.myaccounts.accountsDir
import com.acmpo6ou.myaccounts.MyApp
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class LoadFileModelTests : ModelTest() {
    override val location = "$accountsDir/file.txt"

    @Test
    fun `loadFile should load file and encode it in base64`() {
        val decodedContent = "This is a simple file.\nTo test PyQtAccounts.\nHello World!\n"
        val encodedContent =
            "VGhpcyBpcyBhIHNpbXBsZSBmaWxlLgpUbyB0ZXN0IFB5UXRBY2NvdW50cy4KSGVsbG8gV29ybGQhCg=="

        File(location).apply {
            createNewFile()
            writeText(decodedContent)
        }

        setupInputResolver()
        val app: MyApp = mock { on { contentResolver } doReturn contentResolver }
        val model = LoadFileModel(app)

        val content = model.loadFile(locationUri)
        assertEquals(encodedContent, content)
    }
}
