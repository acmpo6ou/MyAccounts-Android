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

package com.acmpo6ou.myaccounts.account.create_edit_account

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AttachedFilesAdapter @Inject constructor(
    private val view: CreateEditAccountFragment,
    @ActivityContext private val context: Context,
) : RecyclerView.Adapter<AttachedFilesAdapter.ViewHolder>() {

    private val attachedFiles get() = view.viewModel.attachedFilesList
    override fun getItemCount() = attachedFiles.size

    val addedObserver = Observer<Int> {
        notifyItemChanged(it)
        notifyItemRangeChanged(it, 1)
    }
    val removedObserver = Observer<Int> {
        notifyItemRemoved(it)
        notifyItemRangeRemoved(it, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fileName.text = attachedFiles[position]

        // set popup menu on item
        holder.menu.setOnClickListener { it ->
            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.attached_file_item_menu)

            // set color of `Remove` item to red
            popup.menu.findItem(R.id.remove_attached_file).let {
                val spanStr = SpannableString(it.title)
                val redColor = ContextCompat.getColor(context, R.color.red)
                spanStr.setSpan(ForegroundColorSpan(redColor), 0, it.title.length, 0)
                it.title = spanStr
            }

            popup.setOnMenuItemClickListener {
                view.viewModel.removeFile(position)
                true
            }
            popup.show()
        }
    }

    /**
     * ViewHolder for item of attached files list.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fileName: TextView = view.findViewById(R.id.itemName)
        var menu: TextView = view.findViewById(R.id.dots_menu)
    }
}
