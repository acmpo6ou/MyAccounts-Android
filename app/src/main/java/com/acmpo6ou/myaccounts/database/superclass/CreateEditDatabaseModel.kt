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
import androidx.lifecycle.viewModelScope
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.SecureRandom

/**
 * Super class for all view models that create/edit databases.
 */
abstract class CreateEditDatabaseModel : CreateEditViewModel(), DatabaseViewModel {
    override lateinit var defaultDispatcher: CoroutineDispatcher
    override lateinit var uiDispatcher: CoroutineDispatcher
    override var coroutineJob: Job? = null

    override lateinit var _title: MutableLiveData<String>
    override lateinit var _loading: MutableLiveData<Boolean>
    override lateinit var errorMsg_: MutableLiveData<String>

    override lateinit var app: MyApp
    override lateinit var SRC_DIR: String
    override lateinit var titleStart: String

    override val itemNames get() = app.databases.map { it.name }
    override var databaseIndex: Int = 0

    /**
     * This method generates purely random salt for encryption.
     * @return salt for encryption.
     */
    open fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    /**
     * This method removes all unsupported characters from given database name.
     *
     * Supported characters are lower and upper ASCII letters, digits and .-_()
     * We should clean the name because it is used as a name for database files.
     * @param[name] name to clean.
     * @return cleaned from unsupported characters name.
     */
    override fun fixName(name: String): String {
        val supported =
            (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString("") + ".-_()"
        return name.filter { it in supported }
    }

    /**
     * Called when user presses apply button.
     * Launches [apply] only if it's not already launched.
     */
    open fun applyPressed(name: String, password: String) {
        if (coroutineJob == null || !coroutineJob!!.isActive) {
            coroutineJob = viewModelScope.launch(uiDispatcher) {
                apply(name, password)
            }
        }
    }

    abstract suspend fun apply(name: String, password: String)
}
