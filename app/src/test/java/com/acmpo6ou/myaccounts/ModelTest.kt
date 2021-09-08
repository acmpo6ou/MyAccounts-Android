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
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

open class ModelTest : DatabaseUtils {
    val faker = Faker()
    override lateinit var app: MyApp

    open val password = "123"
    val jsonDatabase =
        "{\"gmail\":{\"account\":\"gmail\",\"name\":\"Tom\",\"email\":" +
            "\"tom@gmail.com\",\"password\":\"123\",\"date\":\"01.01.1990\"," +
            "\"comment\":\"My gmail account.\"}}"

    val contentResolver: ContentResolver = mock()
    private val descriptor: ParcelFileDescriptor = mock()

    val locationUri: Uri = mock()
    val destinationUri: Uri = mock()

    open val location = "sampledata/src/main.dba"
    open val destination = "$accountsDir/main.dba"

    @Before
    fun setupApp() {
        app = mock {
            on { ACCOUNTS_DIR } doReturn accountsDir
            on { SRC_DIR } doReturn SRC_DIR
            on { contentResolver } doReturn contentResolver
        }
    }

    /**
     * Creates empty src folder in a fake file system, it ensures that
     * directory will be empty.
     */
    @Before
    fun setupSrcFolder() {
        val srcFolder = File(SRC_DIR)
        val accountsFolder = File(accountsDir)

        // here we delete accounts folder if it already exists to ensure that it will
        // be empty as is needed for our tests
        accountsFolder.deleteRecursively()

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
     * Helper method to copy our test databases from sampledata folder to
     * the fake file system.
     * @param[name] name of the database that we want to copy.
     */
    fun copyDatabase(name: String = "database") {
        val dbFile = File("sampledata/src/$name.dba")
        val dbDestination = File("$SRC_DIR$name.dba")
        dbFile.copyTo(dbDestination)
    }
}
