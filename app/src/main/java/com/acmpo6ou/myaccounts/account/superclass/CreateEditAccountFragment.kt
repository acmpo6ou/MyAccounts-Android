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

package com.acmpo6ou.myaccounts.account.superclass

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.core.getFileName
import com.acmpo6ou.myaccounts.core.superclass.CreateEditFragment
import com.acmpo6ou.myaccounts.databinding.CreateEditAccountFragmentBinding
import com.acmpo6ou.myaccounts.ui.account.AttachedFilesAdapter
import com.acmpo6ou.myaccounts.ui.account.CreateAccountViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Super class for all fragments that create/edit accounts.
 */
abstract class CreateEditAccountFragment : CreateEditFragment() {
    abstract override val viewModel: CreateAccountViewModel
    val LOAD_FILE_RC = 808

    override val applyButton get() = b.applyButton
    override val buttonGenerate get() = b.accountGenerate

    override val nameField get() = b.accountName
    override val passwordField get() = b.accountPassword
    override val repeatPasswordField get() = b.accountRepeatPassword

    override val parentName get() = b.parentName
    override val parentPassword get() = b.parentPassword

    private var binding: CreateEditAccountFragmentBinding? = null
    val b: CreateEditAccountFragmentBinding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateEditAccountFragmentBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = requireContext()
        app = context.applicationContext as MyApp
    }

    /**
     * Displays dialog to choose file to attach.
     */
    private fun loadFileDialog() =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            startActivityForResult(this, LOAD_FILE_RC)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == LOAD_FILE_RC) data?.data?.let {
            val fileName = myContext.getFileName(it) ?: ""
            viewModel.addFile(it, fileName)
        }
    }

    /**
     * Initializes AttachedFilesAdapter for attachedFilesList recycler view.
     */
    fun initAdapter() {
        val adapter = AttachedFilesAdapter(this)
        viewModel.notifyAdded?.observe(viewLifecycleOwner, adapter.addedObserver)
        viewModel.notifyRemoved?.observe(viewLifecycleOwner, adapter.removedObserver)

        b.attachedFilesList.layoutManager = LinearLayoutManager(context)
        b.attachedFilesList.adapter = adapter
    }

    /**
     * Used to initialize all fields and buttons of the create_edit_account form.
     */
    @SuppressLint("SimpleDateFormat")
    override fun initForm() {
        super.initForm()

        // call applyPressed when clicking on the apply button
        applyButton.setOnClickListener {
            viewModel.applyPressed(
                b.accountName.text.toString(),
                b.accountUsername.text.toString(),
                b.accountEmail.text.toString(),
                b.accountPassword.text.toString(),
                b.birthDate.text.toString(),
                b.accountComment.text.toString()
            )
        }

        b.addFile.setOnClickListener {
            loadFileDialog()
        }

        // display date picker when clicking on date label
        b.birthDate.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText(R.string.pick_date)

            val currentlyPickedDate = SimpleDateFormat("dd.MM.yyyy")
                .parse(b.birthDate.text.toString())
                .time + TimeUnit.DAYS.toMillis(1)
            builder.setSelection(currentlyPickedDate)

            val dialog = builder.build()
            dialog.addOnPositiveButtonClickListener {
                val date = Date(it)
                val format = SimpleDateFormat("dd.MM.yyyy")
                b.birthDate.text = format.format(date)
            }
            dialog.show(requireActivity().supportFragmentManager, dialog.toString())
        }
    }
}
