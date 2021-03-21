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

package com.acmpo6ou.myaccounts.updates_activity

import android.content.Context
import android.content.Intent
import com.acmpo6ou.myaccounts.UpdatesActivity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class UpdatesActivityTests {
    lateinit var activity: UpdatesActivity

    @Before
    fun setup() {
        activity = UpdatesActivity()
        activity.viewModel = mock()
    }

    @Test
    fun `onComplete should call viewModel installUpdate`(){
        val context: Context = mock()
        val intent: Intent = mock()

        activity.onComplete.onReceive(context, intent)
        verify(activity.viewModel).installUpdate(context)
    }
}
