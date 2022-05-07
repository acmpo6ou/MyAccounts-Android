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

package com.acmpo6ou.myaccounts.create_edit_account

import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.*
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsModule
import com.acmpo6ou.myaccounts.account.create_edit_account.EditAccountFragment
import com.acmpo6ou.myaccounts.core.AppModule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Database
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
class EditAccountFragmentInst {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var fragment: EditAccountFragment
    private val b get() = fragment.b

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val db = Database("main", data = databaseMap.copy())

    @BindValue
    @JvmField
    val app: MyApp = mock { on { res } doReturn context.resources }

    @BindValue
    @JvmField
    @FragmentScoped
    val accountsActivityI: AccountsActivityI = mock { on { database } doReturn db }

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        val bundle = Bundle()
        bundle.putString("accountName", account.accountName)
        fragment = launchFragmentInHiltContainer(bundle)
    }

    @Test
    fun `initForm should fill all fields`() {
        assertEquals(account.accountName, b.accountName.text.toString())
        assertEquals(account.username, b.accountUsername.text.toString())
        assertEquals(account.email, b.accountEmail.text.toString())
        assertEquals(account.password, b.accountPassword.text.toString())
        assertEquals(account.password, b.accountRepeatPassword.text.toString())
        assertEquals(account.date, b.birthDate.text)
        assertEquals(account.comment, b.accountComment.text.toString())
    }

    @Test
    fun `initForm should change text of apply button`() {
        val saveText = context.resources.getString(R.string.save)
        assertEquals(saveText, b.applyButton.text)
    }
}
