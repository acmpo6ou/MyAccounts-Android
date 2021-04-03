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

package com.acmpo6ou.myaccounts.ui.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.DisplayAccountFragmentInter
import com.acmpo6ou.myaccounts.account.DisplayAccountPresenter
import com.acmpo6ou.myaccounts.account.DisplayAccountPresenterInter
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.databinding.FragmentDisplayAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class DisplayAccountFragment : Fragment(), DisplayAccountFragmentInter {
    private var binding: FragmentDisplayAccountBinding? = null
    val b: FragmentDisplayAccountBinding get() = binding!!

    override lateinit var presenter: DisplayAccountPresenterInter
    lateinit var adapter: DisplayAccountAdapter

    override var account = Account("", "", "", "", "", "", true, mutableMapOf())
    override lateinit var myContext: Context
    val SAVE_FILE_RC = 303

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayAccountBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myContext = requireContext()
        arguments?.getString("accountName")?.let {
            val accountsActivity = activity as AccountsActivity
            val account = accountsActivity.database.data[it]!!

            this.account = account
            initForm(account)
        }

        presenter = DisplayAccountPresenter(this)
        adapter = DisplayAccountAdapter(this)

        b.attachedFilesList.layoutManager = LinearLayoutManager(context)
        b.attachedFilesList.adapter = adapter
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

    /**
     * Used to display save file dialog, so that user can chose location where to save
     * attached file.
     *
     * Starts intent with [SAVE_FILE_RC] request code.
     * Shows dialog to choose location using Storage Access Framework.
     * @param[fileName] name of the file we want to save.
     */
    override fun saveFileDialog(fileName: String) {
        Intent(Intent.ACTION_CREATE_DOCUMENT)
            .apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, fileName)
                startActivityForResult(this, SAVE_FILE_RC)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == SAVE_FILE_RC) resultData?.data?.let { presenter.saveFile(it) }
    }

    /**
     * Used to display a snackbar with success message.
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
