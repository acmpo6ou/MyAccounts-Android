/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.core.utils

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import com.acmpo6ou.myaccounts.R
import com.google.android.material.textfield.TextInputEditText
import com.shawnlin.numberpicker.NumberPicker
import java.security.SecureRandom
import java.util.*
import kotlin.streams.asSequence

/**
 * Dialog to generate password and fill [pass1] and [pass2] password fields with it.
 *
 * Dialog that contains number picker to choose password length and checkboxes to choose what
 * characters to use to generate password (i.e. digits, letters, etc.).
 */
open class GenPassDialog(
    context: Context,
    pass1: TextInputEditText,
    pass2: TextInputEditText,
) {
    val dialog = Dialog(context)
    val generateButton: Button
    val cancelButton: Button
    val length: NumberPicker

    val digitsBox: CheckBox
    val lowerBox: CheckBox
    val upperBox: CheckBox
    val punctBox: CheckBox
    private val checkBoxes: List<CheckBox>

    val digits = ('0'..'9').joinToString("")
    val lower = ('a'..'z').joinToString("")
    val upper = lower.uppercase(Locale.ROOT)
    val punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    val allChars = listOf(digits, lower, upper, punctuation)

    init {
        dialog.apply {
            setContentView(R.layout.generate_password)
            generateButton = findViewById(R.id.generateButton)
            cancelButton = findViewById(R.id.cancelButton)
            length = findViewById(R.id.passwordLength)

            // check boxes
            digitsBox = findViewById(R.id.numbersBox)
            lowerBox = findViewById(R.id.lowerLettersBox)
            upperBox = findViewById(R.id.upperLettersBox)
            punctBox = findViewById(R.id.punctBox)
        }
        checkBoxes = listOf(digitsBox, lowerBox, upperBox, punctBox)

        // set width of dialog to 90%
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)

        generateButton.setOnClickListener {
            // get all selected checkboxes
            val chars = mutableListOf<String>()
            for (box in checkBoxes) {
                // add characters of corresponding check box if it is checked
                if (box.isChecked) {
                    val index = checkBoxes.indexOf(box)
                    chars.add(allChars[index])
                }
            }

            // do not proceed if no checkboxes are checked
            if (chars.isNotEmpty()) {
                // generate password and set it on password fields
                val password = genPass(length.value, chars)
                pass1.setText(password)
                pass2.setText(password)
            }

            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Generates random password.
     *
     * @param[len] length of generated password.
     * @param[chars] list of characters from which password will be generated.
     * @return generated random password.
     */
    open fun genPass(len: Int, chars: List<String>): String {
        val source = chars.joinToString("")
        val password = SecureRandom()
            .ints(len.toLong(), 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")

        // because password generates randomly it not necessary will contain all characters that are
        // specified in [chars], so here we check that generated password contains at least one
        // character from each string specified in [chars] and if not, we generate password again
        for (seq in chars) {
            if (!(password hasoneof seq)) {
                return genPass(len, chars)
            }
        }
        return password
    }
}

/**
 * Checks whether [String] on the left has at least one character from [String] on the right.
 */
infix fun String.hasoneof(other: String): Boolean {
    for (c in other) if (c in this) return true
    return false
}
