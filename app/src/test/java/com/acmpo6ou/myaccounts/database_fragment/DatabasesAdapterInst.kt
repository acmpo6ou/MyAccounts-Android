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

package com.acmpo6ou.myaccounts.database_fragment

import android.view.Menu.FLAG_ALWAYS_PERFORM_CLOSE
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowPopupMenu

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabasesAdapterInst {
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>
    lateinit var presenter: DatabasesPresenterInter
    var recycler: RecyclerView? = null
    var itemLayout: View? = null
    var itemLayout2: View? = null

    @Before
    fun setUp() {
        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)

        // mock the list of databases for test
        val databases = mutableListOf(
                Database("main"), // locked
                Database("test", password = "123") // opened
        )
        presenter = mock()
        whenever(presenter.databases).thenReturn(databases)

        databaseScenario.onFragment {
            // set mocked presenter
            it.presenter = presenter

            // find recycler
            recycler = it.view?.findViewById(R.id.databasesList)
        }
        // measure and lay recycler out as is needed so we can later obtain its items
        recycler?.measure(0, 0)
        recycler?.layout(0, 0, 100, 10000)

        // get item layouts from recycler
        itemLayout = recycler?.getChildAt(0)
        itemLayout2 = recycler?.getChildAt(1)
    }

    @Test
    fun `click on recycler item should call openDatabase`(){
        // perform click on database item
        itemLayout?.performClick()

        verify(presenter).openDatabase(eq(0))
    }

    @Test
    fun `database item should have appropriate name`(){
        val databaseName = itemLayout?.findViewById<TextView>(R.id.itemName)
        assertEquals(
                "item in databases list has incorrect name!",
                "main",
                databaseName?.text
        )
    }

    @Test
    fun `database item should have locked icon when isOpen of Database is false`(){
        // the first database in the list above doesn't have password set hence isOpen is false
        val itemLock = itemLayout?.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(
    "database item has incorrect lock icon when isOpen of Database is false!",
                R.drawable.ic_locked,
            itemLock?.tag,
        )
    }

    @Test
    fun `database item should have opened icon when isOpen of Database is true`(){
        // the second database in the list above does have password set hence isOpen is true
        val itemLock = itemLayout2?.findViewById<ImageView>(R.id.itemIcon)
        assertEquals(
                "database item has incorrect lock icon when isOpen of Database is true!",
                R.drawable.ic_opened,
                itemLock?.tag,
        )
    }

    @Test
    fun `clicking on 'Close' should call closeSelected`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout2?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Close` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.close_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        verify(presenter).closeSelected(1)
    }


    @Test
    fun `clicking on 'Close' should not call closeSelected when database isn't open`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Close` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.close_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        verify(presenter, never()).closeSelected(0)
    }

    @Test
    fun `clicking on 'Delete' should call deleteSelected`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Delete` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.delete_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        verify(presenter).deleteSelected(0)
    }

    @Test
    fun `clicking on 'Export' should call exportSelected`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Export` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.export_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        verify(presenter).exportSelected(0)
    }

    @Test
    fun `clicking on 'Edit' should call editSelected`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout2?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Edit` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.edit_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        // verify that we called the appropriate method
        verify(presenter).editSelected(1)
    }

    @Test
    fun `clicking on 'Edit' should not call editSelected when database isn't open`(){
        // click on 3 dots to display popup menu
        val dotsMenu = itemLayout?.findViewById<TextView>(R.id.dots_menu)
        dotsMenu?.performClick()

        // find the popup menu and click on `Edit` item
        val menu = ShadowPopupMenu.getLatestPopupMenu().menu
        menu.performIdentifierAction(R.id.edit_database_item, FLAG_ALWAYS_PERFORM_CLOSE)

        verify(presenter, never()).editSelected(0)
    }
}