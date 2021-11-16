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
import com.acmpo6ou.myaccounts.core.superclass.CreateEditViewModel
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.SecureRandom

/**
 * Super class for all view models that create/edit databases.
 */
abstract class CreateEditDatabaseModel : CreateEditViewModel(), DbNameModel, DatabaseUtils {
    var coroutineJob: Job? = null
    abstract val uiDispatcher: CoroutineDispatcher

    val loading = MutableLiveData<Boolean>()
    override val errorMsg = MutableLiveData<String>()
    override val itemNames get() = app.databases.map { it.name }

    /**
     * Generates purely random salt for encryption.
     */
    open fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    override fun fixName(name: String) = super<DbNameModel>.fixName(name)

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
