/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.databinding.FragmentListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Super class for fragments that contain list of items.
 */
abstract class ListFragment : Fragment(), ListFragmentI {
    private var binding: FragmentListBinding? = null
    val b: FragmentListBinding get() = binding!!

    abstract val items: List<*>
    abstract val adapter: RecyclerView.Adapter<*>
    abstract val presenter: ListPresenter
    abstract val actionCreateItem: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        b.addItem.setOnClickListener {
            view.findNavController().navigate(actionCreateItem)
        }

        b.itemsList.layoutManager = LinearLayoutManager(context)
        b.itemsList.adapter = adapter

        // listener to hide/display FAB when scrolling, so that the FAB doesn't prevent from
        // accessing items of the list
        b.itemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    b.addItem.hide()
                } else if (dy < 0) {
                    b.addItem.show()
                }
            }
        })
    }

    /**
     * Decides whether to show recycler view placeholder (tip that is shown when
     * recycler is empty).
     * If there are items in the list it hides placeholder, otherwise – displays it.
     */
    fun checkListPlaceholder() {
        b.itemsList.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        b.noItems.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * Displays a snackbar with success message.
     */
    override fun showSuccess() {
        Snackbar.make(
            b.coordinatorLayout,
            R.string.success_message,
            Snackbar.LENGTH_LONG
        )
            .setAction("HIDE") {}
            .show()
    }

    /**
     * Builds and displays confirmation dialog.
     *
     * @param[message] message to describe what we asking user to confirm.
     * @param[positiveAction] function to invoke when user confirms an action (i.e. presses
     * the `Yes` button).
     */
    inline fun confirmDialog(message: String, crossinline positiveAction: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(message)
            .setIcon(R.drawable.ic_warning)
            .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
            .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                positiveAction()
            }
            .show()
    }

    override fun notifyInserted(i: Int) {
        adapter.notifyItemInserted(i)
        checkListPlaceholder()
    }

    override fun notifyChanged(i: Int) = adapter.notifyItemChanged(i)

    override fun notifyRemoved(i: Int) {
        adapter.notifyItemRemoved(i)
        checkListPlaceholder()
    }
}
