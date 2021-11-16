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
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.superclass.ErrorFragment
import com.acmpo6ou.myaccounts.databinding.RenameDatabaseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
open class RenameDatabaseFragment : Fragment(), ErrorFragment {
    override val viewModel: RenameDatabaseViewModel by viewModels()
    override lateinit var myLifecycle: LifecycleOwner
    var databaseIndex by Delegates.notNull<Int>()

    private var binding: RenameDatabaseFragmentBinding? = null
    val b: RenameDatabaseFragmentBinding get() = binding!!

    @Inject
    lateinit var app: MyApp

    @Inject
    override lateinit var superActivity: MainActivity

    @Inject
    @ActivityContext
    lateinit var myContext: Context

    private val nameErrorObserver = Observer<String?> {
        // hide/display name error tip
        b.parentName.error = it

        // enable/disable Save button depending on whether there are errors
        b.saveButton.isEnabled = it == null
    }

    // navigate back to the main fragment when database is successfully renamed
    private val finishedObserver = Observer<Boolean> {
        if (it) (myContext as AppCompatActivity)
            .findNavController(R.id.nav_host_fragment)
            .navigateUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        myLifecycle = viewLifecycleOwner
        initModel()
        initForm()
    }

    fun initForm() {
        val dbName = app.databases[databaseIndex].name

        // Set toolbar title to `Rename <database name>`
        val appTitle = myContext.resources.getString(R.string.rename_db, dbName)
        superActivity.supportActionBar?.title = appTitle

        // fill databaseName field with database name
        b.databaseName.setText(dbName)
        viewModel.validateName(dbName)

        b.saveButton.setOnClickListener {
            viewModel.savePressed(b.databaseName.text.toString())
        }

        // when name is changed validate it using model to display error in case
        // such name already exists or the name is empty
        b.databaseName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                viewModel.validateName(s.toString())
        })

        // save changes when Enter is pressed in name field
        b.databaseName.setOnEditorActionListener {
                _: TextView, action: Int, keyEvent: KeyEvent? ->

            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER ||
                action == EditorInfo.IME_ACTION_DONE ||
                action == EditorInfo.IME_ACTION_NEXT
            ) {
                b.saveButton.performClick()
            }
            false
        }
    }

    override fun initModel() {
        super.initModel()
        viewModel.nameErrors.observe(myLifecycle, nameErrorObserver)
        viewModel.finished.observe(myLifecycle, finishedObserver)
    }
}
