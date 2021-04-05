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

package com.acmpo6ou.myaccounts.updates_activity

import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.core.UpdatesViewModel
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.File

class UpdatesViewModelTests : ModelTest() {
    lateinit var model: UpdatesViewModel

    @Before
    fun setup() {
        model = UpdatesViewModel()
    }

    @Test
    fun `removeOldApk should delete myaccounts-release apk file from Download folder`() {
        model.DOWNLOAD_DIR_FULL = SRC_DIR
        val oldApk = File("$SRC_DIR/myaccounts-release.apk")
        oldApk.createNewFile()

        model.removeOldApk()
        assertFalse(oldApk.exists())
    }
}
