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

package com.acmpo6ou.myaccounts.database

import android.net.Uri
import com.acmpo6ou.myaccounts.core.DatabaseUtils
import com.acmpo6ou.myaccounts.core.superclass.SuperActivityInter
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenterInter

interface MainPresenterInter : SuperPresenterInter {
    fun importSelected()
    fun checkTarFile(location: Uri)
    fun isTimeToUpdate(): Boolean
}

interface MainActivityInter : SuperActivityInter {
    fun importDialog()
    fun showExitTip()
    fun notifyChanged(i: Int)
}

interface MainModelInter : DatabaseUtils {
    fun countFiles(location: Uri): Int
    fun getNames(locationUri: Uri): List<String>
    fun getSizes(locationUri: Uri): List<Int>
    fun importDatabase(locationUri: Uri): String
}