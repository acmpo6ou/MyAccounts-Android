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

import android.os.Build
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountPresenterInter
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountFragment
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DisplayAccountAdapterInst {
    lateinit var scenario: FragmentScenario<DisplayAccountFragment>
    lateinit var presenter: DisplayAccountPresenterInter
    private val fileName = Faker().str()

    var recycler: RecyclerView? = null
    var itemLayout: View? = null

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)

        presenter = mock {
            on { attachedFilesList } doReturn listOf(fileName)
        }

        scenario.onFragment {
            it.presenter = presenter
            recycler = it.view?.findViewById(R.id.attachedFilesList)
        }

        // measure and lay recycler out as is needed so we can obtain its items
        recycler?.measure(0, 0)
        recycler?.layout(0, 0, 100, 10000)
        itemLayout = recycler?.getChildAt(0)
    }

    @Test
    fun `should call presenter fileSelected when item is selected`() {
        itemLayout?.performClick()
        verify(presenter).fileSelected(fileName)
    }
}
