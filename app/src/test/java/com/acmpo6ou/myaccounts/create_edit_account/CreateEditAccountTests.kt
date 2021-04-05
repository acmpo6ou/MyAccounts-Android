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

import android.app.Activity
import com.acmpo6ou.myaccounts.ActivityResultTest
import com.acmpo6ou.myaccounts.account.create_edit_account.CreateEditAccountFragment
import com.acmpo6ou.myaccounts.account.create_edit_account.CreateAccountViewModel
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class TestFragment : CreateEditAccountFragment() {
    override val viewModel: CreateAccountViewModel = mock()
}

class CreateEditAccountTests : ActivityResultTest() {
    lateinit var fragment: CreateEditAccountFragment
    lateinit var viewModel: CreateAccountViewModel

    @Before
    fun setup() {
        fragment = TestFragment()
        fragment.myContext = mock()
        viewModel = fragment.viewModel
    }

    @Test
    fun `onActivityResult should call addFile when code is LOAD_FILE_RC`() {
        // call onActivityResult passing load file request code, result OK and intent
        fragment.onActivityResult(fragment.LOAD_FILE_RC, Activity.RESULT_OK, intent)
        verify(viewModel).addFile(locationUri, "")
    }

    @Test
    fun `onActivityResult should not call addFile when code is other than LOAD_FILE_RC`() {
        // call onActivityResult passing other request code, result OK and intent
        fragment.onActivityResult(OTHER_RC, Activity.RESULT_OK, intent)
        verify(viewModel, never()).addFile(eq(locationUri), anyString())
    }

    @Test
    fun `onActivityResult should not call addFile when result code is CANCELED`() {
        // call onActivityResult passing load file request code, result CANCELED and intent
        fragment.onActivityResult(fragment.LOAD_FILE_RC, Activity.RESULT_CANCELED, intent)
        verify(viewModel, never()).addFile(eq(locationUri), anyString())
    }
}
