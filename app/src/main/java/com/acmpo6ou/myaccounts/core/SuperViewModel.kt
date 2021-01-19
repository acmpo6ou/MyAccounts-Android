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
    val _title = MutableLiveData<String>()
    var title: String
        get() = _title.value!!
        set(value) {_title.value = value}

    val errorMsg_ = MutableLiveData<String>()
    var errorMsg: String
        get() = errorMsg_.value!!
        set(value) {errorMsg_.value = value}

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app], [SRC_DIR] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[SRC_DIR] path to src directory that contains databases.
     * @param[titleStart] string resource used to construct app bar title.
     * @param[databaseIndex] index of database on which operations are performed.
     * Note not all ViewModels need [databaseIndex] property, example is
     * CreateDatabaseViewModel.
     */
    open fun initialize(app: MyApp, titleStart: String, SRC_DIR: String,
                        databaseIndex: Int? = null) {
        this.app = app
        this.SRC_DIR = SRC_DIR
        this.titleStart = titleStart

        // set app bar title if databaseIndex is passed
        databaseIndex?.let {
            this.databaseIndex = it
            val name = databases[it].name
            title = "$titleStart $name"
        }
    }
}