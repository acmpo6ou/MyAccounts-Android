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

interface MainPresenterInter{
    fun importSelected()
    fun checkUpdatesSelected()

    fun navigateToChangelog()
    fun navigateToSettings()
    fun navigateToAbout()

    fun checkTarFile(location: String)
    fun checkForUpdates(): Boolean
    fun isTimeToUpdate(): Boolean
}

interface MainActivityInter{
    val ACCOUNTS_DIR: String
    fun importDialog()
    fun startUpdatesActivity()

    fun noUpdates()
    fun showError(title: String, details: String)
}

interface MainModelInter{
    fun countFiles(location: String): Int
}