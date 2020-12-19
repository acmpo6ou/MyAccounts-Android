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

package com.acmpo6ou.myaccounts.main_activity

import android.content.Context
import android.content.res.Resources
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.MainModelInter
import com.acmpo6ou.myaccounts.core.MainPresenter
import com.acmpo6ou.myaccounts.randomIntExcept
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainPresenterInst {
    lateinit var presenter: MainPresenter
    lateinit var model: MainModelInter
    private lateinit var view: MainActivityInter

    private val faker = Faker()
    private val location: String = faker.file().fileName()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val resources: Resources = context.resources
    private val importErrorTitle = resources.getString(R.string.import_error_title)
    private val import2FilesMsg = resources.getString(R.string.import_2_files)

    @Before
    fun setup(){
        view = mock()
        whenever(view.myContext).thenReturn(context)
        whenever(view.ACCOUNTS_DIR).thenReturn("")
        model = mock()

        presenter = MainPresenter(view)
        presenter.model = model
    }

    @Test
    fun `checkTarFile should check number of files in tar file`(){
        // correct tar file would have 2 files
        // here we return anything but 2 as is needed for test
        whenever(model.countFiles(location)).thenReturn(randomIntExcept(2))
        presenter.checkTarFile(location)

        verify(view).showError(importErrorTitle, import2FilesMsg)
    }

    @Test
    fun `checkTarFile should check names of files`(){
        // mock model to return fake names and correct files count
        val filesList = mutableListOf(
                faker.name().name(),
                faker.name().name(),
        )
        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)

        presenter.checkTarFile(location)
        val importDifferentNamesMsg = resources.getString(
                R.string.import_diff_names, filesList[0], filesList[1])
        verify(view).showError(importErrorTitle, importDifferentNamesMsg)
    }

    @Test
    fun `checkTarFile should check bin file size`(){
        // mock model to return fake sizes, correct files count and file names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                // size of bin file should be exactly 16
                randomIntExcept(16, 0, 200),
                // size of db file should be not less then 100
                100,
        )

        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)
        whenever(model.getSizes(location)).thenReturn(sizesList)

    @Test
    fun `checkTarFile should check bin file size`(){
        // mock model to return fake sizes, correct files count and file names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                // size of bin file should be exactly 16
                randomIntExcept(16, 0, 200),
                // size of db file should be not less then 100
                faker.number().numberBetween(0, 90)
        )

        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)
        whenever(model.getSizes(location)).thenReturn(sizesList)

        presenter.checkTarFile(location)
        val importBinSizeMsg = resources.getString(R.string.import_bin_size, sizesList[0])
        verify(view).showError(importErrorTitle, importBinSizeMsg)
    }
        presenter.checkTarFile(location)
        val importBinSizeMsg = resources.getString(R.string.import_bin_size, sizesList[0])
        verify(view).showError(importErrorTitle, importBinSizeMsg)
    }

    @Test
    fun `checkTarFile should check db file size`(){
        // mock model to return fake sizes, correct files count and file names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                // size of bin file should be exactly 16
                16,
                // size of db file should be not less then 100
                faker.number().numberBetween(0, 90)
        )

        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)
        whenever(model.getSizes(location)).thenReturn(sizesList)

        presenter.checkTarFile(location)
        val importBinSizeMsg = resources.getString(R.string.import_db_size, sizesList[1])
        verify(view).showError(importErrorTitle, importBinSizeMsg)
    }

    @Test
    fun `checkTarFile should call importDatabase if there are no errors`(){
        // mock model to return correct file sizes, count and names
        val filesList = mutableListOf("main", "main")
        val sizesList = mutableListOf(
                16, // size of bin file should be exactly 16
                100 // size of db file should be not less then 100
        )

        whenever(model.getNames(location)).thenReturn(filesList)
        whenever(model.countFiles(location)).thenReturn(2)
        whenever(model.getSizes(location)).thenReturn(sizesList)

        presenter.checkTarFile(location)
        verify(view, never()).showError(anyString(), anyString())
    }
}