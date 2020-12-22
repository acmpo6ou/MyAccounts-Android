/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.core

import android.content.ContentResolver
import android.net.Uri
import org.kamranzafar.jtar.TarEntry
import org.kamranzafar.jtar.TarInputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Contains various methods for working with database tar files. Methods for importing
 * database, counting files in database tar file etc.
 *
 * @param[ACCOUNTS_DIR] path to directory that contains src folder.
 * Default is /storage/emulated/0/MyAccounts/
 */
class MainModel(private val ACCOUNTS_DIR: String,
                private val contentResolver: ContentResolver): MainModelInter {
    /**
     * This method counts number of files that present in given tar file.
     *
     * @param[location] path to tar file.
     * @return number of counted files in tar file.
     */
    override fun countFiles(location: Uri): Int {
        return getNames(location).size
    }

    /**
     * This method returns a list of names of files from tar file.
     *
     * @param[locationUri] path to tar file.
     * @return list of file names from tar file.
     */
    override fun getNames(locationUri: Uri): MutableList<String> {
        val list = mutableListOf<String>()
        val descriptor = contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        // open tar file
        val inputStream = TarInputStream(
                BufferedInputStream(location)
        )

        // get first file from tar
        var entry: TarEntry? = inputStream.nextEntry

        while (entry != null){
            var name = entry.name
            // skip all other files such as tar headers
            if (
                name.startsWith("src/") &&
                (name.endsWith(".db") || name.endsWith(".bin"))
            ) {
                // this needs to be removed from file name
                val srcRe = Regex("^src/")
                val binRe = Regex("\\.db$")
                val dbRe = Regex("\\.bin$")

                // clean file name from extension and `src` folder
                name = srcRe.replace(name, "")
                name = binRe.replace(name, "")
                name = dbRe.replace(name, "")
                list.add(name)
            }
            entry = inputStream.nextEntry
        }
        inputStream.close()
        return list
    }

    /**
     * This method returns file sizes of given tar file.
     *
     * @param[locationUri] path to tar file.
     * @return list of file sizes.
     */
    override fun getSizes(locationUri: Uri): MutableList<Int> {
        val list = mutableListOf<Int>()
        val descriptor = contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        // open tar file
        val inputStream = TarInputStream(
                BufferedInputStream(location)
        )

        // get first file from tar
        var entry: TarEntry? = inputStream.nextEntry

        while (entry != null) {
            var name = entry.name
            // skip all other files such as tar headers
            if (
                    name.startsWith("src/") &&
                    (name.endsWith(".db") || name.endsWith(".bin"))
            ) {
                list.add(entry.size.toInt())
            }
            entry = inputStream.nextEntry
        }
        inputStream.close()
        return list
    }

    /**
     * Used to import database from given tar archive.
     *
     * Extracts .db and .bin files from given tar archive to `src` directory.
     * @param[locationUri] path to tar archive that contains database files we need to extract.
     */
    override fun importDatabase(locationUri: Uri) {
        val descriptor = contentResolver.openFileDescriptor(locationUri, "r")
        val location = FileInputStream(descriptor?.fileDescriptor)

        // open tar file
        val inputStream = TarInputStream(
                BufferedInputStream(location)
        )

        // get first file from tar
        var entry: TarEntry? = inputStream.nextEntry

        // extract database files from tar archive
        while (entry != null) {
            // extract only .db and .bin files, skip all other such as tar headers
            if(!(
                entry.name.endsWith(".db") ||
                entry.name.endsWith(".bin")
            )){
                entry = inputStream.nextEntry
                continue
            }

            // create file we want to extract
            val outStream = FileOutputStream("$ACCOUNTS_DIR${entry.name}")
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
    }
}