/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts

import android.content.Intent
import android.net.Uri
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

/**
 * Super class test for DatabaseFragmentInst and MainActivityInst.
 *
 * Contains some helper methods and properties.
 */
open class DatabaseViewTest {
    lateinit var intent: Intent
    val faker = Faker()
    val location: String = faker.file().fileName()
    val OTHER_RC = faker.number().digit().toInt()

    /**
     * Helper function to mock intent, so that it would contain uri with random string.
     */
    fun mockIntent(){
        intent = mock()
        val uri = mock<Uri>()
        whenever(uri.toString()).thenReturn(location)
        whenever(intent.data).thenReturn(uri)
    }
}