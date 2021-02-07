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

package com.acmpo6ou.myaccounts.database_fragment

import android.content.ContentResolver
import android.net.Uri
import com.acmpo6ou.myaccounts.core.*
import com.acmpo6ou.myaccounts.database.Database
import com.acmpo6ou.myaccounts.database.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.database.DatabasesModelInter
import com.acmpo6ou.myaccounts.database.DatabasesPresenter
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before

open class DatabasesPresenterTest : DatabaseUtils{
    lateinit var view: DatabaseFragmentInter
    lateinit var model: DatabasesModelInter
    lateinit var presenter: DatabasesPresenter

    lateinit var app: MyApp
    override lateinit var SRC_DIR: String

    var locationUri: Uri = mock()
    val contextResolver: ContentResolver = mock()

    val faker = Faker()
    val salt = "0123456789abcdef".toByteArray()

    fun setupPresenter(){
        model = mock()
        presenter = DatabasesPresenter(view)
        presenter.model = model
        presenter.databases = mutableListOf(
                Database("main"),
                Database("test", "123", salt, mapOf()))
    }

    fun callExportDatabase(){
        presenter.exportIndex = 1
        presenter.exportDatabase(locationUri)
    }

    @Before
    fun setUp(){
        app = MyApp()
        app.keyCache = mutableMapOf("123" to deriveKey("123", salt))

        view = mock{
            on{ACCOUNTS_DIR} doReturn ""
            on{app} doReturn DatabasesPresenterTest@app
        }
    }
}
