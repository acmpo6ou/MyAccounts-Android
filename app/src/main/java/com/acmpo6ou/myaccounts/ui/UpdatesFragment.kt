package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.acmpo6ou.myaccounts.R

class UpdatesFragment : Fragment() {

    companion object {
        fun newInstance() = UpdatesFragment()
    }

    private lateinit var viewModel: UpdatesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.updates_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UpdatesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}