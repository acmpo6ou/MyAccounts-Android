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

package com.acmpo6ou.myaccounts.account.display_account

import android.content.Context
import android.net.Uri
import com.acmpo6ou.myaccounts.database.databases_list.Account

interface DisplayAccountFragmentI {
    val myContext: Context
    val account: Account
    val presenter: DisplayAccountPresenterI

    fun saveFileDialog(fileName: String)
    fun fileCorrupted()
    fun showError(details: String)
    fun showSuccess()
}

interface DisplayAccountPresenterI {
    val attachedFilesList: List<String>

    fun fileSelected(fileName: String)
    fun saveFile(destinationUri: Uri)
}

interface DisplayAccountModelI {
    fun saveFile(destinationUri: Uri, content: String)
}
