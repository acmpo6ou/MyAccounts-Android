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

package com.acmpo6ou.myaccounts.main_activity

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainModelInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.acmpo6ou.myaccounts.randomIntExcept
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class MainPresenterInst {
    lateinit var presenter: MainPresenter
    lateinit var model: MainModelInter
    private lateinit var view: MainActivityInter

    private val faker = Faker()
    private val locationUri: Uri = mock()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val resources: Resources = context.resources
    private val importErrorTitle = resources.getString(R.string.import_error_title)
    private val import2FilesMsg = resources.getString(R.string.import_2_files)
    private val importExistsMsg = resources.getString(R.string.import_exists)
    private val ioError = resources.getString(R.string.io_error)

    @Before
    fun setup(){
        view = mock{
            on{myContext} doReturn context
            on{ACCOUNTS_DIR} doReturn ""
        }
        model = mock()

        presenter = MainPresenter(view)
        presenter.model = model
    }

    @Test
    fun `checkTarFile should check number of files in tar file`(){
        // correct tar file would have 2 files
        // here we return anything but 2 as is needed for test
        whenever(model.countFiles(locationUri)).thenReturn(randomIntExcept(2))
        presenter.checkTarFile(locationUri)

        verify(view).showError(importErrorTitle, import2FilesMsg)
    }

    @Test
    fun `checkTarFile should check names of files`(){
        // mock model to return fake names and correct files count
        val filesList = mutableListOf(
                faker.name().name(),
                faker.name().name())
        whenever(model.getNames(locationUri)).thenReturn(filesList)
        whenever(model.countFiles(locationUri)).thenReturn(2)

        presenter.checkTarFile(locationUri)
        val importDifferentNamesMsg = resources.getString(
                R.string.import_diff_names, filesList[0], filesList[1])
        verify(view).showError(importErrorTitle, importDifferentNamesMsg)
    }

    @Test
    fun `checkTarFile should check bin file size`(){
        // mock model to return fake sizes, correct files count and file names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                // size of db file should be not less then 100
                100,
                // size of bin file should be exactly 16
                randomIntExcept(16, 0, 200))

        whenever(model.getNames(locationUri)).thenReturn(filesList)
        whenever(model.countFiles(locationUri)).thenReturn(2)
        whenever(model.getSizes(locationUri)).thenReturn(sizesList)

        presenter.checkTarFile(locationUri)
        val importBinSizeMsg = resources.getString(R.string.import_bin_size, sizesList[1])
        verify(view).showError(importErrorTitle, importBinSizeMsg)
    }

    @Test
    fun `checkTarFile should check db file size`(){
        // mock model to return fake sizes, correct files count and file names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                // size of db file should be not less then 100
                faker.number().numberBetween(0, 90),
                16) // size of bin file should be exactly 16

        whenever(model.getNames(locationUri)).thenReturn(filesList)
        whenever(model.countFiles(locationUri)).thenReturn(2)
        whenever(model.getSizes(locationUri)).thenReturn(sizesList)

        presenter.checkTarFile(locationUri)
        val importBinSizeMsg = resources.getString(R.string.import_db_size, sizesList[0])
        verify(view).showError(importErrorTitle, importBinSizeMsg)
    }

    @Test
    fun `importDatabase should handle IOException`(){
        whenever(model.importDatabase(locationUri)).thenAnswer{
            throw IOException()
        }
        presenter.importDatabase(locationUri)

        verify(view).showError(importErrorTitle, ioError)
    }

    @Test
    fun `importDatabase should handle FileAlreadyExistsException`(){
        whenever(model.importDatabase(locationUri)).thenAnswer{
            throw FileAlreadyExistsException(File(""))
        }
        presenter.importDatabase(locationUri)

        verify(view).showError(importErrorTitle, importExistsMsg)
    }

    @Test
    fun `importDatabase should handle any other Exception`(){
        val msg = faker.str()
        val exception = Exception(msg)
        whenever(model.importDatabase(locationUri)).thenAnswer{
            throw exception
        }
        presenter.importDatabase(locationUri)

        verify(view).showError(importErrorTitle, exception.toString())
    }
}