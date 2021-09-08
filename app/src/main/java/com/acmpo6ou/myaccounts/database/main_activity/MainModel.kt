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

package com.acmpo6ou.myaccounts.database.main_activity

import android.net.Uri
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.SRC_DIR
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@ActivityScoped
class MainModel @Inject constructor(override val app: MyApp) : MainModelI {

    /**
     * Returns size of .dba file user tries to import.
     *
     * @param[locationUri] uri containing tar file.
     */
    override fun getSize(locationUri: Uri): Int {
        val descriptor = app.contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)
        return location.available()
    }

    /**
     * Imports database from given .dba file.
     *
     * Copies given .dba file to src directory.
     * @param[locationUri] uri containing .dba file.
     * @return name of imported database that is later used by presenter.
     */
    override fun importDatabase(locationUri: Uri): String {
        val descriptor = app.contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        val name = File(locationUri.path).nameWithoutExtension
        val file = File("$SRC_DIR/$name.dba")

        if (file.exists())
            throw FileAlreadyExistsException(file)

        location.use {
            file.writeBytes(it.readBytes())
        }

        return name
    }
}
