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

import android.content.Intent
import com.acmpo6ou.myaccounts.ui.DatabasesAdapter

interface DatabasesPresenterInter{
    var databases: List<Database>
//    var exportIndex: Int

    fun exportDatabase(location: String)
    fun deleteDatabase(name: String)
//    fun closeDatabase(i: Int)
//    fun openDatabase(i: Int)
}

interface DatabaseFragmentInter{
    val presenter: DatabasesPresenterInter
    val adapter: DatabasesAdapter

    fun exportDialog(name: String)
    fun confirmDelete(name: String)
    fun showSuccess()
    fun showError(details: String)
    fun startDatabase(databaseJson: String)
    fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?)
}
