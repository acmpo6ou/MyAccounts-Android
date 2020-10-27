package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.acmpo6ou.myaccounts.R

class EditDatabaseFragment : Fragment() {

    companion object {
        fun newInstance() = EditDatabaseFragment()
    }

    private lateinit var viewModel: EditDatabaseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_database_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditDatabaseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}