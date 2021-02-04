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

import androidx.viewbinding.ViewBinding
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.SuperActivity
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenterInter
import com.acmpo6ou.myaccounts.selectNavigationItem
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test

open class TestActivity : SuperActivity() {
    override val b: ViewBinding = mock()
    override var presenter: SuperPresenterInter = mock()
    override val mainFragmentId = 0

    override fun noUpdates() {
    }
}

class SuperActivityTests {
    private lateinit var activity: TestActivity
    private var presenter
        get() = activity.presenter
        set(value) {activity.presenter = value}

    @Before
    fun setup(){
        activity = TestActivity()
        presenter = mock()
    }

    // shortcut
    private fun selectItem(itemId: Int) = selectNavigationItem(itemId, activity)

    @Test
    fun `'Check for updates' should call presenter checkUpdatesSelected`(){
        selectItem(R.id.check_for_updates)
        verify(presenter).checkUpdatesSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'Changelog' should call presenter navigateToChangelog`(){
        selectItem(R.id.changelog)
        verify(presenter).navigateToChangelog()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'Settings' should call presenter navigateToSettings`(){
        selectItem(R.id.settings)
        verify(presenter).navigateToSettings()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'About' should call presenter navigateToAbout`(){
        selectItem(R.id.about)
        verify(presenter).navigateToAbout()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }
}