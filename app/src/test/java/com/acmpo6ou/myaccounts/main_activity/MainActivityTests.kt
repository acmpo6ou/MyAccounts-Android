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

package com.acmpo6ou.myaccounts.main_activity

import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainPresenterInter
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.robolectric.fakes.RoboMenuItem

class MainActivityTests {
    private lateinit var activity: MainActivity
    private lateinit var presenter: MainPresenterInter

    @Before
    fun setup(){
        activity = MainActivity()

        // mock presenter
        presenter = mock()
        activity.presenter = presenter
    }

    @Test
    fun `import database should call presenter importSelected`(){
        // simulate selecting `Import database` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.import_database))

        verify(presenter).importSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `check for updates should call presenter checkUpdatesSelected`(){
        // simulate selecting `Check for updates` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.check_for_updates))

        verify(presenter).checkUpdatesSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `changelog should call presenter navigateToChangelog`(){
        // simulate selecting `Changelog` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.changelog))

        verify(presenter).navigateToChangelog()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }
}