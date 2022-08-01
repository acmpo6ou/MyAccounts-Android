/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsModule
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountBindings
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountFragment
import com.acmpo6ou.myaccounts.account.display_account.DisplayAccountPresenterI
import com.acmpo6ou.myaccounts.getRecycler
import com.acmpo6ou.myaccounts.launchFragmentInHiltContainer
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

@HiltAndroidTest
@UninstallModules(DisplayAccountBindings::class, AccountsModule::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DisplayAccountAdapterInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @BindValue
    @JvmField
    @FragmentScoped
    val activity: AccountsActivityI = mock()

    private val fileName = Faker().str()
    private lateinit var recycler: RecyclerView
    private lateinit var itemLayout: View

    @BindValue
    @JvmField
    @FragmentScoped
    val presenter: DisplayAccountPresenterI = mock {
        on { attachedFilesList } doReturn listOf(fileName)
    }

    @Before
    fun setUp() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        hiltAndroidRule.inject()
        launchFragmentInHiltContainer<DisplayAccountFragment> {
            recycler = this.getRecycler(R.id.attachedFilesList)
        }
        itemLayout = recycler.getChildAt(0)
    }

    @Test
    fun `should call presenter fileSelected when item is selected`() {
        itemLayout.performClick()
        verify(presenter).fileSelected(fileName)
    }
}
