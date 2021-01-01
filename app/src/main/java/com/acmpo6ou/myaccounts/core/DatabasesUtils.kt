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

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Base64
import androidx.fragment.app.Fragment
import com.acmpo6ou.myaccounts.AccountsActivity
import com.acmpo6ou.myaccounts.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

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

/**
 * Used to open databases by given Database instance.
 *
 * In particular opening database means reading content of corresponding .db file,
 * decrypting and deserializing it, then assigning deserialized database map to `data`
 * property of given Database.
 *
 * @param[database] Database instance with password, name and salt to open database.
 * @return same Database instance but with `data` property filled with deserialized
 * database map.
 */
fun openDatabaseUtil(database: Database, SRC_DIR: String): Database {
    val jsonStr = File("$SRC_DIR/${database.name}.db").readText()
    val data = decryptDatabaseUtil(jsonStr, database.password!!, database.salt!!)
    database.data = data
    return database
}

/**
 * Used to decrypt and deserialize encrypted json string to a database map.
 *
 * @param[jsonString] encrypted json string to decrypt.
 * @param[password] password for decryption.
 * @param[salt] salt for decryption.
 * @return decrypted database map.
 */
fun decryptDatabaseUtil(jsonString: String, password: String, salt: ByteArray):
        Map<String, Account> {
    // get key and validator
    val key = deriveKeyUtil(password, salt)
    val validator: Validator<String> = object : StringValidator {
        // this checks whether our encrypted json string is expired or not
        // in our app we don't care about expiration so we return Instant.MAX.epochSecond
        override fun getTimeToLive(): TemporalAmount {
            return Duration.ofSeconds(Instant.MAX.epochSecond)
        }
    }

    // decrypt and deserialize string
    val token = Token.fromString(jsonString)
    val decrypted = token.validateAndDecrypt(key, validator)
    return loadsUtil(decrypted)
}

/**
 * This method creates fernet key given password and salt.
 *
 * @param[password] key password.
 * @param[salt] salt for key.
 * @return created fernet key.
 */
@SuppressLint("NewApi")
fun deriveKeyUtil(password: String, salt: ByteArray): Key {
    val iterations = 100000
    val derivedKeyLength = 256
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val key = secretKeyFactory.generateSecret(spec).encoded

    // there are some differences in API versions:
    // Base64 from java.util is supported only from API 26 but we can use it in tests
    // without mocking
    // there is also Base64 from android.util which supported by all versions but
    // doesn't work in tests
    var strKey: String
    try{
        // our min sdk version is 25, so here we first try to use Base64 from java.utils
        // (this will work for android 8 or later and also during testing)
        strKey = java.util.Base64.getUrlEncoder().encodeToString(key)
    }catch (e: Error){
        // however it will not work on android 7, so here we use Base64 from android.util
        println(e.stackTraceToString())
        strKey = android.util.Base64.encodeToString(key, Base64.DEFAULT)
    }
    return Key(strKey)
}

/**
 * Used to deserialize json string to database map.
 *
 * @param[jsonStr] json string to deserialize.
 * @return when [jsonStr] is empty returns empty map, when it's not empty –
 * deserialized database map.
 */
fun loadsUtil(jsonStr: String): Map<String, Account>{
    var map = mapOf<String, Account>()
    if (jsonStr.isNotEmpty()){
        map = Json.decodeFromString(jsonStr)
    }
    return map
}

/**
 * Used to start AccountsActivity for given database.
 *
 * @param[index] index of database for which we want to start AccountsActivity.
 * @param[fragment] fragment which we use to start the activity.
 */
fun startDatabaseUtil(index: Int, fragment: Fragment) {
    val intent = Intent(fragment.context, AccountsActivity::class.java)
    intent.putExtra("databaseIndex", index)
    fragment.startActivity(intent)
}