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

package com.acmpo6ou.myaccounts.account.accounts_list

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.acmpo6ou.myaccounts.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(FragmentComponent::class)
interface AccountsListBindings {
    @Binds
    fun accountsListPresenter(impl: AccountsListPresenter): AccountsListPresenterI
}

@Module
@InstallIn(FragmentComponent::class)
object AccountsListModule {
    @Provides
    fun accountsFragment(@ActivityContext activity: Context): AccountsFragmentI {
        return (activity as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)!!
            .childFragmentManager
            .fragments.first()
            as AccountsFragment
    }
}
