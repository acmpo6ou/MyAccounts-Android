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
import dagger.hilt.android.scopes.ActivityScoped
import org.kamranzafar.jtar.TarEntry
import org.kamranzafar.jtar.TarInputStream
import java.io.*
import javax.inject.Inject

@ActivityScoped
class MainModel @Inject constructor(override val app: MyApp) : MainModelI {

    /**
     * Cleans database name from .db or .bin extension and `src/` path.
     *
     * @param[databaseName] database name to clean.
     * @return database name cleaned from `.db` or `.bin` and `src/`.
     */
    private fun cleanName(databaseName: String): String {
        // this needs to be removed from the name
        val srcRe = Regex("^src/")
        val dbRe = Regex("\\.db$")
        val binRe = Regex("\\.bin$")

        // clean file name from extension and `src` folder
        var name = srcRe.replace(databaseName, "")
        name = dbRe.replace(name, "")
        name = binRe.replace(name, "")
        return name
    }

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
     * Imports database from given tar archive.
     *
     * Extracts .db and .bin files from given tar archive to src directory.
     * @param[locationUri] uri containing tar archive that in turn contains database files
     * we need to extract.
     * @return name of imported database that is later used by presenter.
     */
    override fun importDatabase(locationUri: Uri): String {
        // get tar file
        val descriptor = app.contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        // open tar file
        val inputStream = TarInputStream(BufferedInputStream(location))

        // get first file from tar
        var entry: TarEntry? = inputStream.nextEntry
        var name = ""

        // extract database files from tar archive
        while (entry != null) {
            // extract only .db and .bin files, skip all other such as tar headers
            if (!(
                entry.name.endsWith(".db") || entry.name.endsWith(".bin")
                )
            ) {
                entry = inputStream.nextEntry
                continue
            }

            // check if the file with such name already exist
            val file = File("${app.ACCOUNTS_DIR}${entry.name}")
            if (file.exists()) throw FileAlreadyExistsException(file)

            name = cleanName(entry.name)

            // create file we want to extract
            val outStream = FileOutputStream("${app.ACCOUNTS_DIR}${entry.name}")
            val dest = BufferedOutputStream(outStream)

            // write data into previously created file
            val size = entry.size.toInt()
            val data = ByteArray(size)
            inputStream.read(data)
            dest.write(data)

            // flush buffers and proceed to next file
            dest.flush()
            dest.close()
            entry = inputStream.nextEntry
        }
        inputStream.close()
        return name
    }
}
