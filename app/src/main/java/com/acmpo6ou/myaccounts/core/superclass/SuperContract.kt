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

package com.acmpo6ou.myaccounts.core.superclass

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.utils.NetUtils
import com.acmpo6ou.myaccounts.core.utils.SettingsUtils
import com.google.android.material.navigation.NavigationView

interface SuperActivityInter :
    NavigationView.OnNavigationItemSelectedListener,
    SettingsUtils,
    NetUtils {
    val mainFragment: ListFragmentInter
    val ACCOUNTS_DIR: String
    var app: MyApp

    fun startUpdatesActivity(version: String)
    fun noUpdates(isAutoCheck: Boolean = false)
    fun updatesCheckFailed(isAutoCheck: Boolean = false)
    fun noInternetConnection(isAutoCheck: Boolean = false)

    fun navigateTo(id: Int)
    fun showError(title: String, details: String)

    fun goBack()
    fun confirmBack()
}

interface SuperPresenterInter {
    fun backPressed()
    fun saveSelected()
    fun checkUpdatesSelected(isAutoCheck: Boolean = false)

    fun navigateToChangelog()
    fun navigateToSettings()
    fun navigateToAbout()
}
