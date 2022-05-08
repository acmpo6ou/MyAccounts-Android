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

package com.acmpo6ou.myaccounts.account.accounts_activity

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.SuperPresenter
import dagger.Lazy
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open class AccountsPresenter @Inject constructor(
    val activity: Lazy<AccountsActivityI>,
    override val app: MyApp,
) : SuperPresenter(), AccountsPresenterI {

    override val view: AccountsActivityI get() = activity.get()
    val database get() = view.database

    /**
     * Called when user clicks `Save` in navigation drawer.
     * Saves database only if it has changed.
     */
    override fun saveSelected() {
        if (!isDatabaseSaved(database)) saveDatabase(database.name, database)
        view.mainFragment.showSuccess()
    }

    /**
     * Called when user presses the back button.
     *
     * Here we decide whether to show a confirmation dialog about unsaved changes or not.
     * If database is already saved – just go back, when it isn't – display
     * confirmation dialog.
     */
    override fun backPressed() {
        if (isDatabaseSaved(database)) {
            view.goBack()
        } else {
            view.confirmBack()
        }
    }
}
