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

package com.acmpo6ou.myaccounts.account.display_account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.accounts_activity.AccountsActivityI
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.acmpo6ou.myaccounts.databinding.FragmentDisplayAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAccountFragment : Fragment(), DisplayAccountFragmentI {
    private var binding: FragmentDisplayAccountBinding? = null
    val b: FragmentDisplayAccountBinding get() = binding!!
    override lateinit var account: Account

    @Inject
    lateinit var presenter: Lazy<DisplayAccountPresenterI>

    @Inject
    lateinit var adapter: Lazy<DisplayAccountAdapter>

    @Inject
    lateinit var accountsActivity: AccountsActivityI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDisplayAccountBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getString("accountName")?.let {
            account = accountsActivity.database.data[it]!!
            initForm(account)
        }

        b.attachedFilesList.layoutManager = LinearLayoutManager(context)
        b.attachedFilesList.adapter = adapter.get()
    }

    /**
     * Initializes display account form with data provided from [account].
     */
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun initForm(account: Account) {
        // set account name as app bar title
        (activity as? AppCompatActivity)?.supportActionBar?.title = account.accountName

        // set text on all of the form account labels
        val usernameStr = requireContext().resources.getString(R.string.username_)
        val emailStr = requireContext().resources.getString(R.string.e_mail_)
        val passwordStr = requireContext().resources.getString(R.string.password_)
        val commentStr = requireContext().resources.getString(R.string.comment_)

        b.accountUsername.text = "$usernameStr ${account.username}"
        b.accountEmail.text = "$emailStr ${account.email}"
        b.accountPassword.text = "$passwordStr ${"•".repeat(16)}"
        b.birthDate.text = account.date
        b.accountComment.text = "$commentStr\n${account.comment}"

        // display password only when password label is held, otherwise hide it
        b.accountPassword.setOnTouchListener { v, event ->
            val view = v as TextView
            if (event.action == ACTION_DOWN) {
                view.text = "$passwordStr ${account.password}"
            } else if (event.action == ACTION_UP) {
                view.text = "$passwordStr ${"•".repeat(16)}"
            }
            true
        }

        if (account.attachedFiles.isEmpty()) {
            b.attachedFilesLabel.visibility = View.GONE
        }
    }

    private val saveFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { presenter.get().saveFile(it) }
            }
        }

    /**
     * Used to display save file dialog, so that user can chose location where to save
     * attached file.
     *
     * Displays dialog to choose location using Storage Access Framework.
     * @param[fileName] name of the file we want to save.
     */
    override fun saveFileDialog(fileName: String) =
        with(Intent(Intent.ACTION_CREATE_DOCUMENT)) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, fileName)
            saveFileLauncher.launch(this)
        }

    /**
     * Displays a snackbar with success message.
     */
    override fun showSuccess() {
        Snackbar.make(
            b.displayAccountLayout,
            R.string.success_message,
            Snackbar.LENGTH_LONG
        )
            .setAction("HIDE") {}
            .show()
    }

    /**
     * Used to display dialog saying that the error has occurred.
     * @param[details] details about the error.
     */
    override fun showError(details: String) {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.error_title)
            .setIcon(R.drawable.ic_error)
            .setNeutralButton("Ok") { _: DialogInterface, _: Int -> }
            .setMessage(details)
            .show()
    }

    /**
     * Displays error dialog saying that the file is corrupted.
     */
    override fun fileCorrupted() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.error_title)
            .setIcon(R.drawable.ic_error)
            .setNeutralButton("Ok") { _: DialogInterface, _: Int -> }
            .setMessage(R.string.file_is_corrupted)
            .show()
    }
}
