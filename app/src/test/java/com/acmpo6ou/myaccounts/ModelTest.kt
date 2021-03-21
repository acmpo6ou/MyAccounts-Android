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

package com.acmpo6ou.myaccounts

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.acmpo6ou.myaccounts.core.DatabaseUtils
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

open class ModelTest : DatabaseUtils {
    val faker = Faker()

    open val password = "123"
    var salt = "0123456789abcdef".toByteArray() // 16 bytes of salt
    val jsonDatabase =
        "{\"gmail\":{\"account\":\"gmail\",\"name\":\"Tom\",\"email\":" +
            "\"tom@gmail.com\",\"password\":\"123\",\"date\":\"01.01.1990\"," +
            "\"comment\":\"My gmail account.\"}}"

    // this is where model will create delete and edit databases during testing
    // /dev/shm/ is a fake in-memory file system
    val accountsDir = "/dev/shm/accounts/"
    override val SRC_DIR = "${accountsDir}src/"

    val contentResolver: ContentResolver = mock()
    private val descriptor: ParcelFileDescriptor = mock()

    val locationUri: Uri = mock()
    val destinationUri: Uri = mock()

    private val location = "sampledata/tar/main.tar"
    private val destination = "$accountsDir/main.tar"

    /**
     * This method creates empty src folder in a fake file system, it ensures that
     * directory will be empty.
     */
    @Before
    fun setupSrcFolder() {
        val srcFolder = File(SRC_DIR)
        val accountsFolder = File(accountsDir)

        // here we delete accounts folder if it already exists to ensure that it will
        // be empty as is needed for our tests
        if (accountsFolder.exists()) {
            accountsFolder.deleteRecursively()
        }

        // then we create accounts folder and src inside it
        srcFolder.mkdirs()
    }

    fun setupOutputResolver() {
        // to simulate the Android Storage Access Framework
        val fos = FileOutputStream(File(destination))
        whenever(descriptor.fileDescriptor).thenReturn(fos.fd)
        whenever(contentResolver.openFileDescriptor(destinationUri, "w"))
            .thenReturn(descriptor)
    }

    fun setupInputResolver() {
        // to simulate the Android Storage Access Framework
        val fis = FileInputStream(File(location))
        whenever(descriptor.fileDescriptor).thenReturn(fis.fd)
        whenever(contentResolver.openFileDescriptor(locationUri, "r"))
            .thenReturn(descriptor)
    }

    /**
     * This is a helper method that will copy our test databases from sampledata folder to
     * the fake file system.
     *
     * @param[name] name of the database that we want to copy to the fake file system
     */
    fun copyDatabase(name: String = "database") {
        // this are were we want to copy database .bin and .db files
        val binDestination = File("$SRC_DIR$name.bin")
        val dbDestination = File("$SRC_DIR$name.db")

        // this are the database files that we want to copy
        val binFile = File("sampledata/src/$name.bin")
        val dbFile = File("sampledata/src/$name.db")

        binFile.copyTo(binDestination)
        dbFile.copyTo(dbDestination)
    }
}
