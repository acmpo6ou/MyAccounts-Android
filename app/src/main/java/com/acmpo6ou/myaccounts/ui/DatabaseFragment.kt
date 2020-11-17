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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.DatabasesAdapterInter
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import kotlinx.android.synthetic.main.fragment_database_list.*

/**
 * A fragment representing a list of Databases.
 */
class DatabaseFragment(
        override val adapter: DatabasesAdapterInter,
        val presenter: DatabasesPresenterInter
) : Fragment(), DatabaseFragmentInter {

    val EXPORT_RC = 101
    val layoutManager = LinearLayoutManager(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_database_list, container, false)

        // Set the adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // when clicking on (+) FAB navigate to CreateDatabaseFragment
        addDatabase.setOnClickListener{
            view.findNavController().navigate(R.id.createDatabaseFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        when(requestCode) {
            EXPORT_RC -> presenter.exportDatabase(data?.data.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
                adapter: DatabasesAdapterInter,
                presenter: DatabasesPresenterInter
        ) = DatabaseFragment(adapter, presenter)
    }
}