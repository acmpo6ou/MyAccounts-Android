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

package com.acmpo6ou.myaccounts.core

import android.content.Context
import android.content.DialogInterface
import com.acmpo6ou.myaccounts.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Used to display dialog saying that the error occurred.
 *
 * @param[context] Activity where to display the dialog.
 * @param[title] title of error dialog.
 * @param[details] details about the error.
 */
fun errorDialog(context: Context, title: String, details: String) {
    MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setIcon(R.drawable.ic_error)
            .setNeutralButton("Ok"){ _: DialogInterface, _: Int -> }
            .setMessage(details)
            .show()
}

