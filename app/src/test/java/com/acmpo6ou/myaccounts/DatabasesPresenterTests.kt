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

import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesModelInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenter
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap

class DatabasesPresenterTests {
    private lateinit var view: DatabaseFragmentInter
    private lateinit var presenter: DatabasesPresenter
    private val faker = Faker()

    @Before
    fun setUp(){
        view = mock()
        presenter = DatabasesPresenter(view)
        presenter.databases = listOf(Database("main"))
    }

    @Test
    fun `exportSelected should call exportDialog`(){
        presenter.exportSelected(0)
        verify(view).exportDialog(0)
    }

    @Test
    fun `exportSelected should save database index to exportIndex`(){
        presenter.exportSelected(1)
        assertEquals(1, presenter.exportIndex)
    }

    @Test
    fun `deleteSelected should call confirmDelete`(){
        presenter.deleteSelected(0)
        verify(view).confirmDelete(0)
    }

    @Test
    fun `closeSelected should call closeDatabase when database is saved`(){
        // mock isDatabaseSaved to return true that will mean that database is saved
        // so presenter should call closeDatabase in this condition
        val presenterSpy = spy(presenter)
        whenever(presenterSpy.isDatabaseSaved(0)).thenReturn(true)

        presenterSpy.closeSelected(0)
        verify(presenterSpy).closeDatabase(0)
    }

    @Test
    fun `closeSelected should call confirmClose when database isn't saved`(){
        // mock isDatabaseSaved to return false that will mean that database is saved
        // so presenter should call confirmClose in this condition
        val presenterSpy = spy(presenter)
        whenever(presenterSpy.isDatabaseSaved(0)).thenReturn(false)

        presenterSpy.closeSelected(0)
        verify(view).confirmClose(0)
    }

    @Test
    fun `editSelected should call navigateToEdit passing through serialized database`(){
        // mock model.dumps() to return fake serialized string
        val expectedJson = faker.lorem().sentence()
        val model: DatabasesModelInter = mock()
        whenever(model.dumps(anyMap())).thenReturn(expectedJson)
        presenter.model = model

        presenter.editSelected(0)
        verify(view).navigateToEdit(expectedJson)
    }
}