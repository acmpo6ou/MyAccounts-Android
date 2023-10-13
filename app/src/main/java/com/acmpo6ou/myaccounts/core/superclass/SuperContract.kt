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

package com.acmpo6ou.myaccounts.core.superclass

import android.content.Context
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.utils.SettingsUtils
import com.google.android.material.navigation.NavigationView

interface SuperActivityI :
    NavigationView.OnNavigationItemSelectedListener,
    SettingsUtils {

    val mainFragment: ListFragmentI
    var myContext: Context
    var app: MyApp

    fun showError(title: String, details: String)
    fun goBack()
    fun confirmBack()
}

interface SuperPresenterI {
    fun backPressed()
    fun saveSelected()
}
