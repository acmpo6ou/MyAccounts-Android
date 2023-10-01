/*
 * Copyright (c) 2020-2023. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts

import android.view.Menu
import android.view.View
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowPopupMenu

/**
 * Simulates selecting an item in navigation drawer.
 *
 * @param[id] item id.
 * @param[view] system under test.
 */
fun selectNavigationItem(id: Int, view: NavigationView.OnNavigationItemSelectedListener) {
    // here we using try-catch to avoid UninitializedPropertyAccessException
    // that occurs because of view bindings
    try {
        view.onNavigationItemSelected(RoboMenuItem(id))
    } catch (e: UninitializedPropertyAccessException) {
    }
}

/**
 * Clicks on the popup menu item.
 *
 * @param[itemLayout] layout containing 3 dots to open the popup menu.
 * @param[itemId] id of the item we want to click.
 */
fun clickMenuItem(itemLayout: View, itemId: Int) {
    // click on 3 dots to display popup menu
    val dotsMenu = itemLayout.findViewById<TextView>(R.id.dots_menu)
    dotsMenu.performClick()

    // find the popup menu and click on the item
    val menu = ShadowPopupMenu.getLatestPopupMenu().menu
    menu.performIdentifierAction(itemId, Menu.FLAG_ALWAYS_PERFORM_CLOSE)
}
