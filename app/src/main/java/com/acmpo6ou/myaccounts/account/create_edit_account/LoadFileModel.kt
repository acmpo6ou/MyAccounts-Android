/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.account.create_edit_account

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LoadFileModel @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /**
     * Loads file from given [locationUri] and encodes it in Base64 format.
     *
     * @param[locationUri] uri containing path to file.
     * @return Base64 encoded file content.
     */
    open fun loadFile(locationUri: Uri): String {
        val descriptor = context.contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        val data = location.readBytes()
        return Base64.getUrlEncoder().encodeToString(data)
    }
}
