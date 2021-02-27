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

import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

class CreateAccountViewModel : CreateEditViewModel() {
    override lateinit var app: MyApp
    var databaseIndex = 999

    val accounts: DbMap get() = app.databases[databaseIndex].data
    override val itemNames get() = accounts.values.toList().map { it.accountName }

    /**
     * Initializes model with needed resources.
     * @param[app] application instance used to get database that contains accounts map.
     * @param[databaseIndex] index of database that contains accounts map.
     */
    fun initialize(app: MyApp, databaseIndex: Int){
        this.app = app
        this.databaseIndex = databaseIndex
    }

    /**
     * Called when user presses apply button.
     * Creates new account using information provided.
     */
    fun applyPressed(accountName: String, username: String, email: String,
                     password: String, date: String, comment: String){
        accounts[accountName] = Account(accountName, username, email, password, date, comment)
        finished = true // notify about creation
    }
}