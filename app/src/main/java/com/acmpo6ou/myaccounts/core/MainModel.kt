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
 * @param[SRC_DIR] path to directory that contains databases.
 * Default is /storage/emulated/0/MyAccounts/src/
 */
class MainModel(private val SRC_DIR: String): MainModelInter {
    /**
     * This method counts number of files that present in given tar file.
     *
     * @param[location] path to tar file.
     * @return number of counted files in tar file.
     */
    override fun countFiles(location: String): Int {
        return 0
    }

    /**
     * Used to import database from given tar archive.
     *
     * Extracts .db and .bin files from given tar archive to `src` directory.
     * @param[tarFile] path to tar archive that contains database files we need to extract.
     */
    fun importDatabase(tarFile: String) {
        // destination is actually should be the `src` folder but because of the way files
        // are stored in tar file we extract them in the parent directory of `src`
        // for more details see DatabasesModel.exportDatabase() documentation
        val destFolder = "$SRC_DIR/../"

        // open tar file
        val inputStream = TarInputStream(
                BufferedInputStream(FileInputStream(tarFile))
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
            val outStream = FileOutputStream("$destFolder${entry.name}")
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