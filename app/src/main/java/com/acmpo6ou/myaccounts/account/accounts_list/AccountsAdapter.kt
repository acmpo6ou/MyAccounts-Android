/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.account.accounts_list

import android.content.Context
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
import com.caverock.androidsvg.SVGImageView
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AccountsAdapter @Inject constructor(
    private val presenter: AccountsListPresenterI,
    @ActivityContext private val context: Context,
) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    private val accountsList get() = presenter.accountsList

    // it's important to sort icon names by length (longer names first) because this way we will
    // have better matches
    private val icons = context.assets.list("")!!
        .map { it.removeSuffix(".svg") }
        .sortedByDescending { it.length }

    /**
     * Loads account icon into [image] given [accountName].
     */
    fun loadAccountIcon(image: SVGImageView, accountName: String) {
        for (icon in icons) {
            if (icon in accountName.lowercase()) {
                image.setImageAsset("$icon.svg")
                return
            }
        }
        image.setImageAsset("account.svg")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accountsList[position]

        // set account item name
        holder.accountName.text = account.accountName

        loadAccountIcon(holder.accountIcon, account.accountName)

        // set popup menu on item
        holder.menu.setOnClickListener { it ->
            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.accounts_item_menu)

            // set color of `Delete` item to red
            popup.menu.findItem(R.id.delete_account_item).let {
                val spanStr = SpannableString(it.title)
                val redColor = ContextCompat.getColor(context, R.color.red)
                spanStr.setSpan(ForegroundColorSpan(redColor), 0, it.title?.length ?: 0, 0)
                it.title = spanStr
            }

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_account_item -> presenter.editAccount(account)
                    R.id.delete_account_item -> presenter.deleteSelected(account)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = accountsList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var accountIcon: SVGImageView = view.findViewById(R.id.accountIcon)
        var accountName: TextView = view.findViewById(R.id.accountName)
        var menu: TextView = view.findViewById(R.id.dots_menu)

        init {
            // navigate to DisplayAccountFragment when account item is selected
            view.setOnClickListener {
                val account = accountsList[bindingAdapterPosition]
                presenter.displayAccount(account)
            }
        }
    }
}
