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

package com.acmpo6ou.myaccounts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.MyApp
import org.junit.Before

/**
 * Used by some instrumentation tests to, for example, avoid checking for updates.
 */
interface InstTest {
    @Before
    fun setupApp(){
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val app = context.applicationContext as MyApp
        app.testing = true
    }
}