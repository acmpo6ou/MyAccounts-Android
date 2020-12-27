/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp

class OpenDatabaseViewModel : ViewModel() {
    private var databaseIndex: Int = 0
    lateinit var app: MyApp

    private val title = MutableLiveData<String>()

    fun getTitle() = title

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[databaseIndex] index of database that we want to open.
     */
    fun setDatabase(app: MyApp, databaseIndex: Int) {
        this.app = app
        this.databaseIndex = databaseIndex

        val name = app.databases[databaseIndex].name
        title.value = "Open $name"
    }
}