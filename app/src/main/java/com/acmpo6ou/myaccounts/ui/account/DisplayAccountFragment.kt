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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.Account
import com.acmpo6ou.myaccounts.databinding.FragmentDisplayAccountBinding
import com.google.android.material.snackbar.Snackbar

class DisplayAccountFragment : Fragment() {
    private var binding: FragmentDisplayAccountBinding? = null
    val b: FragmentDisplayAccountBinding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayAccountBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getString("accountName")?.let {
            val accountsActivity = activity as AccountsActivity
            setAccount(accountsActivity.database.data[it]!!)
        }
    }

    /**
     * Displays snackbar saying that password is copied.
     */
    private fun successCopy() {
        Snackbar.make(
            b.displayAccountLayout,
            R.string.copied,
            Snackbar.LENGTH_LONG
        )
            .setAction("HIDE") {}
            .show()
    }

    /**
     * Initializes display account form with data provided from [account].
     */
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun setAccount(account: Account) {
        // set account name as app bar title
        (activity as? AppCompatActivity)?.supportActionBar?.title = account.accountName

        // copy password when `Copy` FAB is pressed
        b.copyPassword.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", account.password)
            clipboard.setPrimaryClip(clip)
            successCopy()
        }

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

        // display password only when password label is held, otherwise hide the password
        b.accountPassword.setOnTouchListener { v, event ->
            val view = v as TextView
            if (event.action == ACTION_DOWN) {
                view.text = "$passwordStr ${account.password}"
            } else if (event.action == ACTION_UP) {
                view.text = "$passwordStr ${"•".repeat(16)}"
            }
            true
        }
    }
}
