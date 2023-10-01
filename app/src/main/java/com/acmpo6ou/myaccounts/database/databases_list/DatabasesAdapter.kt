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

package com.acmpo6ou.myaccounts.database.databases_list

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class DatabasesAdapter @Inject constructor(
    private val presenter: DatabasesPresenterI,
    @ActivityContext private val context: Context,
    private val app: MyApp
) : RecyclerView.Adapter<DatabasesAdapter.ViewHolder>() {

    private val databases: List<Database> get() = app.databases
    override fun getItemCount() = databases.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val database = databases[position]

        // set database item name
        holder.databaseName.text = database.name

        // set appropriate lock icon according to isOpen property
        // note: we also set tag on lock icon so during tests we can determine what image
        // is set on icon
        if (database.isOpen) {
            holder.lockImage.setImageResource(R.drawable.ic_opened)
            holder.lockImage.tag = R.drawable.ic_opened
        } else {
            holder.lockImage.setImageResource(R.drawable.ic_locked)
            holder.lockImage.tag = R.drawable.ic_locked
        }

        // set popup menu on item
        holder.menu.setOnClickListener { it ->
            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.database_item_menu)

            // set color of `Delete` item to red
            popup.menu.findItem(R.id.delete_database_item).let {
                val spanStr = SpannableString(it.title)
                val redColor = ContextCompat.getColor(context, R.color.red)
                spanStr.setSpan(ForegroundColorSpan(redColor), 0, it.title?.length ?: 0, 0)
                it.title = spanStr
            }

            // if database isn't opened we can't close it
            if (!database.isOpen)
                popup.menu.findItem(R.id.close_database_item)
                    .isEnabled = false

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.export_database_item -> presenter.exportSelected(database)
                    R.id.delete_database_item -> presenter.deleteSelected(database)
                    R.id.edit_database_item -> presenter.editSelected(database)
                    R.id.close_database_item -> presenter.closeSelected(database)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
            popup.show()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lockImage: ImageView = view.findViewById(R.id.itemIcon)
        var databaseName: TextView = view.findViewById(R.id.itemName)
        var menu: TextView = view.findViewById(R.id.dots_menu)

        init {
            // when click is performed on database item we should display open database form
            // or start AccountsActivity for given database, this behaviour is decided in
            // openDatabase method of presenter
            view.setOnClickListener {
                val database = databases[bindingAdapterPosition]
                presenter.openDatabase(database)
            }
        }
    }
}
