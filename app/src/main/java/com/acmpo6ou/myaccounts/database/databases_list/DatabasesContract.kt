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

package com.acmpo6ou.myaccounts.database.databases_list

import android.net.Uri
import com.acmpo6ou.myaccounts.core.superclass.ListFragmentI
import com.acmpo6ou.myaccounts.core.superclass.ListPresenter
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils

interface DatabasesPresenterI : ListPresenter {
    val databases: DbList

    fun exportSelected(database: Database)
    fun exportDatabase(locationUri: Uri)

    fun deleteSelected(database: Database)
    fun deleteDatabase(database: Database)

    fun closeSelected(database: Database)
    fun closeDatabase(database: Database)

    fun editSelected(database: Database)
    fun openDatabase(database: Database)
}

interface DatabasesFragmentI : ListFragmentI {
    val presenter: DatabasesPresenterI
    val adapter: DatabasesAdapter

    fun exportDialog(database: Database)
    fun showError(title: String, details: String)
    fun startDatabase(index: Int)

    fun confirmDelete(database: Database)
    fun confirmClose(database: Database)

    fun navigateToOpen(index: Int)
    fun navigateToEdit(index: Int)
    fun navigateToRename(index: Int)
}

interface DatabasesModelI : DatabaseUtils {
    fun getDatabases(): DbList
    fun exportDatabase(name: String, destinationUri: Uri)
}
