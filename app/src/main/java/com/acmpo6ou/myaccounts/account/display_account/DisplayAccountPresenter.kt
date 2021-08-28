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

package com.acmpo6ou.myaccounts.account.display_account

import android.net.Uri
import dagger.hilt.android.scopes.FragmentScoped
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@FragmentScoped
class DisplayAccountPresenter @Inject constructor(
    private val view: DisplayAccountFragmentI,
    private val model: DisplayAccountModelI,
) : DisplayAccountPresenterI {

    lateinit var selectedFile: String
    private val attachedFiles = view.account.attachedFiles
    override val attachedFilesList get() = attachedFiles.keys.sorted()

    /**
     * Called when user selects attached file from the list.
     *
     * Saves given [fileName] so that later, when saving the file, we could obtain content
     * associated with that file.
     * @param[fileName] name of selected attached file.
     */
    override fun fileSelected(fileName: String) {
        selectedFile = fileName
        view.saveFileDialog(fileName)
    }

    /**
     * Calls model.saveFile() to save selected file handling all errors.
     *
     * If attached file is encoded in invalid Base64 scheme calls view.fileCorrupted()
     * If there are any other errors calls view.showError()
     * @param[destinationUri] uri containing path where to save attached file.
     */
    override fun saveFile(destinationUri: Uri) {
        try {
            model.saveFile(destinationUri, attachedFiles[selectedFile]!!)
            view.showSuccess()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            view.fileCorrupted()
        } catch (e: Exception) {
            e.printStackTrace()
            view.showError(e.toString())
        }
    }

    /**
     * Starts timer to auto remove password from safe clipboard. This is done for security.
     * @param[time] time to wait before removing password, during tests we can specify it as 0
     * to not wait 60 seconds.
     */
    override fun startRemovePassTimer(time: Long) {
        Timer().schedule(time) {
            view.app.password = ""
            cancel()
        }
    }
}
