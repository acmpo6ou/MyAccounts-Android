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

abstract class ListFragment : SuperFragment() {
    var binding: FragmentListBinding? = null
    val b: FragmentListBinding get() = binding!!

    abstract val items: List<*>
    abstract val adapter: RecyclerView.Adapter<*>
    abstract val presenter: ListPresenter

    lateinit var myContext: Context
    lateinit var app: MyApp

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
        // when clicking on (+) FAB navigate to CreateDatabaseFragment
        b.addItem.setOnClickListener{
            view.findNavController().navigate(R.id.actionCreateDatabase)
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
}