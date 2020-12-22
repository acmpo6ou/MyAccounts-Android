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

package com.acmpo6ou.myaccounts

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import java.io.File
import java.io.FileOutputStream

open class ModelTest {
    var salt = "0123456789abcdef".toByteArray() // 16 bytes of salt

    val contentResolver: ContentResolver = mock()
    val locationUri: Uri = mock()
    private val descriptor: ParcelFileDescriptor = mock()
    private val location = "sampledata/tar/main.tar"

    // this is where model will create delete and edit databases during testing
    // /dev/shm/ is a fake in-memory file system
    val accountsDir = "/dev/shm/accounts/"
    val SRC_DIR = "${accountsDir}src/"

    /**
     * This method creates empty src folder in a fake file system, it ensures that
     * directory will be empty.
     */
    @Before
    fun setUpScrFolder(){
        val srcFolder = File(SRC_DIR)
        val accountsFolder = File(accountsDir)

        // here we delete accounts folder if it already exists to ensure that it will
        // be empty as is needed for our tests
        if(accountsFolder.exists()){
            accountsFolder.deleteRecursively()
        }

        // then we create accounts folder and src inside it
        srcFolder.mkdirs()
    }

    @Before
    fun setupResolver(){
        val fos = FileOutputStream(File(location))
        whenever(descriptor.fileDescriptor).thenReturn(fos.fd)
        whenever(contentResolver.openFileDescriptor(locationUri, "r")).thenReturn(descriptor)
    }
}