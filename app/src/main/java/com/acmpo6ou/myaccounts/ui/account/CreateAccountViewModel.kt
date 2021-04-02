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

package com.acmpo6ou.myaccounts.ui.account

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.account.LoadFileModel
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

open class CreateAccountViewModel : CreateEditViewModel() {
    override lateinit var app: MyApp
    lateinit var model: LoadFileModel
    lateinit var accounts: DbMap

    override val itemNames get() = accounts.values.toList().map { it.accountName }
    val filePaths = mutableMapOf<String, Uri>()
    open val attachedFilesList get() = filePaths.keys.toList()

    val notifyAdded = MutableLiveData<Int>()
    val notifyRemoved = MutableLiveData<Int>()

    /**
     * Initializes model with needed resources.
     * @param[app] application instance used to get resources.
     * @param[accounts] accounts map.
     */
    open fun initialize(app: MyApp, accounts: DbMap) {
        this.app = app
        this.accounts = accounts
        model = LoadFileModel(app.contentResolver)
    }

    /**
     * Adds given [locationUri] to [filePaths] and notifies about addition.
     *
     * @param[locationUri] uri containing path to file that needs to be attached.
     * @param[fileName] name of the file that needs to be attached.
     */
    open fun addFile(locationUri: Uri, fileName: String) {
        filePaths[fileName] = locationUri
        val i = attachedFilesList.indexOf(fileName)
        notifyAdded.value = i
    }

    /**
     * Removes uri of attached file from [filePaths] and notifies about removal.
     * @param[position] index of name of the file in [attachedFilesList].
     */
    open fun removeFile(position: Int) {
        val fileName = attachedFilesList[position]
        filePaths.remove(fileName)
        notifyRemoved.value = position
    }

    /**
     * Called when user presses apply button.
     * Creates new account using information provided.
     */
    open fun applyPressed(
        accountName: String,
        username: String,
        email: String,
        password: String,
        date: String,
        comment: String
    ) {
        accounts[accountName] = Account(accountName, username, email, password, date, comment)
        finished = true // notify about creation
    }
}
