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

package com.acmpo6ou.myaccounts.database.databases_list

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
interface DatabasesBindings {
    @Binds
    @FragmentScoped
    fun databasesPresenter(impl: DatabasesPresenter): DatabasesPresenterI

    @Binds
    @FragmentScoped
    fun databasesModel(impl: DatabasesModel): DatabasesModelI
}

@Module
@InstallIn(FragmentComponent::class)
class DatabasesModule {
    @Provides
    @FragmentScoped
    fun databasesFragment(@ActivityContext activity: Context): DatabaseFragmentI {
        return (activity as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)!!
            .childFragmentManager
            .fragments.first()
            as DatabasesFragment
    }

    @Provides
    @FragmentScoped
    fun mainActivity(@ActivityContext activity: Context): MainActivityI {
        return activity as MainActivityI
    }
}
