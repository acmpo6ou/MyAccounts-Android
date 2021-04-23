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

package com.acmpo6ou.myaccounts.database.superclass

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityI

/**
 * Provides errorObserver to display error message.
 */
interface ErrorFragment {
    val viewModel: DatabaseViewModel
    val mainActivity: MainActivityI
    val lifecycle: LifecycleOwner

    fun initModel() {
        // Observer to display error dialog
        val errorObserver = Observer<String> {
            val errorTitle = mainActivity.myContext.resources.getString(R.string.error_title)
            mainActivity.showError(errorTitle, it)
        }
        viewModel.errorMsg.observe(lifecycle, errorObserver)
    }
}
