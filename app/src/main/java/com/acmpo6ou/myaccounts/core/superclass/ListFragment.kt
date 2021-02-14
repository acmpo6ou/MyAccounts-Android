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

package com.acmpo6ou.myaccounts.core.superclass

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MyApp
import com.acmpo6ou.myaccounts.database.superclass.SuperFragment
import com.acmpo6ou.myaccounts.databinding.FragmentListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class ListFragment : SuperFragment(), ListFragmentInter {
    private var binding: FragmentListBinding? = null
    val b: FragmentListBinding get() = binding!!

    abstract val items: List<*>
    abstract val adapter: RecyclerView.Adapter<*>
    abstract val presenter: ListPresenter
    abstract val actionCreateItem: Int

    lateinit var myContext: Context
    lateinit var app: MyApp

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
        app = context.applicationContext as MyApp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkListPlaceholder()
        // when clicking on (+) FAB navigate to fragment where item will be created
        b.addItem.setOnClickListener{
            view.findNavController().navigate(actionCreateItem)
        }

        b.itemsList.layoutManager = LinearLayoutManager(myContext)
        b.itemsList.adapter = adapter
    }

    /**
     * This method decides whether to show recycler view placeholder (tip that is shown when
     * recycler is empty).
     *
     * If there are items in the list it hides placeholder, if there aren't it displays
     * the placeholder.
     */
    fun checkListPlaceholder(){
        if (items.isEmpty()) {
            b.itemsList.visibility = View.GONE
            b.noItems.visibility = View.VISIBLE
        }
        else{
            b.itemsList.visibility = View.VISIBLE
            b.noItems.visibility = View.GONE
        }
    }

    /**
     * Used to build and display confirmation dialog.
     *
     * @param[message] message to describe what we asking user to confirm.
     * @param[positiveAction] function to invoke when user confirms an action (i.e. presses
     * the `Yes` button).
     */
    inline fun confirmDialog(message: String, crossinline positiveAction: ()->Unit) {
        MaterialAlertDialogBuilder(myContext)
                .setTitle(R.string.warning)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                .setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                    positiveAction()
                }
                .show()
    }

    /**
     * This method rerenders list after any item have changed.
     * @param[i] index of item that have changed.
     */
    override fun notifyChanged(i: Int) {
        adapter.notifyItemChanged(i)
        adapter.notifyItemRangeChanged(i, 1)
        checkListPlaceholder()
    }

    /**
     * This method rerenders list after any item have been deleted.
     * @param[i] index of item that have been deleted.
     */
    override fun notifyRemoved(i: Int) {
        adapter.notifyItemRemoved(i)
        adapter.notifyItemRangeRemoved(i, 1)
        checkListPlaceholder()
    }
}

interface ListFragmentInter{
    fun notifyChanged(i: Int)
    fun notifyRemoved(i: Int)
}