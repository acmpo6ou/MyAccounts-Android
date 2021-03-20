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

import android.app.DownloadManager
import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

open class UpdatesViewModel : ViewModel() {
    var DOWNLOAD_DIR_FULL = "/storage/emulated/0/Download/"
    private val apkName = "myaccounts-release.apk"
    val changelog = MutableLiveData<String>()

    /**
     * Removes old myaccounts-release.apk file from Download directory.
     *
     * We need to do this before downloading new update apk file to avoid piling up of
     * update apk files in the Download directory.
     */
    open fun removeOldApk() = File("$DOWNLOAD_DIR_FULL/$apkName").delete()

    /**
     * Using DownloadManager downloads latest apk file from github repository.
     *
     * @param[updateVersion] latest app version available on github repository.
     * @param[manager] DownloadManager instance used to download the apk file.
     * @param[downloadDir] path to Download directory where to download the apk file.
     */
    open fun downloadUpdate(updateVersion: String, manager: DownloadManager, downloadDir: String) {
        val uri = Uri.parse(
            "https://github.com/Acmpo6ou/MyAccounts/releases/download/" +
                "v$updateVersion/$apkName"
        )
        manager.enqueue(
            DownloadManager.Request(uri)
                .setAllowedNetworkTypes(NETWORK_WIFI or NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("MyAccounts")
                .setDescription("Downloading update.")
                .setDestinationInExternalPublicDir(downloadDir, apkName)
        )
    }

    /**
     * Launches intent to install the update apk file.
     */
    fun installUpdate(context: Context) {
        val uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider",
            File(DOWNLOAD_DIR_FULL + apkName)
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * Downloads latest changelog from github repository.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
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
