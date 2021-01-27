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

package com.acmpo6ou.myaccounts.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.core.CreateEditFragment

class CreateDatabaseFragment : CreateEditFragment() {
    companion object {
        fun newInstance() = CreateDatabaseFragment()
    }
    override lateinit var viewModel: CreateDatabaseViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateDatabaseViewModel::class.java)
        initModel()
        initForm()
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    override fun initModel() {
        super.initModel()
        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        viewModel.initialize(app, SRC_DIR)
    }
}
