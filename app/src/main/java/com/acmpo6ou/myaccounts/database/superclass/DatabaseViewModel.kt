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

package com.acmpo6ou.myaccounts.database.superclass

import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils
import com.acmpo6ou.myaccounts.database.databases_list.DbList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Super class for all database view models.
 */
interface DatabaseViewModel : DatabaseUtils {
    var defaultDispatcher: CoroutineDispatcher
    var uiDispatcher: CoroutineDispatcher
    var coroutineJob: Job?

    override var app: MyApp
    var titleStart: String
    var databaseIndex: Int
    var SRC_DIR: String

    var databases: DbList
        get() = app.databases
        set(value) {
            app.databases = value
        }

    var _title: MutableLiveData<String>
    var title: String
        get() = _title.value!!
        set(value) { _title.value = value }

    var _loading: MutableLiveData<Boolean>
    var loading
        get() = _loading.value!!
        set(value) { _loading.value = value }

    var errorMsg_: MutableLiveData<String>
    var errorMsg: String
        get() = errorMsg_.value!!
        set(value) { errorMsg_.value = value }

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app], [SRC_DIR] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[SRC_DIR] path to src directory that contains databases.
     * @param[titleStart] string resource used to construct app bar title.
     * @param[databaseIndex] index of database on which operations are performed.
     * Note not all ViewModels need [databaseIndex] and [titleStart] properties, example is
     * CreateDatabaseViewModel.
     */
    fun initialize(
        app: MyApp,
        SRC_DIR: String,
        titleStart: String? = null,
        databaseIndex: Int? = null
    ) {
        _title = MutableLiveData()
        _loading = MutableLiveData(false)
        errorMsg_ = MutableLiveData()

        defaultDispatcher = Dispatchers.Default
        uiDispatcher = Dispatchers.Main

        this.app = app
        this.SRC_DIR = SRC_DIR

        // set app bar title if databaseIndex and titleStart are passed
        databaseIndex?.let {
            this.titleStart = titleStart!!
            this.databaseIndex = it

            val name = databases[it].name
            title = "$titleStart $name"
        }
    }
}
