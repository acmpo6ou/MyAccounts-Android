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

package com.acmpo6ou.myaccounts.ui.about

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AboutFragment : Fragment() {
    lateinit var myView: View
    lateinit var mainActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myView = requireView()
        mainActivity = requireActivity()

        // set app version
        val versionLabel = myView.findViewById<TextView>(R.id.versionLabel)
        val versionStr =
                String.format(requireActivity().resources.getString(R.string.version),
                              BuildConfig.VERSION_NAME)
        versionLabel.text = versionStr

        configureTabLayout()
    }

    /**
     * This method connects TabLayout, AboutAdapter and ViewPager2.
     */
    private fun configureTabLayout() {
        val tabLayout = myView.findViewById<TabLayout>(R.id.tabLayout)
        val adapter = AboutAdapter(this)
        val pager = myView.findViewById<ViewPager2>(R.id.aboutPager)

        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab: TabLayout.Tab, i: Int ->
            // set tab titles
            when(i){
                0 -> tab.text = "About" // using hardcoded string because it shouldn't be translated
                1 -> tab.text = mainActivity.resources.getString(R.string.license)
                else -> tab.text = mainActivity.resources.getString(R.string.credits)
            }
        }.attach()
    }
}
