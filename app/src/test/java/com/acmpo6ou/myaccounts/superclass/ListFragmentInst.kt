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

package com.acmpo6ou.myaccounts.superclass

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.ListFragment
import com.acmpo6ou.myaccounts.core.superclass.ListPresenter
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

class TestListFragment : ListFragment(){
    override var items: List<String> = listOf()
    override val actionCreateItem = 0
    override val adapter: RecyclerView.Adapter<*> = mock()
    override val presenter: ListPresenter = mock()
}

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ListFragmentInst {
    lateinit var scenario: FragmentScenario<TestListFragment>

    @Before
    fun setup(){
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun `checkListPlaceholder should hide placeholder when list has items`(){
        scenario.onFragment {
            it.items = listOf( Faker().str() )
            it.checkListPlaceholder()

            // placeholder should be invisible
            val placeholder = it.view?.findViewById<TextView>(R.id.no_items)
            Assert.assertEquals(View.GONE, placeholder?.visibility)

            // while the list should be visible
            val list = it.view?.findViewById<RecyclerView>(R.id.itemsList)
            Assert.assertEquals(View.VISIBLE, list?.visibility)
        }
    }

    @Test
    fun `checkListPlaceholder should display placeholder when list has no items`(){
        scenario.onFragment {
            it.items = listOf()
            it.checkListPlaceholder()

            // placeholder should be invisible
            val placeholder = it.view?.findViewById<TextView>(R.id.no_items)
            Assert.assertEquals(View.VISIBLE, placeholder?.visibility)

            // while the list should be visible
            val list = it.view?.findViewById<RecyclerView>(R.id.itemsList)
            Assert.assertEquals(View.GONE, list?.visibility)
        }
    }
}