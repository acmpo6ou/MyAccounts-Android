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

package com.acmpo6ou.myaccounts.database.main_activity

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
interface MainActivityBindings {
    @Binds
    @ActivityScoped
    fun mainPresenter(impl: MainPresenter): MainPresenterI

    @Binds
    @ActivityScoped
    fun mainModel(impl: MainModel): MainModelI
}

@Module
@InstallIn(ActivityComponent::class)
object MainActivityModule {
    @Provides
    @ActivityScoped
    fun mainActivity(@ActivityContext activity: Context): MainActivityI {
        return activity as MainActivityI
    }

    @Provides
    @ActivityScoped
    fun prefs(@ActivityContext activity: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(activity)
    }
}
