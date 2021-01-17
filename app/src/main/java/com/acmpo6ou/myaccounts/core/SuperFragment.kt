/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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
import androidx.fragment.app.Fragment
import com.acmpo6ou.myaccounts.AccountsActivity

open class SuperFragment: Fragment() {
    /**
     * Used to start AccountsActivity for given database.
     * @param[index] index of database for which we want to start AccountsActivity.
     */
    open fun startDatabase(index: Int) {
        val intent = Intent(context, AccountsActivity::class.java)
        intent.putExtra("databaseIndex", index)
        startActivity(intent)
    }
}