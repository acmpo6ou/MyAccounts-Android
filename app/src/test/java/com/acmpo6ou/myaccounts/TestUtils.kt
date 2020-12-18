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

package com.acmpo6ou.myaccounts

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.acmpo6ou.myaccounts.core.Account
import com.github.javafaker.Faker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout


// This two extensions used to find a snackbar during tests
/**
 * @return a TextView if a snackbar is shown anywhere in the view hierarchy.
 *
 * NOTE: calling Snackbar.make() does not create a snackbar. Only calling #show() will create it.
 *
 * If the textView is not-null you can check its text.
 */
fun View.findSnackbarTextView(): TextView? {
    val possibleSnackbarContentLayout = findSnackbarLayout()?.getChildAt(0) as? SnackbarContentLayout
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


/**
 * Helper function that creates simple database map used in tests.
 *
 * @return created database map.
 */
fun getDatabaseMap(): Map<String, Account> {
    val account = Account(
            account="gmail",
            name="Tom",
            email="tom@gmail.com",
            password="123",
            date="01.01.1990",
            comment="My gmail account.",
    )
    return mapOf("gmail" to account)
}

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
fun randomIntExcept(exception: Int, start: Int=0, end: Int=20): Int{
    val faker = Faker()
    var res: Int
    while (true) {
        res = faker.number().numberBetween(start, end)
        if(res != exception){
            break
        }
    }
    return res
}

