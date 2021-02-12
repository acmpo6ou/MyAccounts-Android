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

import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import com.acmpo6ou.myaccounts.database.Database

open class AccountsPresenter(override val view: AccountsActivityInter)
    : SuperPresenter(), AccountsPresenterInter {

    override val SRC_DIR = view.myContext.getExternalFilesDir(null)?.path + "/src"
    val database: Database get() = view.database
    val app: MyApp get() = view.app

    /**
     * Called when user clicks `Save` in navigation drawer.
     * Saves database only if it has changed.
     */
    override fun saveSelected() {
        if (!isDatabaseSaved(database, app)) saveDatabase(database.name, database, app)
    }
}