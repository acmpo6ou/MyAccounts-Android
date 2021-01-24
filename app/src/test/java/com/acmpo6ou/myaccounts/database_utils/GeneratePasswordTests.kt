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

package com.acmpo6ou.myaccounts.database_utils

import com.acmpo6ou.myaccounts.core.GeneratePassword
import com.acmpo6ou.myaccounts.core.hasoneof
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Test

class GeneratePasswordTests {
    val dialog = GeneratePassword(mock(), mock(), mock())

    @Test
    fun `genPass should generate password from passed characters only`(){
        val password = dialog.genPass(16, listOf(dialog.digits, dialog.lower))
        assertTrue(password hasoneof dialog.digits)
        assertTrue(password hasoneof dialog.lower)
        assertFalse(password hasoneof dialog.upper)
        assertFalse(password hasoneof dialog.punctuation)
    }

    @Test
    fun `genPass should generate password of specified length`(){
        val password = dialog.genPass(16, dialog.allChars)
        assertEquals(16, password.length)
    }
}