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
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acmpo6ou.myaccounts.MyApp
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

    @Inject
    lateinit var app: MyApp

    @Inject
    lateinit var inputManager: InputMethodManager

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

        // listener to hide/display FAB when scrolling, so that the FAB doesn't prevent from
        // reading possibly long comment
        b.scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val dy = scrollY - oldScrollY
            if (dy > 0) {
                b.copyPassword.hide()
            } else if (dy < 0) {
                b.copyPassword.show()
            }
        }

        if (account.attachedFiles.isEmpty()) b.attachedFilesLabel.visibility = View.GONE

        b.copyPassword.setOnClickListener {
            app.password = account.password
            passwordCopied()

            checkBoardEnabled()
            showChangeInputMethodDialog()
        }
    }

    /**
     * Checks whether MyAccountsBoard service is enabled in settings by user.
     * If it isn't, goes to input method settings to allow user to enable the service.
     */
    private fun checkBoardEnabled() {
        val isGranted = inputManager
            .enabledInputMethodList
            .any { it.packageName == context?.packageName }

        if (!isGranted) {
            Intent(ACTION_INPUT_METHOD_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
            }
        }
    }

    /**
     * Displays InputMethodPicker dialog for user to chose MyAccountsBoard as current keyboard
     * if it isn't already current.
     */
    private fun showChangeInputMethodDialog() {
        val defaultIME = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(defaultIME)?.packageName
        if (defaultInputMethod != context?.packageName) inputManager.showInputMethodPicker()
    }

    /**
     * Displays snackbar saying that password is copied.
     */
    private fun passwordCopied() {
        Snackbar.make(
            b.displayAccountLayout,
            R.string.copied, Snackbar.LENGTH_LONG
        )
            .setAction("HIDE") {}
            .show()
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
