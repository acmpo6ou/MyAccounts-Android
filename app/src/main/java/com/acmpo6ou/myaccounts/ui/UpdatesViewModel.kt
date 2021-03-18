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
import java.io.File
import java.net.URL

class UpdatesViewModel : ViewModel() {
    var DOWNLOAD_DIR = "/storage/emulated/0/Download/"
    val changelog = MutableLiveData<String>()

    /**
     * Removes old myaccounts-release.apk file from Download directory.
     * We need to do this before downloading new update apk file.
     */
    fun removeOldApk() {
        val oldApk = File("$DOWNLOAD_DIR/myaccounts-release.apk")
        if (oldApk.exists()) oldApk.delete()
    }

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
     * Launches and awaits [getChangelogAsync] coroutine handling any errors.
     *
     * In case of errors, message saying `Failed to load changelog` with additional
     * details about the error, will appear.
     */
    fun getChangelog(errorMsg: String) = viewModelScope.launch(Dispatchers.Main) {
        var text = "<h6>$errorMsg</h6>"
        try {
            text = getChangelogAsync().await()
        } catch (e: Exception) {
            e.printStackTrace()
            text += e.toString()
        }
        changelog.value = text
    }
}
