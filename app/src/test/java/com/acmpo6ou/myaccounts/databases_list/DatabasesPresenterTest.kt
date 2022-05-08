/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.databases_list

import android.net.Uri
import com.acmpo6ou.myaccounts.core.MyApplication
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.utils.DatabaseUtils
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentI
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesModelI
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesPresenter
import com.acmpo6ou.myaccounts.salt
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before

open class DatabasesPresenterTest : DatabaseUtils {
    lateinit var view: DatabasesFragmentI
    lateinit var model: DatabasesModelI
    lateinit var presenter: DatabasesPresenter
    override lateinit var app: MyApp

    var locationUri: Uri = mock()
    val faker = Faker()

    fun setupPresenter() {
        view = mock()
        model = mock()

        presenter = DatabasesPresenter({ view }, model, app)
        presenter.databases = mutableListOf(
            Database("main"),
            Database("test", "123", salt, mutableMapOf())
        )
    }

    fun callExportDatabase() {
        presenter.exportIndex = 1
        presenter.exportDatabase(locationUri)
    }

    @Before
    fun setupApp() {
        app = MyApplication()
        app.keyCache = mutableMapOf("123" to deriveKey("123", salt))
    }
}
