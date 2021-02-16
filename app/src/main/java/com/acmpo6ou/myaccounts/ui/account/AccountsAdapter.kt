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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.account.AccountsFragmentInter
import com.acmpo6ou.myaccounts.account.AccountsListPresenterInter
import com.acmpo6ou.myaccounts.database.Account


/**
 * [RecyclerView.Adapter] that can display an [Account].
 */
class AccountsAdapter(val view: AccountsFragmentInter)
    : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

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
        holder.accountName.text = account.name
    }

    override fun getItemCount(): Int = accountsList.size

    /**
     * Represents ViewHolder for item of accounts list.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var accountName: TextView = view.findViewById(R.id.itemName)

        init {
            // navigate to DisplayAccountFragment when account item is selected
            view.setOnClickListener{
                presenter.displayAccount(adapterPosition)
            }
        }
    }
}
