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

package com.acmpo6ou.myaccounts

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.acmpo6ou.myaccounts.database.databases_list.Account
import com.github.javafaker.Faker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowPopupMenu

// This two extensions used to find a snackbar during tests
/**
 * NOTE: calling Snackbar.make() does not create a snackbar. Only calling #show() will create it.
 * If the textView is not-null you can check its text.
 * @return a TextView if a snackbar is shown anywhere in the view hierarchy.
 */
fun View.findSnackbarTextView(): TextView? {
    val possibleSnackbarContentLayout =
        findSnackbarLayout()?.getChildAt(0) as? SnackbarContentLayout
    return possibleSnackbarContentLayout
        ?.getChildAt(0) as? TextView
}

private fun View.findSnackbarLayout(): Snackbar.SnackbarLayout? {
    when (this) {
        is Snackbar.SnackbarLayout -> return this
        !is ViewGroup -> return null
    }
    // otherwise traverse the children

    // the compiler needs an explicit assert that `this` is an instance of ViewGroup
    this as ViewGroup

    (0 until childCount).forEach { i ->
        val possibleSnackbarLayout = getChildAt(i).findSnackbarLayout()
        if (possibleSnackbarLayout != null) return possibleSnackbarLayout
    }
    return null
}

// this is where we will create delete and edit databases during testing
// /dev/shm/ is a fake in-memory file system
const val accountsDir = "/dev/shm/accounts/"
const val SRC_DIR = "$accountsDir/src/"
val salt = "0123456789abcdef".toByteArray() // 16 bytes of salt

val account = Account(
    accountName = "gmail",
    username = "Tom",
    email = "tom@gmail.com",
    password = "123",
    date = "01.01.1990",
    comment = "My gmail account."
)
val databaseMap = mutableMapOf("gmail" to account.copy())

/**
 * Helper method to clone the mutable map.
 *
 * We need it because the map is *mutable* and changes made to the map will persist between
 * tests.
 */
fun MutableMap<String, Account>.copy() = this.toMap().toMutableMap()

/**
 * Helper function that generates random Int in specified range, but excluding number
 * that is specified in parameter [exception].
 *
 * Example:
 * >>> randomIntExcept(2)
 * will generate any number between 0 and 20 except 2.
 *
 * @param[exception] number to exclude from generation.
 * @param[start] left bound of number range.
 * @param[end] right bound of number range.
 * @return generated random number.
 */
fun randomIntExcept(exception: Int, start: Int = 0, end: Int = 20): Int {
    val faker = Faker()
    var res: Int

    while (true) {
        res = faker.number().numberBetween(start, end)
        if (res != exception) {
            break
        }
    }
    return res
}

// shortcut
fun Faker.str(): String = this.lorem().sentence()

/**
 * Helper method to simulate selecting an item in navigation drawer.
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
 * Helper method to click on the popup menu item.
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

/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.Theme_MyAccounts_NoActionBar,
    crossinline action: Fragment.() -> Unit = {}
): T {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltActivityForTest::class.java
        )
    ).putExtra(FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)

    var fragment: Fragment? = null
    ActivityScenario.launch<HiltActivityForTest>(startActivityIntent).onActivity { activity ->
        fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment!!.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment!!, "")
            .commitNow()

        fragment!!.action()
    }
    return fragment as T
}

/**
 * Used in tests to get measured and laid out recyclerview.
 * It finds recycler, measures and lays it out, so that later we can obtain its items.
 */
fun Fragment.getRecycler(): RecyclerView {
    val recycler: RecyclerView = this.view!!.findViewById(R.id.itemsList)
    recycler.measure(0, 0)
    recycler.layout(0, 0, 100, 10000)
    return recycler
}
