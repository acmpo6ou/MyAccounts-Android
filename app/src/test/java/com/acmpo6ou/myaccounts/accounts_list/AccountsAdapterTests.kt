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

package com.acmpo6ou.myaccounts.accounts_list

import android.content.res.AssetManager
import com.acmpo6ou.myaccounts.account.accounts_list.AccountsAdapter
import com.caverock.androidsvg.SVGImageView
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class AccountsAdapterTests {
    private lateinit var adapter: AccountsAdapter
    private lateinit var mockImage: SVGImageView

    private val mockAssets: AssetManager =
        mock {
            on { list("") } doReturn arrayOf("gmail.svg", "git.svg", "gitrepo.svg") }

    @Before
    fun setup() {
        adapter = AccountsAdapter(
            mock(),
            mock { on { assets } doReturn mockAssets },
        )
        mockImage = mock()
    }

    @Test
    fun `loadAccountIcon should load appropriate account icon`() {
        adapter.loadAccountIcon(mockImage, "gmail")
        verify(mockImage).setImageAsset("gmail.svg")
    }

    @Test
    fun `loadAccountIcon should load appropriate icon when there is no exact match`() {
        // `gmail2` and `_gmail` aren't exact matches of `gmail` but they should still work
        adapter.loadAccountIcon(mockImage, "gmail2")
        adapter.loadAccountIcon(mockImage, "_gmail")
        verify(mockImage, times(2)).setImageAsset("gmail.svg")
    }

    @Test
    fun `loadAccountIcon should load icon with the best score`() {
        // `git` goes before `gitrepo` in assets, but `gitrepo` will have a better score
        adapter.loadAccountIcon(mockImage, "gitrepo")
        verify(mockImage).setImageAsset("gitrepo.svg")
    }

    @Test
    fun `loadAccountIcon should NOT call loadAccountIcon if there is no match`() {
        // because we have a default icon
        adapter.loadAccountIcon(mockImage, "there will be no match")
        verify(mockImage, never()).setImageAsset(anyString())
    }
}