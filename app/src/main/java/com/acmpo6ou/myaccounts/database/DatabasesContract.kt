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

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.acmpo6ou.myaccounts.core.DatabaseUtils
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.ListFragmentInter
import com.acmpo6ou.myaccounts.core.superclass.ListPresenter
import com.acmpo6ou.myaccounts.ui.database.DatabasesAdapter

interface DatabasesPresenterInter : DatabaseUtils, ListPresenter{
    var databases: DbList

    fun exportSelected(i: Int)
    fun exportDatabase(locationUri: Uri)

    fun deleteSelected(i: Int)
    fun deleteDatabase(i: Int)

    fun closeSelected(i: Int)
    fun closeDatabase(i: Int)

    fun editSelected(i: Int)
    fun openDatabase(i: Int)
}

interface DatabaseFragmentInter : ListFragmentInter{
    val presenter: DatabasesPresenterInter
    val adapter: DatabasesAdapter

    val ACCOUNTS_DIR: String
    var myContext: Context
    var app: MyApp

    fun exportDialog(i: Int)

    fun confirmDelete(i: Int)
    fun confirmClose(i: Int)

    fun navigateToEdit(i: Int)
    fun navigateToOpen(i: Int)

    fun showSuccess()
    fun showError(title: String, details: String)

    fun startDatabase(index: Int)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface DatabasesModelInter : DatabaseUtils{
    fun getDatabases(): DbList
    fun exportDatabase(name: String, destinationUri: Uri)
}
