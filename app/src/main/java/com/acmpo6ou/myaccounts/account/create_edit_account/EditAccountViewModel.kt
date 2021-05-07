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

package com.acmpo6ou.myaccounts.account.create_edit_account

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.database.databases_list.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    app: MyApp,
    model: LoadFileModel,
) : CreateAccountViewModel(app, model) {
    private lateinit var oldAccount: Account

    /**
     * Initializes model with needed resources.
     * @param[accountName] name of account being edited.
     */
    fun initialize(accountName: String) {
        oldAccount = accounts[accountName]!!

        // fill filePaths with existing attached files
        oldAccount.attachedFiles.keys.forEach {
            filePaths[it] = null
        }
    }

    override fun applyPressed(
        accountName: String,
        username: String,
        email: String,
        password: String,
        date: String,
        comment: String
    ) {
        try {
            val attachedFiles = mutableMapOf<String, String>()
            for ((fileName, uri) in filePaths) {
                if (uri != null) {
                    // load all attached files
                    val content = model.loadFile(uri)
                    attachedFiles[fileName] = content
                } else {
                    // and add existing ones
                    attachedFiles[fileName] = oldAccount.attachedFiles[fileName]!!
                }
            }

            // remove old account and create new one
            accounts.remove(oldAccount.accountName)
            accounts[accountName] = Account(
                accountName, username, email, password, date, comment,
                oldAccount.copyEmail, attachedFiles
            )

            finished.value = true // notify about successful edition
        } catch (e: Exception) {
            e.printStackTrace()
            errorMsg.value = e.toString()
        }
    }

    /**
     * Validates given name, checks whether it's not empty and whether account
     * with such name already exists, but it's okay if name doesn't change through editing.
     *
     * If name is empty [emptyNameErr] is set to true.
     * If account with such name already exists [existsNameErr] is set to true.
     * @param[name] name to validate.
     */
    override fun validateName(name: String) {
        val oldName = oldAccount.accountName

        // it's okay if name didn't change through editing
        if (oldName == name) {
            existsNameErr.value = false
            emptyNameErr.value = false
        } else {
            super.validateName(name)
        }
    }
}
