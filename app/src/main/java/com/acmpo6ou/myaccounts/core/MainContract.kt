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

package com.acmpo6ou.myaccounts.core

import android.content.Context
import android.net.Uri
import com.acmpo6ou.myaccounts.MyApp

interface MainPresenterInter{
    fun importSelected()
    fun checkUpdatesSelected()

    fun navigateToChangelog()
    fun navigateToSettings()
    fun navigateToAbout()

    fun checkTarFile(location: Uri)
    fun checkForUpdates(): Boolean
    fun isTimeToUpdate(): Boolean
}

interface MainActivityInter{
    val ACCOUNTS_DIR: String

    val myContext: Context
    var app: MyApp

    fun importDialog()
    fun startUpdatesActivity()

    fun noUpdates()
    fun showError(title: String, details: String)

    fun navigateTo(id: Int)
}

interface MainModelInter{
    fun countFiles(location: Uri): Int
    fun getNames(locationUri: Uri): MutableList<String>
    fun getSizes(locationUri: Uri): MutableList<Int>
    fun importDatabase(locationUri: Uri): String
}