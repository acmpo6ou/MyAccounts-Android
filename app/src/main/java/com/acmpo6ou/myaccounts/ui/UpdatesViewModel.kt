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

package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class UpdatesViewModel : ViewModel() {
    val changelog = MutableLiveData<String>()

    /**
     * Downloads latest changelog from github repository.
     */
    private fun getChangelogAsync() = viewModelScope.async(Dispatchers.Default) {
        URL(
            "https://raw.githubusercontent.com/Acmpo6ou/MyAccounts/" +
                "master/app/src/main/res/raw/changelog"
        )
            .openStream()
            .use {
                return@async String(it.readBytes())
            }
    }

    /**
     * Launches and awaits [getChangelogAsync] coroutine.
     */
    fun getChangelog() = viewModelScope.launch(Dispatchers.Main) {
        changelog.value = getChangelogAsync().await()
    }
}
