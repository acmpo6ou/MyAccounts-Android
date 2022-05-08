/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

@FragmentScoped
class DisplayAccountModel @Inject constructor(
    @ActivityContext private val context: Context,
) : DisplayAccountModelI {
    /**
     * Decodes given base64 [content] string and writes it to a file.
     *
     * @param[destinationUri] uri containing file path.
     * @param[content] base64 encoded string, that is a content of the file.
     */
    override fun saveFile(destinationUri: Uri, content: String) {
        val descriptor = context.contentResolver.openFileDescriptor(destinationUri, "w")
        val destination = FileOutputStream(descriptor?.fileDescriptor)

        val data = Base64.getDecoder().decode(content.toByteArray())
        destination.write(data)
    }
}
