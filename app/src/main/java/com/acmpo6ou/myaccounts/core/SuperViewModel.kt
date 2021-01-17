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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp

open class SuperViewModel: ViewModel() {
    var databaseIndex: Int = 0
    lateinit var app: MyApp
    lateinit var SRC_DIR: String
    lateinit var titleStart: String

    var databases: MutableList<Database>
        get() = app.databases
        set(value) {
            app.databases = value
        }

    // app bar title
    val title = MutableLiveData<String>()
    fun getTitle() = title.value!!

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app], [SRC_DIR] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[databaseIndex] index of database that we want to open.
     * @param[SRC_DIR] path to src directory that contains databases.
     * @param[titleStart] string resource used to construct app bar title.
     */
    open fun initialize(app: MyApp, databaseIndex: Int, SRC_DIR: String, titleStart: String) {
        this.app = app
        this.SRC_DIR = SRC_DIR
        this.titleStart = titleStart
        this.databaseIndex = databaseIndex

        // set app bar title
        val name = databases[databaseIndex].name
        title.value = "$titleStart $name"
    }
}