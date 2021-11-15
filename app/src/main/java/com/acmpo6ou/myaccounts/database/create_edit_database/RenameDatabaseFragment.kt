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

package com.acmpo6ou.myaccounts.database.create_edit_database

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.databinding.RenameDatabaseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
open class RenameDatabaseFragment : Fragment() {
    open val viewModel: RenameDatabaseViewModel by viewModels()
    var databaseIndex by Delegates.notNull<Int>()

    private var binding: RenameDatabaseFragmentBinding? = null
    val b: RenameDatabaseFragmentBinding get() = binding!!

    @Inject
    lateinit var app: MyApp

    @Inject
    lateinit var superActivity: MainActivity

    @Inject
    @ActivityContext
    lateinit var myContext: Context

    // Hides/displays name error tip
    private val nameErrorObserver = Observer<String?> {
        b.parentName.error = it
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = RenameDatabaseFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = RenameDatabaseFragmentArgs.fromBundle(it)
            databaseIndex = args.databaseIndex
            viewModel.databaseIndex = databaseIndex
        }
        initForm()
    }

    fun initForm() {
        val dbName = app.databases[databaseIndex].name

        // Set toolbar title to `Rename <database name>`
        val appTitle = myContext.resources.getString(R.string.rename_db, dbName)
        superActivity.supportActionBar?.title = appTitle

        // fill databaseName field with database name
        b.databaseName.setText(dbName)

        b.saveButton.setOnClickListener {
            viewModel.savePressed(b.databaseName.text.toString())
        }
    }
}