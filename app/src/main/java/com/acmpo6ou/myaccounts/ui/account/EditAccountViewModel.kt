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
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

class EditAccountViewModel : CreateAccountViewModel() {
    private var oldAccount: Account? = null

    /**
     * Initializes model with needed resources.
     * @param[app] application instance used to get resources.
     * @param[accounts] accounts map.
     * @param[accountName] name of account being edited.
     */
    fun initialize(app: MyApp, accounts: DbMap, accountName: String) {
        super.initialize(app, accounts)
        oldAccount = accounts[accountName]
    }

    override fun applyPressed(accountName: String, username: String, email: String, password: String, date: String, comment: String) {
        // remove old account and create new one
        accounts.remove(oldAccount?.accountName)
        super.applyPressed(accountName, username, email, password, date, comment)
    }
}