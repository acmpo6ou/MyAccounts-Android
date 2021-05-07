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

package com.acmpo6ou.myaccounts.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.utils.GenPassDialog
import com.acmpo6ou.myaccounts.core.utils.hasoneof
import com.google.android.material.textfield.TextInputEditText
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase.assertTrue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GenPassDialogInst {
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var dialog: GenPassDialog

    private val pass1: TextInputEditText = mock()
    private val pass2: TextInputEditText = mock()

    @Before
    fun setup() {
        dialog = GenPassDialog(context, pass1, pass2)
    }

    @Test
    fun `click on generateButton should generate password of correct length`() {
        // default length should be 16
        assertEquals(16, dialog.length.value)

        dialog.generateButton.performClick()
        verify(pass1).setText(argThat<String> { length == 16 })
    }

    @Test
    fun `click on generateButton should generate password using correct characters`() {
        // password should contain only upper letters and digits
        dialog.lowerBox.isChecked = false
        dialog.punctBox.isChecked = false

        dialog.generateButton.performClick()
        argumentCaptor<String> {
            verify(pass1).setText(capture())

            assertTrue(firstValue hasoneof dialog.digits)
            assertTrue(firstValue hasoneof dialog.upper)
            assertFalse(firstValue hasoneof dialog.lower)
            assertFalse(firstValue hasoneof dialog.punctuation)
        }
    }

    @Test
    fun `generated password should be set on password fields`() {
        // passwords from both fields
        var text1 = ""
        var text2 = ""

        dialog.generateButton.performClick()

        // get passwords from fields
        argumentCaptor<String> {
            verify(pass1).setText(capture())
            text1 = firstValue
        }
        argumentCaptor<String> {
            verify(pass2).setText(capture())
            text2 = firstValue
        }

        // check passwords
        assertNotEquals("", text1)
        assertEquals(text1, text2)
    }

    @Test
    fun `should not set password if no character check boxes selected`() {
        // all checkboxes are unchecked
        dialog.lowerBox.isChecked = false
        dialog.upperBox.isChecked = false
        dialog.digitsBox.isChecked = false
        dialog.punctBox.isChecked = false

        dialog.generateButton.performClick()
        verify(pass1, never()).setText(anyString())
        verify(pass2, never()).setText(anyString())
    }

    @Test
    fun `genPass should generate password from passed characters only`() {
        val password = dialog.genPass(16, listOf(dialog.digits, dialog.lower))
        assertTrue(password hasoneof dialog.digits)
        assertTrue(password hasoneof dialog.lower)
        assertFalse(password hasoneof dialog.upper)
        assertFalse(password hasoneof dialog.punctuation)
    }

    @Test
    fun `genPass should generate password of specified length`() {
        val password = dialog.genPass(16, dialog.allChars)
        assertEquals(16, password.length)
    }
}
