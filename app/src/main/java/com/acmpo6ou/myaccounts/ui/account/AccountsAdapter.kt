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

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.AccountsFragmentInter
import com.acmpo6ou.myaccounts.account.AccountsListPresenterInter
import com.acmpo6ou.myaccounts.database.Account

/**
 * [RecyclerView.Adapter] that can display an [Account].
 */
class AccountsAdapter(val view: AccountsFragmentInter) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    val presenter: AccountsListPresenterInter get() = view.presenter
    private val accountsList: List<Account> get() = presenter.accountsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accountsList[position]

        // set account item name
        holder.accountName.text = account.accountName

        // set popup menu on item
        holder.menu.setOnClickListener { it ->
            val popup = PopupMenu(view.myContext, it)
            popup.inflate(R.menu.accounts_item_menu)

            // set color of `Delete` item to red
            popup.menu.findItem(R.id.delete_account_item).let {
                val spanStr = SpannableString(it.title)
                val redColor = ContextCompat.getColor(view.myContext, R.color.red)
                spanStr.setSpan(ForegroundColorSpan(redColor), 0, it.title.length, 0)
                it.title = spanStr
            }

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_account_item -> presenter.deleteSelected(position)
                    R.id.edit_account_item -> presenter.editAccount(position)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = accountsList.size

    /**
     * Represents ViewHolder for item of accounts list.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var accountName: TextView = view.findViewById(R.id.itemName)
        var menu: TextView = view.findViewById(R.id.dots_menu)

        init {
            // navigate to DisplayAccountFragment when account item is selected
            view.setOnClickListener {
                presenter.displayAccount(adapterPosition)
            }
        }
    }
}
