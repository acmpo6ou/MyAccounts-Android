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

import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.acmpo6ou.myaccounts.ui.DatabasesAdapter
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabasesAdapterInst {
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>
    lateinit var presenter: DatabasesPresenterInter
    var recycler: RecyclerView? = null

    @Before
    fun setUp() {
        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)

        // mock the list of databases for test
        val databases = listOf(
                Database("main")
        )
        presenter = mock()
        whenever(presenter.databases).thenReturn(databases)

        databaseScenario.onFragment {
            // set mocked presenter
            it.presenter = presenter

            // find recycler measure and lay it out as is needed so we can later obtain its
            // items
            recycler = it.view?.findViewById(R.id.databasesList)
            recycler?.measure(0, 0)
            recycler?.layout(0, 0, 100, 10000)
        }
    }

    @Test
    fun `click on recycler item should call openDatabase`(){
        databaseScenario.onFragment {
            // perform click on database item
            recycler?.getChildAt(0)?.performClick()
        }

        verify(presenter).openDatabase(eq(0))
    }

    @Test
    fun `database item should have appropriate name`(){
        databaseScenario.onFragment {
            val itemLayout = recycler?.getChildAt(0)
            val databaseName = itemLayout?.findViewById<TextView>(R.id.databaseItemName)
            assertEquals(
                    "item in databases list has incorrect name!",
                    "main",
                    databaseName?.text
            )
        }
    }
}