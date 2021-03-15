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

package com.acmpo6ou.myaccounts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.databinding.UpdatesActivityBinding
import com.acmpo6ou.myaccounts.ui.UpdatesViewModel

class UpdatesActivity : AppCompatActivity() {
    private var binding: UpdatesActivityBinding? = null
    val b: UpdatesActivityBinding get() = binding!!
    lateinit var viewModel: UpdatesViewModel

    private val changelogObserver = Observer<String>{
        b.changelogText.text = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdatesActivityBinding.inflate(layoutInflater)
        setContentView(b.root)

        viewModel = ViewModelProvider(this).get(UpdatesViewModel::class.java)
        viewModel.changelog.observe(this, changelogObserver)
//        viewModel.getChangelog()
    }
}