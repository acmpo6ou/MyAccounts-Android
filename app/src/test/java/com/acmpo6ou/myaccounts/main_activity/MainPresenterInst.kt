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
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
}