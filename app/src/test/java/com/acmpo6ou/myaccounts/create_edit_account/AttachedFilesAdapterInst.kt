/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.create_edit_account

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsModule
import com.acmpo6ou.myaccounts.account.create_edit_account.CreateAccountFragment
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@HiltAndroidTest
@UninstallModules(AppModule::class, AccountsModule::class)
@RunWith(RobolectricTestRunner::class)
class AttachedFilesAdapterInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    lateinit var fragment: CreateAccountFragment
    val db = Database("main", data = mutableMapOf())

    @BindValue
    @JvmField
    val app: MyApp = mock { on { res } doReturn context.resources }

    @BindValue
    @JvmField
    @FragmentScoped
    val accountsActivityI: AccountsActivityI = mock { on { database } doReturn db }

    private val fileName = Faker().str()
    private val fileName2 = Faker().str()

    private lateinit var recycler: RecyclerView
    private lateinit var itemLayout: View
    private lateinit var itemLayout2: View

    @Before
    fun setUp() {
        context.setTheme(R.style.Theme_MyAccounts_NoActionBar)
        hiltAndroidRule.inject()
        fragment = launchFragmentInHiltContainer()

        // add attached files
        fragment.viewModel.apply {
            filePaths[fileName] = null
            filePaths[fileName2] = null
        }

        fragment.initAdapter()
        recycler = fragment.getRecycler(R.id.attachedFilesList)

        itemLayout = recycler.getChildAt(0)
        itemLayout2 = recycler.getChildAt(1)
    }

    @Test
    fun `attached file item should have appropriate name`() {
        val name = itemLayout.findViewById<TextView>(R.id.itemName)
        assertEquals(fileName, name.text)
    }

    @Test
    fun `clicking on 'Remove' should remove attached file from the list`() {
        clickMenuItem(itemLayout, R.id.remove_attached_file)

        recycler = fragment.getRecycler(R.id.attachedFilesList)
        itemLayout = recycler.getChildAt(0)

        // now first attached file should be fileName2
        val name = itemLayout.findViewById<TextView>(R.id.itemName)
        assertEquals(fileName2, name.text)
    }
}
