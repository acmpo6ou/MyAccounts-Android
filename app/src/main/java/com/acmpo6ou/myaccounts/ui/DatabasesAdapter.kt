/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter


/**
 * [RecyclerView.Adapter] that can display a [Database].
 */
class DatabasesAdapter(
        private val view: DatabaseFragmentInter
) : RecyclerView.Adapter<DatabasesAdapter.ViewHolder>() {
    val presenter: DatabasesPresenterInter
        get() = view.presenter // alias
    val databases: List<Database>
        get() = presenter.databases // alias

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_database, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val database = databases[position]
        // set database item name
        holder.databaseName.text = database.name

        // set appropriate lock icon according to isOpen property
        // note: we also set tag on lock icon so later we can determine icon resource
        if(database.isOpen){

        }
        else{
            holder.lockImage.setImageResource(R.drawable.ic_locked)
            holder.lockImage.tag = R.drawable.ic_locked
        }
    }

    override fun getItemCount(): Int = presenter.databases.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lockImage: ImageView
        var databaseName: TextView
        var menu: TextView

        init{
            lockImage = view.findViewById(R.id.databaseLock)
            databaseName = view.findViewById(R.id.databaseItemName)
            menu = view.findViewById(R.id.dots_menu)

            // when click is performed on database item we should display open database form
            // or start AccountsActivity for given database, this behaviour is decided in
            // openDatabase method
            view.setOnClickListener{
                presenter.openDatabase(adapterPosition)
            }
        }
    }
}