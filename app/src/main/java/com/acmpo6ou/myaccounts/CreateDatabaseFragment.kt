package com.acmpo6ou.myaccounts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CreateDatabaseFragment : Fragment() {

    companion object {
        fun newInstance() = CreateDatabaseFragment()
    }

    private lateinit var viewModel: CreateDatabaseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_database_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateDatabaseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}