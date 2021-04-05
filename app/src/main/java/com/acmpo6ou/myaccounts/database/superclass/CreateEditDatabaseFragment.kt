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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.CreateEditFragment
import com.acmpo6ou.myaccounts.database.main_activity.MainActivityInter
import com.acmpo6ou.myaccounts.databinding.CreateEditDatabaseFragmentBinding

/**
 * Super class for CreateDatabaseFragment and EditDatabaseFragment.
 */
abstract class CreateEditDatabaseFragment : CreateEditFragment(), ErrorFragment {
    override val mainActivity: MainActivityInter get() = activity as MainActivity
    override lateinit var lifecycle: LifecycleOwner
    abstract override val viewModel: CreateEditDatabaseModel

    override val applyButton get() = b.applyButton
    override val buttonGenerate get() = b.databaseGenerate

    override val nameField get() = b.databaseName
    override val passwordField get() = b.databasePassword
    override val repeatPasswordField get() = b.databaseRepeatPassword

    override val parentName get() = b.parentName
    override val parentPassword get() = b.parentPassword

    // Hides/displays loading progress bar of apply button.
    private val loadingObserver = Observer<Boolean> {
        if (it) {
            b.progressLoading.visibility = View.VISIBLE
            b.applyButton.isEnabled = false
        } else {
            b.progressLoading.visibility = View.GONE
            b.applyButton.isEnabled = true
        }
    }

    private var binding: CreateEditDatabaseFragmentBinding? = null
    val b: CreateEditDatabaseFragmentBinding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateEditDatabaseFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle = viewLifecycleOwner
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = requireContext()
        app = context.applicationContext as MyApp
    }

    /**
     * Used to initialize all fields and buttons of the create_edit_database form.
     */
    override fun initForm() {
        super.initForm()

        // call applyPressed when clicking on the apply button
        applyButton.setOnClickListener {
            viewModel.applyPressed(
                nameField.text.toString(),
                passwordField.text.toString()
            )
        }
    }

    /**
     * This method initializes view model providing all needed resources.
     */
    override fun initModel() {
        val SRC_DIR = myContext.getExternalFilesDir(null)?.path + "/src"
        viewModel.initialize(app, SRC_DIR)

        viewModel._loading.observe(viewLifecycleOwner, loadingObserver)
        super<ErrorFragment>.initModel()
        super<CreateEditFragment>.initModel()
    }
}
