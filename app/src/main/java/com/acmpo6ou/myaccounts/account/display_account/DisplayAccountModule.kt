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

package com.acmpo6ou.myaccounts.account.display_account

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.acmpo6ou.myaccounts.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
interface DisplayAccountBindings {
    @Binds
    @FragmentScoped
    fun displayPresenter(impl: DisplayAccountPresenter): DisplayAccountPresenterI

    @Binds
    @FragmentScoped
    fun displayModel(impl: DisplayAccountModel): DisplayAccountModelI
}

@Module
@InstallIn(FragmentComponent::class)
object DisplayAccountModule {
    @Provides
    @FragmentScoped
    fun displayFragment(@ActivityContext activity: Context): DisplayAccountFragmentI {
        return (activity as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)!!
            .childFragmentManager
            .fragments.first()
            as DisplayAccountFragment
    }

    @Provides
    @FragmentScoped
    fun inputMethodManager(@ActivityContext activity: Context): InputMethodManager =
        activity.getSystemService(InputMethodManager::class.java)
}
