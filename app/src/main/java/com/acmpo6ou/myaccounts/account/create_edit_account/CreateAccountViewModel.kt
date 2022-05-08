/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.account.create_edit_account

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.database.databases_list.DbMap
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class CreateAccountViewModel @Inject constructor(
    override val app: MyApp,
    val model: LoadFileModel,
) : CreateEditViewModel() {

    lateinit var accounts: DbMap
    override val itemNames get() = accounts.values.toList().map { it.accountName }

    open val filePaths = mutableMapOf<String, Uri?>()
    open val attachedFilesList get() = filePaths.keys.toList()

    val notifyAdded = MutableLiveData<Int>()
    val notifyRemoved = MutableLiveData<Int>()
    override var errorMsg = MutableLiveData<String>()

    /**
     * Adds given [locationUri] to [filePaths] and notifies about addition.
     *
     * @param[locationUri] uri containing path to file to be attached.
     * @param[fileName] name of the file to be attached.
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
     * Handles any errors when loading attached files.
     */
    open fun applyPressed(
        accountName: String,
        username: String,
        email: String,
        password: String,
        date: String,
        comment: String,
    ) {
        val attachedFiles = mutableMapOf<String, String>()
        try {
            // load all attached files
            for ((fileName, uri) in filePaths) {
                val content = model.loadFile(uri!!)
                attachedFiles[fileName] = content
            }

            accounts[accountName] = Account(
                accountName, username, email, password, date, comment,
                true, attachedFiles
            )
            finished.value = true // notify about creation
        } catch (e: Exception) {
            e.printStackTrace()
            errorMsg.value = e.toString()
        }
    }
}
