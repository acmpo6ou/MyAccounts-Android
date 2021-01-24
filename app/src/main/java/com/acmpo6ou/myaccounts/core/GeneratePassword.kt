/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

import android.app.Dialog
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.acmpo6ou.myaccounts.R
import com.google.android.material.textfield.TextInputEditText

open class GeneratePassword(activity: AppCompatActivity,
                       pass1: TextInputEditText, pass2: TextInputEditText) {
    val dialog: Dialog = Dialog(activity)
    val generateButton: Button
    val cancelButton: Button
    val length: NumberPicker

    val digitsBox: CheckBox
    val lowerBox: CheckBox
    val upperBox: CheckBox
    val punctBox: CheckBox
    val checkBoxes: List<CheckBox>

    val digits = ('0'..'9').joinToString("")
    val lower = ('a'..'z').joinToString("")
    val upper = lower.toUpperCase()
    val punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    val allChars = listOf(digits, lower, upper, punctuation)

    init{
        dialog.setContentView(R.layout.generate_password)
        generateButton = dialog.findViewById(R.id.generateButton)
        cancelButton = dialog.findViewById(R.id.cancelButton)
        length = dialog.findViewById(R.id.passwordLength)

        // check boxes
        digitsBox = dialog.findViewById(R.id.numbersBox)
        lowerBox = dialog.findViewById(R.id.lowerLettersBox)
        upperBox = dialog.findViewById(R.id.upperLettersBox)
        punctBox = dialog.findViewById(R.id.punctBox)
        checkBoxes = listOf(digitsBox, lowerBox, upperBox, punctBox)

        dialog.setTitle("Generate password")
        length.value = 16 // set default length
        length.minValue = 8

        generateButton.setOnClickListener {
            // get all selected checkboxes
            val chars = mutableListOf<String>()
            for (box in checkBoxes){
                // add characters of corresponding check box if it is checked
                if (box.isChecked){
                    val index = checkBoxes.indexOf(box)
                    chars.add(allChars[index])
                }
            }

            // generate password and set it on password fields
            val password = genPass(length.value, chars)
            pass1.setText(password)
            pass2.setText(password)

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
     * @param[len] [Int] number that defines length of generated password.
     * @param[chars] [List] of [String] of characters from which password will be generated.
     * @return generated random password.
     */
    open fun genPass(len: Int, chars: List<String>): String {
        // here we generate the password using [len] parameter and strings
        // that are passed to genPass and packed to [chars]
        val password =  (1..len)
                .map{ chars.joinToString("").random() }
                .joinToString("")

        // because password generates randomly it not necessary will contain all characters that are
        // specified in [chars], so here we check that generated password contains at least one character
        // from each string specified in [chars] and if not we generate password again
        for(seq in chars){
            if (!(password hasoneof seq)){
                return genPass(len, chars)
            }
        }
        return password
    }
}

/**
 * Checks whether [String] on the left has at least one character from [String] on the right.
 *
 * @param[other] [String] on the right, i.e. the one from which we check characters.
 * @return [Boolean] value representing whether [String] on the left contains
 * at least one character from [String] on the right.
 */
infix fun String.hasoneof(other: String): Boolean {
    for(c in other){
        if ( c in this ){
            return true
        }
    }
    return false
}
