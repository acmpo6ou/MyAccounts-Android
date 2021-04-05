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

package com.acmpo6ou.myaccounts.core.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.acmpo6ou.myaccounts.AccountsActivity
import java.io.File
import kotlin.reflect.KProperty1

/**
 * Extension function to combine 2 LiveData properties into one.
 * Note: it's completely copied from StackOverflow.
 */
fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

/**
 * Function to get property from [instance] by [propertyName] string using reflection.
 * Note: it's completely copied from StackOverflow.
 *
 * @param[instance] instance to get property from.
 * @param[propertyName] string with property name.
 * @return property named [propertyName] got from [instance].
 */
@Suppress("UNCHECKED_CAST")
fun <R> getProperty(instance: Any, propertyName: String): R {
    val property =
        instance::class.members.first { it.name == propertyName } as KProperty1<Any, *>
    return property.get(instance) as R
}

/**
 * Used to start AccountsActivity for given database.
 * @param[index] index of database for which we want to start AccountsActivity.
 */
fun startDatabaseUtil(index: Int, context: Context) {
    val intent = Intent(context, AccountsActivity::class.java)
    intent.putExtra("databaseIndex", index)
    context.startActivity(intent)
}

// next 2 extension functions are used to get file name from Uri
// Note: they are completely copied from StackOverflow

fun Context.getFileName(uri: Uri): String? {
    when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> {
            val filePath = uri.path
            if (!filePath.isNullOrEmpty()) {
                return File(filePath).name
            }
        }

        ContentResolver.SCHEME_CONTENT -> {
            return contentResolver.getCursorContent(uri)
        }
    }

    return null
}

private fun ContentResolver.getCursorContent(uri: Uri): String? = kotlin.runCatching {
    query(uri, null, null, null, null)?.use { cursor ->
        val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            cursor.getString(nameColumnIndex)
        } else null
    }
}.getOrNull()
