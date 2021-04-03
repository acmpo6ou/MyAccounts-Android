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

import android.app.Activity
import com.acmpo6ou.myaccounts.ActivityResultTest
import com.acmpo6ou.myaccounts.account.DisplayAccountPresenterInter
import com.acmpo6ou.myaccounts.ui.account.DisplayAccountFragment
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class DisplayAccountTests : ActivityResultTest() {
    lateinit var fragment: DisplayAccountFragment
    lateinit var presenter: DisplayAccountPresenterInter

    @Before
    fun setup() {
        presenter = mock()
        fragment = DisplayAccountFragment()
        fragment.presenter = presenter
    }

    @Test
    fun `onActivityResult should call saveFile when code is SAVE_FILE_RC`() {
        // call onActivityResult passing save file request code, result OK and intent
        fragment.onActivityResult(fragment.SAVE_FILE_RC, Activity.RESULT_OK, intent)
        verify(presenter).saveFile(locationUri)
    }

    @Test
    fun `onActivityResult should not call saveFile when code is other than SAVE_FILE_RC`() {
        // call onActivityResult passing other request code, result OK and intent
        fragment.onActivityResult(OTHER_RC, Activity.RESULT_OK, intent)
        verify(presenter, never()).saveFile(locationUri)
    }

    @Test
    fun `onActivityResult should not call saveFile when result code is CANCELED`() {
        // call onActivityResult passing save file request code, result CANCELED and intent
        fragment.onActivityResult(fragment.SAVE_FILE_RC, Activity.RESULT_CANCELED, intent)
        verify(presenter, never()).saveFile(locationUri)
    }
}
