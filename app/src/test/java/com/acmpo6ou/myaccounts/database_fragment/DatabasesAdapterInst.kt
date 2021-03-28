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

package com.acmpo6ou.myaccounts.database_fragment

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.clickMenuItem
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.database.DatabaseFragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DatabasesAdapterInst {
    lateinit var scenario: FragmentScenario<DatabaseFragment>
    lateinit var presenter: DatabasesPresenterInter

    var recycler: RecyclerView? = null
    var itemLayout: View? = null
    var itemLayout2: View? = null

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)

        // mock the list of databases for test
        val mockDatabases = mutableListOf(
            Database("main"), // locked
            Database("test", "123")
        ) // opened
        presenter = mock { on { databases } doReturn mockDatabases }

        scenario.onFragment {
            it.presenter = presenter
            recycler = it.view?.findViewById(R.id.itemsList)
        }
        // measure and lay recycler out as is needed so we can later obtain its items
        recycler?.measure(0, 0)
        recycler?.layout(0, 0, 100, 10000)

        // get item layouts from recycler
        itemLayout = recycler?.getChildAt(0)
        itemLayout2 = recycler?.getChildAt(1)
    }

    @Test
    fun `click on recycler item should call openDatabase`() {
        itemLayout?.performClick()
        verify(presenter).openDatabase(0)
    }

    @Test
    fun `database item should have appropriate name`() {
        val databaseName = itemLayout?.findViewById<TextView>(R.id.itemName)
        assertEquals("main", databaseName?.text)
    }

    @Test
    fun `database item should have locked icon when isOpen of Database is false`() {
        // the first database in the list above is closed
        val itemLock = itemLayout?.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(
            "database item has unlocked icon when isOpen of Database is false!",
            R.drawable.ic_locked, itemLock?.tag
        )
    }

    @Test
    fun `database item should have opened icon when isOpen of Database is true`() {
        // the second database in the list above is opened
        val itemLock = itemLayout2?.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(
            "database item has locked icon when isOpen of Database is true!",
            R.drawable.ic_opened, itemLock?.tag
        )
    }

    @Test
    fun `clicking on 'Close' should call closeSelected`() {
        clickMenuItem(itemLayout2, R.id.close_database_item)
        verify(presenter).closeSelected(1)
    }

    @Test
    fun `clicking on 'Close' should not call closeSelected when database is already closed`() {
        clickMenuItem(itemLayout, R.id.close_database_item)
        verify(presenter, never()).closeSelected(0)
    }

    @Test
    fun `clicking on 'Delete' should call deleteSelected`() {
        clickMenuItem(itemLayout, R.id.delete_database_item)
        verify(presenter).deleteSelected(0)
    }

    @Test
    fun `clicking on 'Export' should call exportSelected`() {
        clickMenuItem(itemLayout, R.id.export_database_item)
        verify(presenter).exportSelected(0)
    }

    @Test
    fun `clicking on 'Edit' should call editSelected`() {
        clickMenuItem(itemLayout2, R.id.edit_database_item)
        verify(presenter).editSelected(1)
    }

    @Test
    fun `clicking on 'Edit' should not call editSelected when database is closed`() {
        clickMenuItem(itemLayout, R.id.edit_database_item)
        verify(presenter, never()).editSelected(0)
    }
}
