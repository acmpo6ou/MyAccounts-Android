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

package com.acmpo6ou.myaccounts.account

import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.database.DbMap

class AccountsListPresenter(val view: AccountsFragmentInter) : AccountsListPresenterInter {
    override val accounts: DbMap = mapOf()

    /**
     * Helper method to get Account from [accounts] by index.
     *
     * The problem is that [accounts] is a map of [String] to [Account], but AccountsAdapter
     * will typically work with indexes rather then Strings. That's why this method exists.
     * It provides a way to get [Account] from [accounts] by given index.
     */
    override fun getAccount(i: Int): Account {
        val name = accounts.keys.sorted()[i] // sort keys alphabetically
        return accounts[name]!!
    }

    override fun displayAccount(i: Int) {
        TODO("Not yet implemented")
    }
}