package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.acmpo6ou.myaccounts.R

class EditAccountFragment : Fragment() {

    companion object {
        fun newInstance() = EditAccountFragment()
    }

    private lateinit var viewModel: EditAccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_edit_account_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditAccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

}